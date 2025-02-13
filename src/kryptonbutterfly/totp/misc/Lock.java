package kryptonbutterfly.totp.misc;

public final class Lock
{
	private volatile boolean locked;
	
	public Lock(boolean locked)
	{
		this.locked = locked;
	}
	
	public synchronized void notifyThread()
	{
		locked = false;
		notify();
	}
	
	public synchronized void await()
	{
		if (locked)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
