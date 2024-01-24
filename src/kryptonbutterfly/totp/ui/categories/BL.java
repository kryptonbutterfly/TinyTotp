package kryptonbutterfly.totp.ui.categories;

import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;

import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.TotpConstants;
import kryptonbutterfly.totp.misc.HtmlColors;
import kryptonbutterfly.totp.prefs.TotpCategory;
import kryptonbutterfly.totp.ui.misc.KeyTypedAdapter;
import kryptonbutterfly.totp.ui.misc.color.ColorChooserMenu;
import kryptonbutterfly.util.swing.Logic;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import kryptonbutterfly.util.swing.events.GuiCloseEvent.Result;

final class BL extends Logic<CategoriesGui, Void>
{
	private final KeyListener enterName = new KeyTypedAdapter(c -> gui.if_(gui -> {
		if (gui.name.getText().isBlank())
			gui.btnApply.requestFocus();
		else
			add(null);
	}), KeyEvent.VK_ENTER);
	
	private final KeyListener escapeListener = new KeyTypedAdapter(
		c -> gui.if_(CategoriesGui::dispose),
		KeyEvent.VK_ESCAPE);
	
	private final KeyListener enterTypedListener = new KeyTypedAdapter(
		c -> gui.if_(gui -> gui.btnApply.requestFocus()),
		KeyEvent.VK_ENTER);
	
	BL(CategoriesGui gui)
	{
		super(gui);
	}
	
	void initAdd(CategoriesGui gui)
	{
		gui.color = randomHTMLColor();
		gui.icon.setIcon(TotpCategory.getColoredIcon(gui.color));
		gui.name.setText("");
	}
	
	KeyListener enterName()
	{
		return enterName;
	}
	
	KeyListener escapeListener()
	{
		return escapeListener;
	}
	
	KeyListener enterTypedListener()
	{
		return enterTypedListener;
	}
	
	MouseListener colorListener()
	{
		return new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				final var mousePos = e.getLocationOnScreen();
				gui.if_(
					gui -> EventQueue.invokeLater(
						() -> new ColorChooserMenu(
							gui,
							ModalityType.MODELESS,
							res -> res.getReturnValue().if_(color ->
							{
								gui.color = color;
								gui.icon.setIcon(TotpCategory.getColoredIcon(color));
							}),
							gui.color,
							mousePos)));
			}
		};
	}
	
	void add(ActionEvent ae)
	{
		gui.if_(gui -> {
			if (gui.name.getText().isBlank())
				return;
			final var category = new TotpCategory();
			category.name	= gui.name.getText().trim();
			category.color	= gui.color;
			gui.contentBox.add(new CategoryComponent(gui, category, this::remove, escapeListener, enterTypedListener));
			
			initAdd(gui);
			gui.validate();
		});
	}
	
	void remove(CategoryComponent entry)
	{
		gui.if_(gui -> {
			gui.contentBox.remove(entry);
			gui.validate();
			gui.repaint();
		});
	}
	
	void abort(ActionEvent ae)
	{
		gui.if_(CategoriesGui::dispose);
	}
	
	void apply(ActionEvent ae)
	{
		gui.if_(gui -> {
			final var changeMap = new HashMap<String, String>();
			
			final var categories = new ArrayList<TotpCategory>();
			for (final var component : gui.contentBox.getComponents())
			{
				final var category = (CategoryComponent) component;
				categories.add(category.category());
				
				final var	origName	= category.category().name;
				final var	newName		= category.txtCategory.getText();
				if (!origName.equals(newName))
					changeMap.put(origName, newName);
				category.category().name = newName;
			}
			
			TinyTotp.config.categories = categories;
			
			gui.dispose(new GuiCloseEvent<>(Result.SUCCESS, Opt.empty(), changeMap));
		});
	}
	
	@Override
	protected void disposeAction()
	{
		gui.if_(TinyTotp.windowStates.categoryDialog::persistBounds);
	}
	
	private Color randomHTMLColor()
	{
		final int index = TotpConstants.FAST_RANDOM.nextInt(HtmlColors.values().length);
		return HtmlColors.values()[index].color();
	}
}