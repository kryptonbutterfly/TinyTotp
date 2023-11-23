package d.t.totp.ui.misc;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class KeyTypedAdapter extends KeyAdapter
{
	private final Consumer<Component>	action;
	private final int					key;
	
	public KeyTypedAdapter(Consumer<Component> action, int key)
	{
		this.action	= action;
		this.key	= key;
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
		if (e.getKeyChar() == key)
		{
			e.consume();
			action.accept(e.getComponent());
		}
	}
}