package d.t.totp.ui.misc.color;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JColorChooser;

import d.t.totp.misc.HtmlColors;
import d.t.totp.ui.misc.KeyTypedAdapter;
import de.tinycodecrank.monads.opt.Opt;
import de.tinycodecrank.util.swing.Logic;
import de.tinycodecrank.util.swing.events.GuiCloseEvent;
import de.tinycodecrank.util.swing.events.GuiCloseEvent.Result;

final class BL extends Logic<ColorChooserMenu, Color>
{
	private final Color	currentColor;
	private boolean		isFocusListenerActive	= true;
	
	private final KeyListener escapeKeyListener = new KeyTypedAdapter(
		c -> gui.if_(gui -> gui.dispose()),
		KeyEvent.VK_ESCAPE);
	
	BL(ColorChooserMenu gui, Color currentColor)
	{
		super(gui);
		this.currentColor = currentColor;
	}
	
	WindowFocusListener focusListener()
	{
		return new WindowAdapter()
		{
			@Override
			public void windowLostFocus(WindowEvent e)
			{
				if (isFocusListenerActive)
					gui.if_(gui -> gui.dispose());
			}
		};
	}
	
	KeyListener escapeKeyListener()
	{
		return escapeKeyListener;
	}
	
	void pickColor(ActionEvent ae)
	{
		gui.if_(gui ->
		{
			isFocusListenerActive = false;
			gui.setVisible(false);
			final var newColor = JColorChooser.showDialog(gui, "Category Color", currentColor);
			if (newColor != null)
				disposeSuccess(gui, newColor);
			else
				gui.dispose();
		});
	}
	
	void selectColor(ItemEvent ie)
	{
		gui.if_(gui ->
		{
			if (ie.getStateChange() != ItemEvent.SELECTED)
				return;
			final var newColor = (HtmlColors) gui.htmlColor.getSelectedItem();
			if (newColor != null)
				disposeSuccess(gui, newColor.color);
			else
				gui.dispose();
		});
	}
	
	private static void disposeSuccess(ColorChooserMenu gui, Color newColor)
	{
		gui.dispose(new GuiCloseEvent<Color>(Result.SUCCESS, Opt.empty(), newColor));
	}
}