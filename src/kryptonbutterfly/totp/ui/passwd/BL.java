package kryptonbutterfly.totp.ui.passwd;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JOptionPane;

import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.ui.misc.KeyTypedAdapter;
import kryptonbutterfly.util.swing.Logic;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import kryptonbutterfly.util.swing.events.GuiCloseEvent.Result;
import kryptonbutterfly.util.swing.state.WindowState;

final class BL extends Logic<PasswdGui, WindowState>
{
	private final WindowState guiPrefs;
	
	BL(PasswdGui gui, WindowState prefs)
	{
		super(gui);
		this.guiPrefs = prefs;
	}
	
	void unlock(ActionEvent ae)
	{
		gui.if_(
			gui ->
			{
				final char[] passwd = gui.passwordField.getPassword();
				if (isValid(passwd))
					gui.dispose(new GuiCloseEvent<char[]>(Result.SUCCESS, Opt.empty(), passwd));
				else
				{
					gui.passwordField.setText("");
					JOptionPane.showMessageDialog(
						gui,
						"Unable to decrypt Totp keys using supplied password!",
						"Invalid Password",
						JOptionPane.ERROR_MESSAGE);
				}
			});
	}
	
	private boolean isValid(char[] password)
	{
		if (TinyTotp.config.entries.isEmpty())
			return true;
		final var entry = TinyTotp.config.entries.get(0);
		try
		{
			entry.decryptSecret(password);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	KeyListener enterListener(PasswdGui gui)
	{
		return new KeyTypedAdapter(c -> this.unlock(null), KeyEvent.VK_ENTER);
	}
	
	@Override
	protected void disposeAction()
	{
		gui.if_(guiPrefs::persistBounds);
	}
}