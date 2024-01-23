package kryptonbutterfly.totp.ui.main;

import java.awt.BorderLayout;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.TotpConstants;
import kryptonbutterfly.totp.misc.Assets;
import kryptonbutterfly.totp.misc.HtmlColors;
import kryptonbutterfly.util.swing.ObservableGui;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import lombok.val;

@SuppressWarnings("serial")
public class MainGui extends ObservableGui<BL, Void, char[]> implements TotpConstants
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
		add(menuBar, BorderLayout.NORTH);
		
		val addQrEntry = new JMenuItem(Assets.getQrByBackground(getBackground()));
		menuBar.add(addQrEntry);
		addQrEntry.addActionListener(logic::addQrEntry);
		
		val addEntryItem = new JMenuItem(BUTTON_ADD);
		addEntryItem.setToolTipText("Add new TOTP Key");
		addEntryItem.setForeground(HtmlColors.LightGreen.color());
		menuBar.add(addEntryItem);
		addEntryItem.addActionListener(logic::addEntry);
		
		val categoriesItem = new JMenuItem("Categories");
		menuBar.add(categoriesItem);
		categoriesItem.addActionListener(logic::categories);
		
		val scrollPane = new JScrollPane(contentBox);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(TotpConstants.SCROLL_INCREMENT);
		add(scrollPane, BorderLayout.CENTER);
		
		logic.populateContent();
		
		// add(contentBox, BorderLayout.CENTER);
	}
}