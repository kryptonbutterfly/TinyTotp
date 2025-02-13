package kryptonbutterfly.totp.ui.components;

import static java.awt.RenderingHints.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.JPanel;

import kryptonbutterfly.math.vector._int.Vec2i;
import kryptonbutterfly.totp.TotpConstants;
import kryptonbutterfly.totp.misc.Assets;
import kryptonbutterfly.totp.misc.Rect;

@SuppressWarnings("serial")
public class ComboIcon extends JPanel implements TotpConstants
{
	/**
	 * @formatter:off
	 */
	private static final Map<?, ?> HINTS = Map.of(
		KEY_ALPHA_INTERPOLATION,	VALUE_ALPHA_INTERPOLATION_QUALITY,
		KEY_ANTIALIASING,			VALUE_ANTIALIAS_ON,
		KEY_COLOR_RENDERING,		VALUE_COLOR_RENDER_QUALITY,
		KEY_INTERPOLATION,			VALUE_INTERPOLATION_BICUBIC,
		KEY_RENDERING,				VALUE_RENDER_QUALITY);
	/**
	 * @formatter:on
	 */
	
	private Color					backgroundColor	= null;
	private BufferedImage			issuerIcon		= null;
	private BufferedImage			userIcon		= null;
	private final Vec2i				size;
	
	private float	issuerAspect	= 1;
	private float	userAspect		= 1;
	
	public ComboIcon()
	{
		this.size = ICON_AREA_SIZE.add(ICON_PADDING.size());
		final var dims = new Dimension(size.x(), size.y());
		setSize(dims);
		setMinimumSize(dims);
		setMaximumSize(dims);
		setPreferredSize(dims);
	}
	
	public void setIssuerIcon(BufferedImage issuerIcon)
	{
		this.issuerIcon = issuerIcon;
		if (issuerIcon != null)
			this.issuerAspect = ((float) issuerIcon.getWidth()) / issuerIcon.getHeight();
		repaint();
	}
	
	public BufferedImage getIssuerIcon()
	{
		return this.issuerIcon;
	}
	
	public void setUserIcon(BufferedImage userIcon)
	{
		this.userIcon = userIcon;
		if (userIcon != null)
			this.userAspect = ((float) userIcon.getWidth() / userIcon.getHeight());
		repaint();
	}
	
	public BufferedImage getUserIcon()
	{
		return this.userIcon;
	}
	
	public void setIconBG(Color bgColor)
	{
		this.backgroundColor = bgColor;
		repaint();
	}
	
	public Color getIconBG()
	{
		return this.backgroundColor;
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		
		final int	offX	= ICON_PADDING.left();
		final int	offY	= ICON_PADDING.bottom();
		
		if (g instanceof Graphics2D g2)
			g2.setRenderingHints(HINTS);
		
		g.setColor(super.getBackground());
		g.fillRect(0, 0, size.x(), size.y());
		if (this.backgroundColor != null)
		{
			final int	width	= ICON_AREA_SIZE.x();
			final int	height	= ICON_AREA_SIZE.y();
			
			g.setColor(this.backgroundColor);
			g.fillOval(0, 0, ICON_PADDING.left() * 2, ICON_PADDING.bottom() * 2);
			g.fillOval(width, 0, ICON_PADDING.right() * 2, ICON_PADDING.bottom() * 2);
			g.fillOval(width, height, ICON_PADDING.right() * 2, ICON_PADDING.top() * 2);
			g.fillOval(0, height, ICON_PADDING.left() * 2, ICON_PADDING.bottom() * 2);
			g.fillRect(0, ICON_PADDING.bottom(), size.x(), height);
			g.fillRect(ICON_PADDING.left(), 0, width, size.y());
		}
		
		{
			final var issuer = issuerIcon != null ? issuerIcon : Assets.MISSING_ICON_IMG;
			
			final var rect = new Rect(ICON_SIZE);
			if (issuerAspect > 1)
				rect.scaleHeight(1 / issuerAspect).moveY((ICON_SIZE.y() - rect.height) / 2);
			else
				rect.scaleWidth(issuerAspect).moveX((ICON_SIZE.x() - rect.width) / 2);
			rect.moveX(offX)
				.moveY(offY);
			
			g.drawImage(
				issuer,
				rect.x1(),
				rect.y1(),
				rect.x2(),
				rect.y2(),
				0,
				0,
				issuer.getWidth(),
				issuer.getHeight(),
				this);
		}
		
		if (userIcon != null)
		{
			final var rect = new Rect(ICON_USER_SIZE);
			if (userAspect > 1)
				rect.scaleHeight(1 / userAspect).moveY((ICON_USER_SIZE.y() - rect.height) / 2);
			else
				rect.scaleWidth(userAspect).moveX((ICON_USER_SIZE.x() - rect.width) / 2);
			rect.moveX(offX)
				.moveY(offY)
				.moveX(ICON_USER_OFFSET.x());
			
			g.drawImage(
				userIcon,
				rect.x1(),
				rect.y1(),
				rect.x2(),
				rect.y2(),
				0,
				0,
				userIcon.getWidth(),
				userIcon.getHeight(),
				this);
		}
	}
}
