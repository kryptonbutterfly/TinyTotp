package kryptonbutterfly.totp.misc;

import static kryptonbutterfly.math.utils.range.Range.*;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.Map;

import org.apache.commons.net.ntp.NTPUDPClient;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import kryptonbutterfly.functions.bool_.BoolToIntFunction;
import kryptonbutterfly.math.utils.Max;
import kryptonbutterfly.math.vector._int.Vec2i;
import kryptonbutterfly.monads.failable.Failable;
import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.totp.TinyTotp;

public class Utils
{
	private static final int	ALPHA_MASK	= 0xFF00_0000;
	private static final int	RGB_MASK	= ~ALPHA_MASK;
	
	private static Opt<Long> ntpTimeOffset = Opt.empty();
	
	private Utils()
	{}
	
	public static boolean isBright(Color bgColor)
	{
		return (bgColor.getRed() + bgColor.getGreen() + bgColor.getBlue()) / 3 > 0x7F;
	}
	
	public static <E> E getContrasting(Color bgColor, E light, E dark)
	{
		if (isBright(bgColor))
			return dark;
		return light;
	}
	
	public static BufferedImage scaleDownToMax(BufferedImage rawImage, Vec2i maxSize)
	{
		final int	maxWidth		= maxSize.x();
		final int	maxHeight		= maxSize.y();
		final float	targetAspect	= 1F * maxWidth / maxHeight;
		final float	imageAspect		= 1F * rawImage.getWidth() / rawImage.getHeight();
		
		final float factor;
		
		if (targetAspect > imageAspect)
		{
			if (rawImage.getHeight() <= maxHeight)
				return rawImage;
			factor = 1F * maxHeight / rawImage.getHeight();
		}
		else
		{
			if (rawImage.getWidth() <= maxWidth)
				return rawImage;
			factor = 1F * maxWidth / rawImage.getWidth();
		}
		return new AffineTransformOp(
			new AffineTransform(new float[] { factor, 0F, 0F, factor }),
			AffineTransformOp.TYPE_BICUBIC).filter(rawImage, null);
	}
	
	public static final BufferedImage mirror(BufferedImage src)
	{
		final int	width	= src.getWidth();
		final int	height	= src.getHeight();
		final var	result	= new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		
		for (final int y : range(height))
			for (final int x : range(width))
				result.setRGB(width - x - 1, y, src.getRGB(x, y));
			
		return result;
	}
	
	public static final Opt<BufferedImage> generateQr(
		String data,
		Color dark,
		Color light,
		int size,
		ErrorCorrectionLevel level)
	{
		final BoolToIntFunction	colorConverter	= bit -> bit ? dark.getRGB() : light.getRGB();
		final var				sizeRange		= range(size);
		
		return Failable.attempt(
			() -> new MultiFormatWriter().encode(
				data,
				BarcodeFormat.QR_CODE,
				size,
				size,
				Map.of(EncodeHintType.ERROR_CORRECTION, level.name())))
			.toOpt(e -> e.printStackTrace())
			.map(matrix ->
			{
				final var image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
				for (int y : sizeRange)
					for (int x : sizeRange)
						image.setRGB(x, y, colorConverter.apply(matrix.get(x, y)));
				return image;
			});
	}
	
	public static final BufferedImage inverse(BufferedImage original)
	{
		final var	rangeY	= range(original.getHeight());
		final var	rangeX	= range(original.getWidth());
		final var	result	= new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (final int y : rangeY)
			for (final int x : rangeX)
				result.setRGB(x, y, original.getRGB(x, y) ^ 0x00FF_FFFF);
		return result;
	}
	
	public static final BufferedImage fromMask(BufferedImage mask, Color color)
	{
		final int	colorInt	= color.getRGB() & RGB_MASK;
		final var	yRange		= range(mask.getHeight());
		final var	xRange		= range(mask.getWidth());
		final var	image		= new BufferedImage(xRange.stop, yRange.stop, BufferedImage.TYPE_INT_ARGB);
		for (final int y : yRange)
			for (final int x : xRange)
			{
				final int brightness = mask.getRGB(x, y) & 0xFF;
				image.setRGB(x, y, colorInt | brightness << 24);
			}
		return image;
	}
	
	public static Vec2i max(Vec2i first, Vec2i... other)
	{
		int	maxX	= first.x();
		int	maxY	= first.y();
		for (final var o : other)
		{
			maxX	= Max.max(maxX, o.x());
			maxY	= Max.max(maxY, o.y());
		}
		return new Vec2i(maxX, maxY);
	}
	
	public static long getNtpTimeOffset(String address) throws IOException
	{
		try (final var timeClient = new NTPUDPClient())
		{
			final var inetAddress = InetAddress.getByName(address);
			timeClient.setDefaultTimeout(Duration.ofMillis(TinyTotp.config.ntpTimeoutMillis));
			final var timeInfo = timeClient.getTime(inetAddress);
			timeInfo.computeDetails();
			final long offset = timeInfo.getOffset();
			ntpTimeOffset = Opt.of(offset);
			return offset;
		}
	}
	
	public static long getUnixTime()
	{
		final long time = System.currentTimeMillis();
		if (!TinyTotp.config.useTimeServer)
			return time;
		
		if (!ntpTimeOffset.isPresent())
			try
			{
				return time + getNtpTimeOffset(TinyTotp.config.ntpServer);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		
		return time + ntpTimeOffset.get(() -> 0L);
	}
}