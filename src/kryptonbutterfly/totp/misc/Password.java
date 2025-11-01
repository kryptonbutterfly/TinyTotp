package kryptonbutterfly.totp.misc;

import static kryptonbutterfly.math.utils.range.Range.*;

import java.security.SecureRandom;
import java.util.HashSet;

public final class Password implements AutoCloseable
{
	private char[] password;
	
	private final HashSet<PasswordChangeListener> changeListener = new HashSet<>();
	
	public Password(char[] password)
	{
		this.password = password;
	}
	
	public char[] password()
	{
		synchronized (this)
		{
			return password;
		}
	}
	
	public PasswordEvaluation password(char[] password)
	{
		final var evaluation = validate(password);
		if (!evaluation.isValid)
			return evaluation;
		
		synchronized (this)
		{
			final var old = this.password;
			changeListener.forEach(l -> l.onChange(old, password));
			scramble();
			this.password = password;
			return evaluation;
		}
	}
	
	public static PasswordEvaluation validate(char[] password)
	{
		if (password.length == 0)
			return new PasswordEvaluation(true, "Password is empty!");
		return new PasswordEvaluation(true, "");
	}
	
	private void scramble()
	{
		final var rand = new SecureRandom();
		for (int i : range(password.length))
			password[i] = (char) rand.nextInt();
		password = null;
	}
	
	@Override
	public void close()
	{
		changeListener.clear();
		scramble();
	}
	
	public void addChangeListener(PasswordChangeListener listener)
	{
		this.changeListener.add(listener);
	}
	
	public void removeChangeListener(PasswordChangeListener listener)
	{
		this.changeListener.remove(listener);
	}
	
	public static final record PasswordEvaluation(boolean isValid, String message)
	{}
	
	@FunctionalInterface
	public static interface PasswordChangeListener
	{
		public void onChange(char[] oldPW, char[] newPW);
	}
}
