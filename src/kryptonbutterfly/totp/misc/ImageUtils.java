package kryptonbutterfly.totp.misc;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import kryptonbutterfly.math.vector._int.Vec2i;

public class ImageUtils
{
	public static final BufferedImage scaleDownToMax(BufferedImage rawImage, Vec2i maxSize)
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
	
	private static final BufferedImage scale(float factor, BufferedImage rawImage)
	{
		final var transform = new AffineTransform();
		transform.scale(factor, factor);
		return new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC)
			.filter(rawImage, null);
	}
}