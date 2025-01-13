package kryptonbutterfly.totp.ui.main;

import java.awt.BorderLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;

import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.TotpConstants;
import kryptonbutterfly.totp.misc.Assets;
import kryptonbutterfly.util.swing.ObservableGui;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import lombok.val;

@SuppressWarnings("serial")
public final class MainGui extends ObservableGui<BL, Void, char[]> implements TotpConstants
{
	final Box contentBox = Box.createVerticalBox();
	
	public MainGui(Consumer<GuiCloseEvent<Void>> closeListener, char[] password)
	{
		super(closeListener, password);
		TinyTotp.windowStates.mainWindow.setBoundsAndState(this);
		setTitle("Tiny TOTP");
		
		businessLogic.if_(this::init);
	}
	
	@Override
	protected BL createBusinessLogic(char[] passwd)
	{
		return new BL(this, passwd);
	}
	
	private void init(BL logic)
	{
		val menuBar = new JMenuBar();
		getContentPane().add(menuBar, BorderLayout.NORTH);
		
		JMenu addMenu = new JMenu(BUTTON_ADD);
		menuBar.add(addMenu);
		addMenu.setToolTipText("Add new Totp key.");
		
		JMenuItem addQrEntry = new JMenuItem(Assets.getQr16ByBackground(getBackground()));
		addQrEntry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
		addQrEntry.setText("import from qr");
		addMenu.add(addQrEntry);
		addQrEntry.addActionListener(logic::addQrEntry);
		
		val addEntryItem = new JMenuItem("add manually");
		addEntryItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK));
		addEntryItem.setToolTipText("Manually add new Totp key.");
		addMenu.add(addEntryItem);
		addEntryItem.addActionListener(logic::addEntry);
		
		final var settingsItem = new JMenu("Settings");
		menuBar.add(settingsItem);
		
		val categoriesItem = new JMenuItem("Categories");
		settingsItem.add(categoriesItem);
		categoriesItem.addActionListener(logic::categories);
		
		val preferencesItem = new JMenuItem("Preferences");
		settingsItem.add(preferencesItem);
		preferencesItem.addActionListener(logic::preferences);
		
		val scrollPane = new JScrollPane(contentBox);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(TotpConstants.SCROLL_INCREMENT);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		logic.populateContent();
	}
}