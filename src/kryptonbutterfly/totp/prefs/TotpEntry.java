package kryptonbutterfly.totp.prefs;

import java.awt.Color;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.annotations.Expose;

import kryptonbutterfly.totp.TotpConstants;
import kryptonbutterfly.totp.misc.otp.OtpUri;
import lombok.SneakyThrows;

public class TotpEntry implements TotpConstants
{
	public TotpEntry()
	{}
	
	@Expose
	public String encryptedSecret = null;
	
	@Expose
	public OtpAlgo algorithm = OtpUri.DEFAULT_ALGO;
	
	@Expose
	public String salt = createSalt();
	
	@Expose
	public int totpLength = 6;
	
	@Expose
	public Integer counter = null;
	
	@Expose
	public int totpValidForSeconds = 30;
	
	@Expose
	public String issuerName = null;
	
	@Expose
	public String accountName = null;
	
	@Expose
	public String icon = null;
	
	@Expose
	public String userIcon = null;
	
	@Expose
	public Color iconBG = null;
	
	@Expose
	public String category = null;
	
	@SneakyThrows
	private static String createSalt()
	{
		final var	seed	= new byte[32];
		final var	rand	= SecureRandom.getInstanceStrong();
		rand.nextBytes(seed);
		return Base64.getEncoder().encodeToString(seed);
	}
	
	private byte[] salt()
	{
		return Base64.getDecoder().decode(this.salt);
	}
	
	@SneakyThrows
	public byte[] decryptSecret(char[] password)
	{
		Objects.requireNonNull(password);
		Objects.requireNonNull(encryptedSecret);
		
		final var key = getKeyFromPassword(password);
		
		final var cipher = Cipher.getInstance(AES);
		cipher.init(Cipher.DECRYPT_MODE, key);
		
		final var encrypted = Base64.getDecoder().decode(encryptedSecret);
		return cipher.doFinal(encrypted);
	}
	
	@SneakyThrows
	private SecretKey getKeyFromPassword(char[] password)
	{
		Objects.requireNonNull(salt);
		
		final var	factory	= SecretKeyFactory.getInstance(PASSWORD_TO_KEY_ALGO);
		final var	spec	= new PBEKeySpec(password, salt(), AES_ITERATION_COUNT, AES_KEY_LENGTH);
		return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), AES);
	}
	
	@SneakyThrows
	public void encryptAndSetSecret(byte[] secret, char[] password)
	{
		Objects.requireNonNull(secret);
		Objects.requireNonNull(password);
		
		final var	key		= getKeyFromPassword(password);
		final var	cipher	= Cipher.getInstance(AES);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		final var encrypted = cipher.doFinal(secret);
		this.encryptedSecret = Base64.getEncoder().encodeToString(encrypted);
	}
}