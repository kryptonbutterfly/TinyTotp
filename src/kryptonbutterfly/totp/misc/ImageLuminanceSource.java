package kryptonbutterfly.totp.misc;

import static kryptonbutterfly.math.utils.range.Range.*;

import java.awt.image.BufferedImage;

import com.google.zxing.LuminanceSource;

import kryptonbutterfly.math.utils.limit.LimitInt;

public final class ImageLuminanceSource extends LuminanceSource
{
	private final BufferedImage image;
	
	public ImageLuminanceSource(BufferedImage image)
	{
		this(image, image.getWidth(), image.getHeight());
	}
	
	public ImageLuminanceSource(BufferedImage image, int width, int height)
	{
		super(width, height);
		if (image.getType() == BufferedImage.TYPE_BYTE_GRAY)
			this.image = image;
		else
		{
			this.image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
			
			for (final int y : range(height))
				for (final int x : range(width))
					this.image.setRGB(x, y, toLuminance(image.getRGB(x, y)));
		}
	}
	
	private static final int toLuminance(int argb)
	{
		final int alpha = (argb >>> 24) & 0xFF;
		if (alpha == 0)
			return 0xFF;
		
		final int	red		= (argb >> 16) & 0xFF;
		final int	green	= (argb >> 8) & 0xFF;
		final int	blue	= argb & 0xFF;
		return (306 * red + 601 * green + 117 * blue + 0x200) >> 10;
	}
	
	@Override
	public byte[] getRow(int y, byte[] row)
	{
		if (LimitInt.inRange(0, y, getHeight() - 1))
			throw new IllegalArgumentException("Requested row is outside the image: " + y);
		
		final int width = getWidth();
		
		if (row == null || row.length < width)
			row = new byte[width];
		image.getRaster().getDataElements(0, y, width, 1, row);
		
		return row;
	}
	
	@Override
	public byte[] getMatrix()
	{
		final int	width	= getWidth();
		final int	height	= getHeight();
		final var	matrix	= new byte[width * height];
		image.getRaster().getDataElements(0, 0, width, height, matrix);
		return matrix;
	}
}
