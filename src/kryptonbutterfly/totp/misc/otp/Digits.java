package kryptonbutterfly.totp.misc.otp;

enum Digits
{
	_6(6),
	_7(7),
	_8(8);
	
	public final int digits;
	
	Digits(int digits)
	{
		this.digits = digits;
	}
	
	public static Digits of(String value)
	{
		return switch (value)
		{
			case "6" -> _6;
			case "7" -> _7;
			case "8" -> _8;
			default -> null;
		};
	}
}
