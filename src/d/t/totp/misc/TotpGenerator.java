package d.t.totp.misc;

import static de.tinycodecrank.math.utils.range.Range.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.Box;

import d.t.totp.prefs.TotpEntry;
import d.t.totp.ui.main.TotpComponent;
import lombok.SneakyThrows;

public final class TotpGenerator extends Thread
{
	private volatile boolean	terminate	= false;
	private Box					contentBox;
	private Runnable			refreshUI;
	private char[]				password;
	
	public TotpGenerator(Runnable refreshUI, Box contentBox, char[] password)
	{
		this.contentBox	= contentBox;
		this.password	= password;
		this.refreshUI	= refreshUI;
	}
	
	public void run()
	{
		while (!terminate)
		{
			final long currentTime = System.currentTimeMillis();
			
			if (range(contentBox.getComponents()).element()
				.stream()
				.parallel()
				.map(c -> (TotpComponent) c)
				.filter(c -> c.update(currentTime, password))
				.count() > 0)
				refreshUI.run();
			
			try
			{
				TimeUnit.MILLISECONDS.sleep(100);
			}
			catch (InterruptedException e)
			{}
		}
		password	= null;
		contentBox	= null;
		refreshUI	= null;
	}
	
	public void stopLoop()
	{
		terminate = true;
	}
	
	public static String generateTotp(TotpEntry entry, char[] password, long currentTimeFrame)
	{
		final var steps = Long.toHexString(currentTimeFrame).toUpperCase();
		return generateTOTP(
			entry.decryptSecret(password),
			steps,
			entry.totpLength,
			"HmacSHA1");
	}
	
	@SneakyThrows
	private static byte[] hmac_sha(
		String crypto,
		byte[] keyBytes,
		byte[] text)
	{
		final var	hmac	= Mac.getInstance(crypto);
		final var	macKey	= new SecretKeySpec(keyBytes, "RAW");
		hmac.init(macKey);
		return hmac.doFinal(text);
	}
	
	private static byte[] hexStrToBytes(String hex)
	{
		final var array = new BigInteger("10" + hex, 16).toByteArray();
		return Arrays.copyOfRange(array, 1, array.length);
	}
	
	@SuppressWarnings("unused")
	private static String generateTOTP(
		byte[] key,
		String time,
		int codeDigits,
		String crypto)
	{
		time = leftPad(time, '0', 16);
		
		byte[] msg = hexStrToBytes(time);
		
		byte[] hash = hmac_sha(crypto, key, msg);
		
		int	offset	= hash[hash.length - 1] & 0xF;
		int	binary	= ((hash[offset] & 0x7F) << 24)
			| ((hash[offset + 1] & 0xFF) << 16)
			| ((hash[offset + 2] & 0xFF) << 8)
			| (hash[offset + 3] & 0xFF);
		
		long mod = 1;
		for (int i : range(codeDigits))
			mod *= 10;
		
		long otp = binary % mod;
		
		return leftPad(Long.toString(otp), '0', codeDigits);
	}
	
	@SuppressWarnings("unused")
	private static String leftPad(String orig, char padding, int size)
	{
		if (orig.length() >= size)
			return orig;
		final var sb = new StringBuilder(size);
		for (int _i : range(size - orig.length()))
			sb.append(padding);
		sb.append(orig);
		return sb.toString();
	}
}