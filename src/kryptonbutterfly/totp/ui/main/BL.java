package kryptonbutterfly.totp.ui.main;

import static kryptonbutterfly.math.utils.range.Range.*;

import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.misc.TotpGenerator;
import kryptonbutterfly.totp.misc.otp.OtpUri;
import kryptonbutterfly.totp.prefs.TotpEntry;
import kryptonbutterfly.totp.ui.add.manual.AddKey;
import kryptonbutterfly.totp.ui.categories.CategoriesGui;
import kryptonbutterfly.totp.ui.qrimport.QrGui;
import kryptonbutterfly.util.swing.Logic;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;

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
		gui.if_(gui -> {
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
					this::addEntry,
					password,
					"Add TOTP Secret")));
	}
	
	void addQrEntry(ActionEvent ae)
	{
		gui.if_(
			gui -> EventQueue
				.invokeLater(
					() -> new QrGui(
						gui,
						ModalityType.APPLICATION_MODAL,
						gce -> gce.getReturnValue().if_(url ->
						{
							try
							{
								final var uri = OtpUri.parseOtpUrl(url);
								createEntry(gui, uri);
							}
							catch (MalformedURLException | URISyntaxException e)
							{
								JOptionPane.showMessageDialog(
									gui,
									"Unable to import secret.\n\n%s".formatted(e),
									"Import failed",
									JOptionPane.ERROR_MESSAGE);
							}
						}),
						"Import Secret")));
	}
	
	private void createEntry(MainGui gui, OtpUri uri)
	{
		EventQueue.invokeLater(
			() -> new AddKey(
				gui,
				ModalityType.APPLICATION_MODAL,
				this::addEntry,
				uri,
				password,
				"Add TOTP Secret"));
	}
	
	private void addEntry(GuiCloseEvent<TotpEntry> gce)
	{
		gce.getReturnValue().if_(entry -> {
			gui.if_(gui -> {
				gui.contentBox.add(new TotpComponent(entry, this::remove, password));
				gui.validate();
			});
		});
	}
	
	void categories(ActionEvent ae)
	{
		gui.if_(gui -> EventQueue.invokeLater(() -> new CategoriesGui(gui, ModalityType.APPLICATION_MODAL, gce -> {
			gce.getReturnValue().if_(mapping -> {
				for (final var comp : gui.contentBox.getComponents())
					((TotpComponent) comp).updateCategory(mapping);
			});
		})));
	}
	
	void remove(TotpComponent entry)
	{
		gui.if_(gui -> {
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
		
		gui.if_(gui -> {
			final var list = new ArrayList<TotpEntry>();
			for (final var c : gui.contentBox.getComponents())
				list.add(((TotpComponent) c).entry());
			TinyTotp.config.entries = list;
		});
		clearPassword();
		
		gui.if_(TinyTotp.windowStates.mainWindow::persistBoundsAndState);
	}
	
	private void clearPassword()
	{
		for (int i : range(password.length))
			password[i] = (char) 0xFFFF;
		password = null;
	}
}