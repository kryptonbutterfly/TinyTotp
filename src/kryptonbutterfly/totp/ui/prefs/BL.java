package kryptonbutterfly.totp.ui.prefs;

import java.awt.event.ActionEvent;

import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.ui.prefs.cat.PrefsCat;
import kryptonbutterfly.util.swing.Logic;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import kryptonbutterfly.util.swing.events.GuiCloseEvent.Result;

final class BL extends Logic<PreferencesGui, Void>
{
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
