package d.t.totp;

import java.util.Random;

import de.tinycodecrank.math.vector._int.Vec2i;

public interface TotpConstants
{
	public static final Random FAST_RANDOM = new Random();
	
	public static final String	PROGRAM_DOCK_NAME	= "tiny TOTP";
	public static final String	PROGRAM_NAME		= "TinyTotp";
	public static final String	ORG_NAME			= "tinycodecrank";
	
	public static final String	PNG		= "png";
	public static final String	SHA1	= "SHA1";
	
	public static final Vec2i	CACHED_IMAGE_MAX_SIZE	= new Vec2i(0x100, 0x100);
	public static final Vec2i	ICON_WIDTH				= new Vec2i(64, 64);
	
	public static final int	COMPONENT_MIN_HEIGHT	= 64;
	public static final int	COMPONENT_MAX_HEIGHT	= 96;
	
	public static final int	CATEGORY_MAX_HEIGHT		= 48;
	public static final int	CATEGORY_MARKER_WIDTH	= 7;
	
	public static final String	PASSWORD_TO_KEY_ALGO	= "PBKDF2WithHmacSHA256";
	public static final int		AES_ITERATION_COUNT		= 0x10000;
	public static final int		AES_KEY_LENGTH			= 0x100;
	public static final String	AES						= "AES";
	
	public static final String	IMG_CACHE			= "cache";
	public static final String	IMG_CACHE_MAPPINGS	= "mappings.json";
	
	public static final int SCROLL_INCREMENT = 128;
	
	public static final String	LABEL_ACCOUNT_NAME		= "Account Name";
	public static final String	LABEL_SECRET_KEY		= "Secret Key";
	public static final String	LABEL_ISSUER			= "Issuer";
	public static final String	LABEL_CATEGORY			= "Category";
	public static final String	LABEL_PASSWORD_INTERVAL	= "Password interval in s";
	public static final String	LABEL_PASSWORD_LENGTH	= "Password length";
	
	public static final String	BUTTON_ABORT	= "abort";
	public static final String	BUTTON_APPLY	= "apply";
	
	public static final String BUTTON_ADD = "➕";
}