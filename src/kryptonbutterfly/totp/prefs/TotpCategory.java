package kryptonbutterfly.totp.prefs;

import static kryptonbutterfly.math.utils.range.Range.*;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import com.google.gson.annotations.Expose;

import kryptonbutterfly.cache.LRUCache;
import kryptonbutterfly.totp.misc.Assets;
import kryptonbutterfly.totp.misc.HtmlColors;

public final class TotpCategory implements Cloneable
{
	@SuppressWarnings("removal")
	private static final LRUCache<Color, ImageIcon> iconCache = new LRUCache<>(TotpCategory::generateColoredIcon, 0xFF);
	
	public TotpCategory()
	{}
	
	@Expose
	public String name = null;
	
	@Expose
	public Color color = HtmlColors.LightGreen.color();
	
	private transient Color		last	= null;
	private transient ImageIcon	icon	= Assets.ALPHA_24x24;
	
	public ImageIcon getIcon()
	{
		if (color == last)
			return icon;
		
		last	= color;
		icon	= generateColoredIcon(color);
		return icon;
	}
	
	public static ImageIcon getColoredIcon(Color color)
	{
		return iconCache.get(color);
	}
	
	private static ImageIcon generateColoredIcon(Color color)
	{
		final int colorInt = color.getRGB() & 0xFF_FFFF;
		
		final var	mask	= Assets.MARKER_MASK;
		final var	image	= new BufferedImage(mask.getWidth(), mask.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		final var	yRange	= range(mask.getHeight());
		final var	xRange	= range(mask.getWidth());
		for (int y : yRange)
			for (int x : xRange)
			{
				final int brightness = mask.getRGB(x, y) & 0xFF;
				image.setRGB(x, y, colorInt | brightness << 24);
			}
		return new ImageIcon(image);
	}
	
	@Override
	public TotpCategory clone()
	{
		final var res = new TotpCategory();
		res.name	= name;
		res.color	= color;
		return res;
	}
}