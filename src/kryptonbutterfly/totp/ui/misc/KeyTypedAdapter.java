package kryptonbutterfly.totp.ui.misc;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class KeyTypedAdapter extends KeyAdapter
{
	private final Consumer<Component>	action;
	private final int[]					keys;
	
	public KeyTypedAdapter(Consumer<Component> action, int... keys)
	{
		this.action	= action;
		this.keys	= keys;
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
		if (e.isConsumed())
			return;
		for (int key : keys)
		{
			if (e.getKeyChar() == key)
			{
				e.consume();
				action.accept(e.getComponent());
			}
		}
	}
}