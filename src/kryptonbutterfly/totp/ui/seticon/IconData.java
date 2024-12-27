package kryptonbutterfly.totp.ui.seticon;

import java.awt.Color;
import java.awt.image.BufferedImage;

public record IconData(
	String issuerName,
	BufferedImage issuerImage,
	String userName,
	BufferedImage userImage,
	Color bgColor)
{}
