package kryptonbutterfly.totp.misc;

import kryptonbutterfly.math.vector._int.Vec2i;

public record Margin(int top, int right, int bottom, int left)
{
	public Margin(int vertical, int horizontal)
	{
		this(vertical, horizontal, vertical, horizontal);
	}
	
	public Margin(int all)
	{
		this(all, all, all, all);
	}
	
	public int width()
	{
		return right + left;
	}
	
	public int height()
	{
		return top + bottom;
	}
	
	public Vec2i size()
	{
		return new Vec2i(width(), height());
	}
}