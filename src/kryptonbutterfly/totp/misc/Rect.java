package kryptonbutterfly.totp.misc;

import kryptonbutterfly.math.vector._int.Vec2i;

public final class Rect
{
	private int	x	= 0;
	private int	y	= 0;
	public int	width, height;
	
	public Rect(Vec2i size)
	{
		this(size.x(), size.y());
	}
	
	public Rect(int width, int height)
	{
		this.width	= width;
		this.height	= height;
	}
	
	public int x1()
	{
		return x;
	}
	
	public int y1()
	{
		return y;
	}
	
	public int x2()
	{
		return x + width;
	}
	
	public int y2()
	{
		return y + height;
	}
	
	public Rect scaleWidth(float factor)
	{
		this.width *= factor;
		return this;
	}
	
	public Rect scaleHeight(float factor)
	{
		this.height *= factor;
		return this;
	}
	
	public Rect moveX(int x)
	{
		this.x += x;
		return this;
	}
	
	public Rect moveY(int y)
	{
		this.y += y;
		return this;
	}
}
