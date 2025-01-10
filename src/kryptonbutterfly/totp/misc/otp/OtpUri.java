package kryptonbutterfly.totp.misc.otp;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;

import kryptonbutterfly.totp.prefs.OtpAlgo;

public sealed interface OtpUri permits TotpUri
{
	static final String	PROTOCOL_TERMINATOR	= "://";
	static final String	PROTOCOL			= "otpauth://";
	static final String	TYPE_TERMINATOR		= "/";
	static final String	PATH_TERMINATOR		= "?";
	static final String	PARAM_DELIM			= "=";
	static final String	QUERY_DELIM			= "&";
	static final String	ISSUER_TERMINATOR	= ":";
	
	static final String	SECRET		= "secret";
	static final String	ALGORITHM	= "algorithm";
	static final String	DIGITS		= "digits";
	static final String	COUNTER		= "counter";
	static final String	PERIOD		= "period";
	static final String	ISSUER		= "issuer";
	
	public static final int		DEFAULT_PERIOD	= 30;
	public static final Digits	DEFAULT_DIGITS	= Digits._6;
	public static final OtpAlgo	DEFAULT_ALGO	= OtpAlgo.SHA1;
	
	public String toStringUrl();
	
	public String account();
	
	public String secret();
	
	public OtpAlgo algorithm();
	
	public int digits();
	
	public int counter();
	
	public int period();
	
	public String type();
	
	public String issuer();
	
	public static OtpUri parseOtpUrl(String url) throws MalformedURLException, URISyntaxException
	{
		if (!url.startsWith(PROTOCOL))
		{
			final int term = url.indexOf(PROTOCOL_TERMINATOR);
			if (term < 0)
				throw new MalformedURLException("No protocol specified.");
			final var protocol = url.substring(term);
			throw new MalformedURLException(
				"Invalid protocol '%s' for OTP URL. Expected %s.".formatted(protocol, PROTOCOL));
		}
		final var urlPart = url.substring(PROTOCOL.length());
		
		final int typeTerm = urlPart.indexOf(TYPE_TERMINATOR);
		if (typeTerm < 0)
			throw new MalformedURLException("OTP URL is missing type declaration!");
		final var type = urlPart.substring(0, typeTerm);
		return switch (type)
		{
			case "totp" -> TotpUri.parse(url);
			case "htop" -> throw new IllegalStateException(
				"HTOP's currently are not supported.");
			default -> throw new MalformedURLException(
				"'%s' is not a valid OTP type!".formatted(type));
		};
	}
	
	public static String buildOtpUriString(OtpUri uri, HashMap<String, String> query)
	{
		final var sb = new StringBuilder(PROTOCOL)
			.append(uri.type())
			.append(TYPE_TERMINATOR)
			.append(uri.account())
			.append(PATH_TERMINATOR);
		
		boolean isFirst = true;
		for (final var p : query.entrySet())
		{
			if (isFirst)
				isFirst = false;
			else
				sb.append(QUERY_DELIM);
			sb.append(p.getKey())
				.append(PARAM_DELIM)
				.append(p.getValue());
		}
		
		return sb.toString();
	}
}
