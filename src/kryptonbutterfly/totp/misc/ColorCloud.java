package kryptonbutterfly.totp.misc;

import java.awt.Color;
import java.util.HashSet;
import java.util.function.Function;

import kryptonbutterfly.math.vector._int.IVecI;
import kryptonbutterfly.monads.opt.Opt;
import lombok.val;

public final class ColorCloud<Col extends NamedColor, Vec extends IVecI<Vec>>
{
	private final Function<Color, Vec> toVec;
	
	public ColorCloud(Function<Color, Vec> toVec)
	{
		this.toVec = toVec;
	}
	
	private final HashSet<VectorizedColor<Col, Vec>> colors = new HashSet<>();
	
	public boolean addAllColors(Col[] colors)
	{
		boolean hasDuplicates = false;
		for (val color : colors)
			hasDuplicates |= addColor(color);
		return hasDuplicates;
	}
	
	public boolean addColor(Col color)
	{
		val vec = toVec.apply(color.color());
		return colors.add(new VectorizedColor<>(vec, color));
	}
	
	public Opt<Col> getClosest(Color color)
	{
		Col	closest		= null;
		int	closestDist	= Integer.MAX_VALUE;
		
		for (val c : colors)
		{
			val			vec		= toVec.apply(color);
			final int	dist	= c.vec().sub(vec).lengthSQ();
			if (dist < closestDist)
			{
				closest		= c.color();
				closestDist	= dist;
			}
		}
		return Opt.of(closest);
	}
}