package kryptonbutterfly.totp.misc;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import lombok.SneakyThrows;

public class Assets
{
	@SneakyThrows
	private static BufferedImage loadImage(String image)
	{
		try (InputStream iStream = Assets.class.getResourceAsStream(image))
		{
			var img = ImageIO.read(iStream);
			if (img != null)
				return img;
			throw new IllegalArgumentException(
				"The asset \"%s\" is not a supported image type!".formatted(image));
		}
	}
	
	private static ImageIcon loadIcon(String image)
	{
		return new ImageIcon(loadImage(image));
	}
	
	private static final ImageIcon scaleIcon(Image img, int size, int hints)
	{
		return new ImageIcon(img.getScaledInstance(size, size, hints));
	}
	
	// public static final Image APP_ICON = loadImage("/assets/icon.png");
	public static final Image ALPHA = loadImage("/assets/transparent.png");
	
	public static final BufferedImage MARKER_MASK = loadImage("/assets/marker-mask.png");
	
	public static final ImageIcon	ALPHA_24x24	= scaleIcon(ALPHA, 24, Image.SCALE_FAST);
	public static final ImageIcon	ALPHA_32x32	= scaleIcon(ALPHA, 32, Image.SCALE_FAST);
	public static final ImageIcon	ALPHA_64x64	= scaleIcon(ALPHA, 64, Image.SCALE_FAST);
	
	public static final ImageIcon MISSING_ICON = scaleIcon(
		loadImage("/assets/missingIcon.png"),
		64,
		Image.SCALE_SMOOTH);
	
	public static final ImageIcon	QR_ICON_LIGHT_16	= loadIcon("/assets/qr-icon-light_16.png");
	public static final ImageIcon	QR_ICON_DARK_16		= loadIcon("/assets/qr-icon-dark_16.png");
	
	public static final ImageIcon	QR_ICON_LIGHT	= loadIcon("/assets/qr-icon-light.png");
	public static final ImageIcon	QR_ICON_DARK	= loadIcon("/assets/qr-icon-dark.png");
	
	public static final ImageIcon	EDIT_LIGHT	= loadIcon("/assets/edit-light_24.png");
	public static final ImageIcon	EDIT_DARK	= loadIcon("/assets/edit-dark_24.png");
	
	public static final ImageIcon	DELETE_LIGHT	= loadIcon("/assets/delete-light_24.png");
	public static final ImageIcon	DELETE_DARK		= loadIcon("/assets/delete-dark_24.png");
	
	public static final ImageIcon	CAMERA_LIGHT	= loadIcon("/assets/camera-light.png");
	public static final ImageIcon	CAMERA_DARK		= loadIcon("/assets/camera-dark.png");
	
	public static final ImageIcon COLOR_PICKER = loadIcon("/assets/color-picker.png");
	
	public static final BufferedImage ICON = loadImage("/assets/icon.png");
	
	public static ImageIcon getCameraByBackground(Color bgColor)
	{
		return Utils.getContrasting(bgColor, CAMERA_LIGHT, CAMERA_DARK);
	}
	
	public static ImageIcon getQr16ByBackground(Color bgColor)
	{
		return Utils.getContrasting(bgColor, QR_ICON_LIGHT_16, QR_ICON_DARK_16);
	}
	
	public static ImageIcon getQrByBackground(Color bgColor)
	{
		return Utils.getContrasting(bgColor, QR_ICON_LIGHT, QR_ICON_DARK);
	}
	
	public static ImageIcon getEditByBackground(Color bgColor)
	{
		return Utils.getContrasting(bgColor, EDIT_LIGHT, EDIT_DARK);
	}
	
	public static ImageIcon getDeleteByBackground(Color bgColor)
	{
		return Utils.getContrasting(bgColor, DELETE_LIGHT, DELETE_DARK);
	}
}