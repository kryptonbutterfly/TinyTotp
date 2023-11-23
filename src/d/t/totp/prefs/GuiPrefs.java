package d.t.totp.prefs;

import static de.tinycodecrank.math.utils.range.Range.*;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import com.google.gson.annotations.Expose;

import de.tinycodecrank.math.utils.limit.LimitInt;
import de.tinycodecrank.monads.opt.Opt;

public class GuiPrefs
{
	private static final GuiPrefs DEFAULTS = new GuiPrefs();
	
	public GuiPrefs()
	{}
	
	public GuiPrefs(int posX, int posY, int width, int height, int state)
	{
		this.posX	= posX;
		this.posY	= posY;
		this.width	= width;
		this.height	= height;
		this.state	= state;
	}
	
	@Expose
	public int	posX	= 100;
	@Expose
	public int	posY	= 100;
	@Expose
	public int	width	= 800;
	@Expose
	public int	height	= 600;
	@Expose
	public int	screen	= 0;
	@Expose
	public int	state	= JFrame.NORMAL;
	
	public void setBounds(Component target)
	{
		final var	screens	= GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		final var	screen	= screens[LimitInt.clamp(0, this.screen, screens.length - 1)];
		final var	bounds	= screen.getDefaultConfiguration().getBounds();
		
		final int	x	= posX + bounds.x;
		final int	y	= posY + bounds.y;
		target.setBounds(x, y, width, height);
	}
	
	public void setBoundsAndState(Frame target)
	{
		setBounds(target);
		target.setExtendedState(state);
	}
	
	public void persistBounds(Component source)
	{
		persistBoundsAndState(source, JFrame.NORMAL);
	}
	
	public void persistBoundsAndState(Frame source)
	{
		state = source.getExtendedState();
		persistBoundsAndState(source, state);
	}
	
	private void persistBoundsAndState(Component source, int state)
	{
		if (GraphicsEnvironment.isHeadless())
			return;
		
		final var	DEFAULT_SCREEN_INDEX	= 0;
		final var	screens					= GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		
		var	currScreen		= source.getGraphicsConfiguration().getDevice();
		int	currScreenIndex	= findIndex(screens, currScreen, DEFAULT_SCREEN_INDEX);
		if (currScreenIndex == DEFAULT_SCREEN_INDEX && !findIndex(screens, this.screen).isPresent())
		{
			currScreenIndex	= Opt.of(this.screen)
				.filter(new LimitInt(0, screens.length - 1)::inRange)
				.get(() -> DEFAULT_SCREEN_INDEX);
			currScreen		= screens[currScreenIndex];
		}
		this.screen = currScreenIndex;
		
		final var	screenBounds	= currScreen.getDefaultConfiguration().getBounds();
		final var	loc				= source.getLocationOnScreen();
		
		switch (state)
		{
		case Frame.NORMAL -> {
			this.posX	= loc.x - screenBounds.x;
			this.posY	= loc.y - screenBounds.y;
			this.width	= source.getWidth();
			this.height	= source.getHeight();
		}
		case Frame.ICONIFIED -> {} // Do nothing (preserve original size and position)
		case Frame.MAXIMIZED_BOTH -> {} // Do nothing (preserve original size and position)
		case Frame.MAXIMIZED_HORIZ -> {
			this.posY	= loc.y - screenBounds.y;
			this.height	= source.getHeight();
		}
		case Frame.MAXIMIZED_VERT -> {
			this.posX	= loc.x - screenBounds.x;
			this.width	= source.getWidth();
		}
		default -> {
			new IllegalArgumentException(
				"Illegal Window state %s. Defaulting to NORMAL and resetting bounds to default")
					.printStackTrace();
			this.state	= DEFAULTS.state;
			this.posX	= DEFAULTS.posX;
			this.posY	= DEFAULTS.posY;
			this.width	= DEFAULTS.width;
			this.height	= DEFAULTS.height;
			this.screen	= DEFAULTS.screen;
		}
		}
	}
	
	private static <E> Opt<Integer> findIndex(E[] array, E element)
	{
		for (int i : range(array.length))
			if (array[i] == element)
				return Opt.of(i);
		return Opt.empty();
	}
	
	private static <E> int findIndex(E[] array, E element, int fallback)
	{
		return findIndex(array, element).get(() -> fallback);
	}
}