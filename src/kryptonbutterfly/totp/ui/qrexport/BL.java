package kryptonbutterfly.totp.ui.qrexport;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.ui.misc.KeyTypedAdapter;
import kryptonbutterfly.util.swing.Logic;

final class BL extends Logic<QrExportGui, Void>
{
	final KeyListener escapeListener = new KeyTypedAdapter(c -> ok(null), KeyEvent.VK_ESCAPE);
	
	BL(QrExportGui gui)
	{
		super(gui);
	}
	
	void ok(ActionEvent ae)
	{
		gui.if_(QrExportGui::dispose);
	}
	
	@Override
	protected void disposeAction()
	{
		gui.if_(TinyTotp.windowStates.qrExport::persistBounds);
	}
}
