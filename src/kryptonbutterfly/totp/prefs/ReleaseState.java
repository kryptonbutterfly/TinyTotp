package kryptonbutterfly.totp.prefs;

import com.google.gson.annotations.Expose;

import kryptonbutterfly.checkRelease.Cadence;
import kryptonbutterfly.checkRelease.data.ReleaseInfo;

public class ReleaseState
{
	@Expose
	public long lastQuery = Long.MIN_VALUE;
	
	@Expose
	public Cadence cadence = Cadence.Daily;
	
	@Expose
	public ReleaseInfo latestVersion = null;
}
