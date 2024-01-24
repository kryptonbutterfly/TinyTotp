package kryptonbutterfly.totp.misc;

import kryptonbutterfly.monads.opt.Opt;

public final class SecretData
{
	private static final String	ISSUER	= "\\&issuer\\=";
	private static final String	SECRET	= "\\?secret\\=";
	
	public final String	issuer;
	public final String	account;
	public final String	secretKey;
	
	public static final Opt<SecretData> parseUrl(String url)
	{
		try
		{
			var			split	= url.split(ISSUER);
			final var	issuer	= split[1];
			split = split[0].split(SECRET);
			final var	secretKey	= split[0];
			final var	remainder	= split[0];
			final int	end			= Math.max(remainder.lastIndexOf("/"), remainder.lastIndexOf(":")) + 1;
			final var	account		= remainder.substring(end);
			return Opt.of(new SecretData(issuer, secretKey, account));
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			return Opt.empty();
		}
	}
	
	private SecretData(String issuer, String secretKey, String account)
	{
		this.issuer		= issuer;
		this.secretKey	= secretKey;
		this.account	= account;
	}
}
