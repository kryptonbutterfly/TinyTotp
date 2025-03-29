package kryptonbutterfly.totp.misc;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import lombok.SneakyThrows;

public class Assets
{
	private static final Color	DARK	= new Color(0x505050);
	private static final Color	LIGHT	= new Color(0xDDDDDD);
	
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
	
	public static final BufferedImage MARKER_MASK = loadImage("/assets/marker-mask.png");
	
	public static final BufferedImage MISSING_ICON_IMG = loadImage("/assets/missingIcon.png");
	
	private static final ImageIcon	QR_ICON_LIGHT_16, QR_ICON_DARK_16;
	private static final ImageIcon	EDIT_LIGHT, EDIT_DARK;
	private static final ImageIcon	DELETE_LIGHT, DELETE_DARK;
	private static final ImageIcon	CAMERA_LIGHT, CAMERA_DARK;
	private static final ImageIcon	CLIPBOARD_DARK, CLIPBOARD_LIGHT;
	static
	{
		final var qr_mask = loadImage("/assets/qr-mask.png");
		QR_ICON_DARK_16		= new ImageIcon(Utils.fromMask(qr_mask, DARK));
		QR_ICON_LIGHT_16	= new ImageIcon(Utils.fromMask(qr_mask, LIGHT));
		
		final var edit_mask = loadImage("/assets/edit-mask.png");
		EDIT_DARK	= new ImageIcon(Utils.fromMask(edit_mask, DARK));
		EDIT_LIGHT	= new ImageIcon(Utils.fromMask(edit_mask, LIGHT));
		
		final var delete_mask = loadImage("/assets/delete-mask.png");
		DELETE_DARK		= new ImageIcon(Utils.fromMask(delete_mask, DARK));
		DELETE_LIGHT	= new ImageIcon(Utils.fromMask(delete_mask, LIGHT));
		
		final var camera_mask = loadImage("/assets/camera-mask.png");
		CAMERA_DARK		= new ImageIcon(Utils.fromMask(camera_mask, DARK));
		CAMERA_LIGHT	= new ImageIcon(Utils.fromMask(camera_mask, LIGHT));
		
		final var clipboard_mask = loadImage("/assets/clipboard-mask.png");
		CLIPBOARD_DARK	= new ImageIcon(Utils.fromMask(clipboard_mask, DARK));
		CLIPBOARD_LIGHT	= new ImageIcon(Utils.fromMask(clipboard_mask, LIGHT));
	}
	
	public static final ImageIcon COLOR_PICKER = loadIcon("/assets/color-picker.png");
	
	public static final BufferedImage	ICON	= loadImage("/assets/icon.png");
	public static final ImageIcon		ICON_48	= new ImageIcon(
		ICON.getScaledInstance(48, 48, BufferedImage.SCALE_SMOOTH));
	
	public static ImageIcon getCameraByBackground(Color bgColor)
	{
		return Utils.getContrasting(bgColor, CAMERA_LIGHT, CAMERA_DARK);
	}
	
	public static ImageIcon getQr16ByBackground(Color bgColor)
	{
		return Utils.getContrasting(bgColor, QR_ICON_LIGHT_16, QR_ICON_DARK_16);
	}
	
	public static ImageIcon getEditByBackground(Color bgColor)
	{
		return Utils.getContrasting(bgColor, EDIT_LIGHT, EDIT_DARK);
	}
	
	public static ImageIcon getDeleteByBackground(Color bgColor)
	{
		return Utils.getContrasting(bgColor, DELETE_LIGHT, DELETE_DARK);
	}
	
	public static ImageIcon getClipboardByBackground(Color bgColor)
	{
		return Utils.getContrasting(bgColor, CLIPBOARD_LIGHT, CLIPBOARD_DARK);
	}
}