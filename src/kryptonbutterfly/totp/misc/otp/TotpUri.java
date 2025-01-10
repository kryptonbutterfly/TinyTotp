package kryptonbutterfly.totp.misc.otp;

import static kryptonbutterfly.math.utils.range.Range.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Objects;

import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.totp.prefs.OtpAlgo;

public final class TotpUri implements OtpUri
{
	private final String		issuer, account, secret;
	private final OtpAlgo		algo;
	private final Digits		digits;
	private final Opt<Integer>	counter;
	private final int			period;
	
	private TotpUri(
		String issuer,
		String account,
		String secret,
		OtpAlgo algo,
		Digits digits,
		Opt<Integer> counter,
		int period)
	{
		Objects.requireNonNull(issuer);
		Objects.requireNonNull(account);
		Objects.requireNonNull(secret);
		Objects.requireNonNull(algo);
		Objects.requireNonNull(digits);
		
		this.issuer		= issuer;
		this.account	= account;
		this.secret		= secret;
		this.algo		= algo;
		this.digits		= digits;
		this.counter	= counter;
		this.period		= period;
	}
	
	public TotpUri(
		String issuer,
		String account,
		String secret,
		int digits,
		int period)
	{
		this(issuer, account, secret, DEFAULT_ALGO, Digits.of("" + digits), Opt.empty(), period);
	}
	
	private TotpUri(
		String issuer,
		String account,
		String secret,
		Opt<OtpAlgo> algo,
		Opt<Digits> digits,
		Opt<Integer> counter,
		Opt<Integer> period)
	{
		this(issuer, account, secret, algo.get(() -> OtpAlgo.SHA1), digits
			.get(() -> DEFAULT_DIGITS), counter, period.get(() -> DEFAULT_PERIOD));
	}
	
	static TotpUri parse(String url) throws URISyntaxException, MalformedURLException
	{
		final var	uri		= new URI(url);
		final var	query	= uri.getQuery().split(QUERY_DELIM);
		final var	params	= new HashMap<String, String>();
		for (final var ie : range(query))
		{
			final var split = ie.element().split(PARAM_DELIM);
			if (split.length == 2)
				params.put(split[0], split[1]);
			else
				System.err.printf(
					"[WARN]\tQueryparam contains '%s' %d times. Expected 1. Ignoring this param.",
					QUERY_DELIM,
					split.length);
		}
		
		final String secret = params.get(SECRET);
		if (secret == null)
			throw new MalformedURLException("OTP URL secret missing.");
		final Opt<OtpAlgo>	algorithm	= Opt.of(params.get(ALGORITHM)).map(OtpAlgo::valueOf);
		final Opt<Digits>	digits		= Opt.of(params.get(DIGITS)).map(Digits::of);
		final Opt<Integer>	counter		= Opt.of(params.get(COUNTER)).map(Integer::valueOf);
		final Opt<Integer>	period		= Opt.of(params.get(PERIOD)).map(Integer::valueOf);
		
		final String label = uri.getPath().substring(TYPE_TERMINATOR.length());
		
		final String	account;
		final String	issuer;
		if (label.contains(ISSUER_TERMINATOR))
		{
			final int issuerTerm = label.indexOf(ISSUER_TERMINATOR);
			issuer = label.substring(0, issuerTerm);
			if (params.containsKey(ISSUER) && !params.get(ISSUER).equals(issuer))
				throw new MalformedURLException(
					"conflicting issuer: %s – %s".formatted(issuer, params.get(ISSUER)));
			account = label.substring(issuerTerm + ISSUER_TERMINATOR.length());
		}
		else
		{
			account	= label;
			issuer	= params.get(ISSUER);
		}
		if (!Objects.nonNull(issuer))
			throw new MalformedURLException("OTP URL issuer missing.");
		
		return new TotpUri(issuer, account, secret, algorithm, digits, counter, period);
	}
	
	@Override
	public String toStringUrl()
	{
		final var map = new HashMap<String, String>();
		map.put(SECRET, secret);
		map.put(ISSUER, issuer);
		if (algo != DEFAULT_ALGO)
			map.put(ALGORITHM, account);
		if (digits != DEFAULT_DIGITS)
			map.put(DIGITS, "" + digits.digits);
		counter.if_(c -> map.put(COUNTER, "" + c));
		if (period != DEFAULT_PERIOD)
			map.put(PERIOD, "" + period);
		
		return OtpUri.buildOtpUriString(this, map);
	}
	
	@Override
	public String account()
	{
		return account;
	}
	
	@Override
	public String secret()
	{
		return secret;
	}
	
	@Override
	public OtpAlgo algorithm()
	{
		return algo;
	}
	
	@Override
	public int digits()
	{
		return digits.digits;
	}
	
	public boolean hasCounter()
	{
		return counter.isPresent();
	}
	
	@Override
	/**
	 * @return The counter or {@code Integer.MIN_VALUE} if not present.
	 */
	public int counter()
	{
		return counter.get(() -> Integer.MIN_VALUE);
	}
	
	@Override
	public int period()
	{
		return period;
	}
	
	@Override
	public String issuer()
	{
		return issuer;
	}
	
	@Override
	public String type()
	{
		return "totp";
	}
}
