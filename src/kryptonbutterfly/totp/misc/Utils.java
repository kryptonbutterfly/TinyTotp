package kryptonbutterfly.totp.misc;

import static kryptonbutterfly.math.utils.range.Range.*;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import kryptonbutterfly.functions.bool_.BoolToIntFunction;
import kryptonbutterfly.math.vector._int.Vec2i;
import kryptonbutterfly.monads.failable.Failable;
import kryptonbutterfly.monads.opt.Opt;

public class Utils
{
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
		return scale(factor, rawImage);
	}
	
	private static BufferedImage scale(float factor, BufferedImage rawImage)
	{
		final var transform = new AffineTransform();
		transform.scale(factor, factor);
		return new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC)
			.filter(rawImage, null);
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
}