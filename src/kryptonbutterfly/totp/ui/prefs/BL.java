package kryptonbutterfly.totp.ui.prefs;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.ui.misc.KeyTypedAdapter;
import kryptonbutterfly.totp.ui.prefs.cat.PrefsCat;
import kryptonbutterfly.util.swing.Logic;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import kryptonbutterfly.util.swing.events.GuiCloseEvent.Result;

final class BL extends Logic<PreferencesGui, Void>
{
	final KeyListener escapeListener = new KeyTypedAdapter(
		c -> gui.if_(PreferencesGui::dispose),
		KeyEvent.VK_ESCAPE);
	
	BL(PreferencesGui gui)
	{
		super(gui);
	}
	
	void apply(ActionEvent _ae)
	{
		gui.if_(gui -> {
			gui.prefsCategories.forEach(PrefsCat::persist);
			gui.dispose(new GuiCloseEvent<>(Result.SUCCESS));
		});
	}
	
	void abort(ActionEvent _ae)
	{
		gui.if_(PreferencesGui::dispose);
	}
	
	@Override
	protected void disposeAction()
	{
		gui.if_(TinyTotp.windowStates.prefsWindow::persistBounds);
	}
}
