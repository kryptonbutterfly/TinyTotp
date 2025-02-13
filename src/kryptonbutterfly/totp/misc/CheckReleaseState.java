package kryptonbutterfly.totp.misc;

import java.util.function.LongSupplier;

import kryptonbutterfly.checkRelease.ICadence;
import kryptonbutterfly.checkRelease.ICheckReleaseState;
import kryptonbutterfly.checkRelease.data.ReleaseInfo;
import kryptonbutterfly.totp.prefs.ReleaseState;

public class CheckReleaseState implements ICheckReleaseState
{
	private LongSupplier		currentTimeMillis	= null;
	private final ReleaseState	state;
	
	public CheckReleaseState(LongSupplier currentTimeMillis, ReleaseState state)
	{
		this.currentTimeMillis	= currentTimeMillis;
		this.state				= state;
	}
	
	@Override
	public long lastQuery()
	{
		return state.lastQuery;
	}
	
	@Override
	public void lastQuery(long last)
	{
		this.state.lastQuery = last;
	}
	
	@Override
	public ICadence cadence()
	{
		return this.state.cadence;
	}
	
	@Override
	public ReleaseInfo latestVersion()
	{
		return this.state.latestVersion;
	}
	
	@Override
	public void latestVersion(ReleaseInfo release)
	{
		this.state.latestVersion = release;
	}
	
	@Override
	public long currentTimeMillis()
	{
		return currentTimeMillis.getAsLong();
	}
}
