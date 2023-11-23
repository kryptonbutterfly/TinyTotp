package d.t.totp.ui.passwd;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JOptionPane;

import d.t.totp.TinyTotp;
import d.t.totp.prefs.GuiPrefs;
import d.t.totp.ui.misc.KeyTypedAdapter;
import de.tinycodecrank.monads.opt.Opt;
import de.tinycodecrank.util.swing.Logic;
import de.tinycodecrank.util.swing.events.GuiCloseEvent;
import de.tinycodecrank.util.swing.events.GuiCloseEvent.Result;

final class BL extends Logic<PasswdGui, GuiPrefs>
{
	private final GuiPrefs guiPrefs;
	
	BL(PasswdGui gui, GuiPrefs prefs)
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