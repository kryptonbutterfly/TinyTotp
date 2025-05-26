package kryptonbutterfly.totp.ui.misc;

import static java.awt.RenderingHints.*;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.JLabel;

import kryptonbutterfly.totp.misc.Margin;

@SuppressWarnings("serial")
public final class AutoScalingImage extends JLabel
{
	private BufferedImage	image	= null;
	private Margin			margin	= new Margin(0);
	
	/**
	 * @formatter:off
	 */
	private static final Map<?, ?> HINTS = Map.of(
		KEY_ALPHA_INTERPOLATION,	VALUE_ALPHA_INTERPOLATION_QUALITY,
		KEY_ANTIALIASING, 			VALUE_ANTIALIAS_ON,
		KEY_COLOR_RENDERING,		VALUE_COLOR_RENDER_QUALITY,
		KEY_INTERPOLATION,			VALUE_INTERPOLATION_BICUBIC,
		KEY_RENDERING,				VALUE_RENDER_QUALITY);
	/**
	 * @formatter:on
	 */
	
	public AutoScalingImage(Margin margin)
	{
		this.margin = margin;
	}
	
	public void setMargin(Margin margin)
	{
		this.margin = margin;
		repaint();
	}
	
	public void setIcon(BufferedImage image)
	{
		this.image = image;
		repaint();
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		if (g instanceof Graphics2D g2)
			g2.setRenderingHints(HINTS);
		
		final Rectangle bounds;
		{
			final int	x		= margin.left();
			final int	y		= margin.top();
			final int	width	= getWidth() - x - margin.right();
			final int	height	= getHeight() - y - margin.bottom();
			bounds = new Rectangle(x, y, width, height);
		}
		
		g.setColor(super.getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if (image == null)
			return;
		
		final float aspect = ((float) bounds.width) / bounds.height;
		
		final int	iconWidth	= image.getWidth();
		final int	iconHeight	= image.getHeight();
		final float	iconAspect	= ((float) iconWidth) / iconHeight;
		
		final int width, height;
		if (aspect > iconAspect)
		{
			height	= bounds.height;
			width	= (int) (bounds.width / aspect * iconAspect);
		}
		else
		{
			width	= bounds.width;
			height	= (int) (bounds.height * aspect / iconAspect);
		}
		
		final int	x	= bounds.x + (int) ((bounds.width - width) * 0.5F);
		final int	y	= bounds.y + (int) ((bounds.height - height) * 0.5F);
		
		g.drawImage(
			image,
			x,
			y,
			x + width,
			y + height,
			0,
			0,
			image.getWidth(),
			image.getHeight(),
			this);
	}
}
