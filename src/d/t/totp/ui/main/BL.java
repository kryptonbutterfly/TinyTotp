package d.t.totp.ui.main;

import static de.tinycodecrank.math.utils.range.Range.*;

import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import d.t.totp.TinyTotp;
import d.t.totp.misc.TotpGenerator;
import d.t.totp.prefs.TotpEntry;
import d.t.totp.ui.add.manual.AddKey;
import d.t.totp.ui.categories.CategoriesGui;
import de.tinycodecrank.util.swing.Logic;

final class BL extends Logic<MainGui, char[]>
{
	private char[]			password;
	private TotpGenerator	generator;
	
	BL(MainGui gui, char[] password)
	{
		super(gui);
		this.password = password;
	}
	
	void populateContent()
	{
		gui.if_(gui ->
		{
			TinyTotp.config.entries.forEach(e -> gui.contentBox.add(new TotpComponent(e, this::remove, password)));
			generator = new TotpGenerator(gui::validate, gui.contentBox, password);
			generator.start();
		});
	}
	
	void addEntry(ActionEvent ae)
	{
		gui.if_(
			gui -> EventQueue.invokeLater(
				() -> new AddKey(
					gui,
					ModalityType.APPLICATION_MODAL,
					gce -> gce.getReturnValue().if_(e ->
					{
						gui.contentBox.add(new TotpComponent(e, this::remove, password));
						gui.validate();
					}),
					password,
					"Add TOTP Secret")));
	}
	
	void addQrEntry(ActionEvent ae)
	{
		// TODO implement
		System.out.println("add TOTP key via qr code");
	}
	
	void categories(ActionEvent ae)
	{
		gui.if_(gui -> EventQueue.invokeLater(() -> new CategoriesGui(gui, ModalityType.APPLICATION_MODAL, gce ->
		{
			gce.getReturnValue().if_(mapping ->
			{
				for (final var comp : gui.contentBox.getComponents())
					((TotpComponent) comp).updateCategory(mapping);
			});
		})));
	}
	
	void remove(TotpComponent entry)
	{
		gui.if_(gui ->
		{
			gui.contentBox.remove(entry);
			gui.validate();
			gui.repaint();
		});
	}
	
	@Override
	protected void disposeAction()
	{
		if (generator != null)
		{
			generator.stopLoop();
			generator = null;
		}
		
		gui.if_(gui ->
		{
			final var list = new ArrayList<TotpEntry>();
			for (final var c : gui.contentBox.getComponents())
				list.add(((TotpComponent) c).entry());
			TinyTotp.config.entries = list;
		});
		clearPassword();
		
		gui.if_(TinyTotp.config.mainWindow::persistBoundsAndState);
	}
	
	private void clearPassword()
	{
		for (int i : range(password.length))
			password[i] = (char) 0xFFFF;
		password = null;
	}
}