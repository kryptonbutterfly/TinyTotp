package kryptonbutterfly.totp.prefs;

public enum OtpAlgo
{
	SHA1,
	SHA256,
	SHA512;
	
	public final String algo;
	
	private OtpAlgo()
	{
		this.algo = this.name();
	}
}
