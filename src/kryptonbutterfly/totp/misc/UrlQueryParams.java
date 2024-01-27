package kryptonbutterfly.totp.misc;

import kryptonbutterfly.monads.opt.Opt;

public record UrlQueryParams(String issuer, String secretKey, String account)
{
	
	private static final String ISSUER = "\\&issuer\\=";
	private static final String SECRET = "\\?secret\\=";
	
	private static final String KEY_ISSUER = "&issuer=";
	private static final String KEY_SECRET = "?secret=";
	
	public final String toUrl()
	{
		return toUrl(account, secretKey, issuer);
	}
	
	public static final String toUrl(String account, String secretKey, String issuer)
	{
		return "otpauth://totp/" + account + KEY_SECRET + secretKey + KEY_ISSUER + issuer;
	}
	
	public static final Opt<UrlQueryParams> parseUrl(String url)
	{
		try
		{
			var			split	= url.split(ISSUER);
			final var	issuer	= split[1];
			split = split[0].split(SECRET);
			final var	secretKey	= split[1];
			final var	remainder	= split[0];
			final int	end			= Math.max(remainder.lastIndexOf("/"), remainder.lastIndexOf(":")) + 1;
			final var	account		= remainder.substring(end);
			return Opt.of(new UrlQueryParams(issuer, secretKey, account));
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			return Opt.empty();
		}
	}
}
