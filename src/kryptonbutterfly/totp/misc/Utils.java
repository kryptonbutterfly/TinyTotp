package kryptonbutterfly.totp.misc;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import kryptonbutterfly.math.vector._int.Vec2i;

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
}