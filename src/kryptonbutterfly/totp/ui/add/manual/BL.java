package kryptonbutterfly.totp.ui.add.manual;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.function.Function;

import javax.swing.ImageIcon;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.apache.commons.codec.binary.Base32;

import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.TotpConstants;
import kryptonbutterfly.totp.misc.Utils;
import kryptonbutterfly.totp.prefs.TotpCategory;
import kryptonbutterfly.totp.prefs.TotpEntry;
import kryptonbutterfly.totp.ui.misc.KeyTypedAdapter;
import kryptonbutterfly.util.swing.Logic;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import kryptonbutterfly.util.swing.events.GuiCloseEvent.Result;
import lombok.SneakyThrows;

final class BL extends Logic<AddKey, char[]>
{
	private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	private char[]	password;
	private long	categoryPopupLastVisible	= 0L;
	
	BL(AddKey gui, char[] password)
	{
		super(gui);
		this.password = password;
	}
	
	final KeyListener enterListener = new KeyTypedAdapter(
		c -> gui.if_(gui -> c.transferFocus()),
		KeyEvent.VK_ENTER);
	
	final KeyListener escapeListener = new KeyTypedAdapter(
		c -> gui.if_(AddKey::dispose),
		KeyEvent.VK_ESCAPE);
	
	final KeyListener categoryEnterListener = new KeyTypedAdapter(
		c -> gui.if_(gui -> gui.btnApply.requestFocus()),
		KeyEvent.VK_ENTER);
	
	final PopupMenuListener categoryPopupListener = new PopupMenuListener()
	{
		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e)
		{}
		
		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
		{
			categoryPopupLastVisible = System.currentTimeMillis();
		}
		
		@Override
		public void popupMenuCanceled(PopupMenuEvent e)
		{}
	};
	
	final KeyListener categoryEscapeListener = new KeyAdapter()
	{
		public void keyTyped(KeyEvent e)
		{
			final long tolerance = 10L;
			if (e.getKeyChar() == KeyEvent.VK_ESCAPE)
				gui.if_(gui -> {
					if (Math.abs(e.getWhen() - categoryPopupLastVisible) < tolerance)
						return;
					e.consume();
					gui.dispose();
				});
		};
	};
	
	final MouseListener iconClickListener()
	{
		return new MouseAdapter()
		{
			@SuppressWarnings("unchecked")
			@Override
			@SneakyThrows
			public void mouseClicked(MouseEvent e)
			{
				if (getDataIfAvailable(DataFlavor.imageFlavor, img -> setIcon((BufferedImage) img), false))
					return;
				if (getDataIfAvailable(DataFlavor.javaFileListFlavor, f -> setIconFromFile((List<File>) f), false))
					return;
				getDataIfAvailable(DataFlavor.stringFlavor, string -> setIconFromUrl((String) string), false);
			}
		};
	}
	
	@SneakyThrows
	private <Res> Res getDataIfAvailable(DataFlavor flavor, Function<Object, Res> action, Res fallback)
	{
		if (clipboard.isDataFlavorAvailable(flavor))
			return action.apply(clipboard.getData(flavor));
		return fallback;
	}
	
	@SneakyThrows
	private boolean setIconFromFile(List<File> files)
	{
		return setIconFromUrl(files.get(0).toURI().toURL().toString());
	}
	
	private boolean setIconFromUrl(String address)
	{
		final var img = TinyTotp.imageCache.getImage(address);
		return gui.map(gui -> img.if_(image -> setIcon(gui, address, image))).isPresent();
	}
	
	private boolean setIcon(BufferedImage img)
	{
		return gui.flatmap(
			gui -> TinyTotp.imageCache.addImage(img)
				.if_(name -> setIcon(gui, name, img)))
			.isPresent();
	}
	
	private void setIcon(AddKey gui, String name, BufferedImage img)
	{
		final var icon = new ImageIcon(Utils.scaleDownToMax(img, TotpConstants.ICON_WIDTH));
		gui.iconName = name;
		gui.lblIcon.setIcon(icon);
		gui.lblIcon.setToolTipText(name);
	}
	
	void abort(ActionEvent ae)
	{
		gui.if_(AddKey::dispose);
	}
	
	void apply(ActionEvent ae)
	{
		gui.if_(gui -> {
			final var totpEntry = new TotpEntry();
			totpEntry.accountName	= gui.txtAccountname.getText();
			totpEntry.issuerName	= gui.txtIssuer.getText();
			final var category = ((TotpCategory) gui.comboBoxCategory.getSelectedItem());
			if (category != null)
				totpEntry.category = category.name;
			else
				totpEntry.category = null;
			
			totpEntry.encryptAndSetSecret(new Base32().decode(gui.txtSecretkey.getText()), password);
			totpEntry.icon = gui.iconName;
			
			totpEntry.totpLength			= (int) gui.spinnerTotpLength.getValue();
			totpEntry.totpValidForSeconds	= (int) gui.spinnerTimeFrame.getValue();
			
			gui.dispose(new GuiCloseEvent<>(Result.SUCCESS, Opt.empty(), totpEntry));
		});
	}
	
	@Override
	protected void disposeAction()
	{
		gui.if_(TinyTotp.windowStates.createDialog::persistBounds);
		
		password = null;
	}
}