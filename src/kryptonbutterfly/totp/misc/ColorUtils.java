package kryptonbutterfly.totp.misc;

import java.awt.Color;

public interface ColorUtils
{
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
}