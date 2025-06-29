package kryptonbutterfly.totp.ui.seticon;

import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.function.Function;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.TotpConstants;
import kryptonbutterfly.totp.misc.Utils;
import kryptonbutterfly.totp.prefs.TotpCategory;
import kryptonbutterfly.totp.ui.chooseicon.ChooseIcon;
import kryptonbutterfly.totp.ui.misc.KeyTypedAdapter;
import kryptonbutterfly.totp.ui.misc.color.ColorChooserMenu;
import kryptonbutterfly.util.swing.Logic;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import kryptonbutterfly.util.swing.events.GuiCloseEvent.Result;
import lombok.SneakyThrows;

final class BL extends Logic<SetIcon, IconData> implements TotpConstants
{
	private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	private transient IconData icon = new IconData(null, null, null, null, null);
	
	final KeyListener escapeListener = new KeyTypedAdapter(
		c -> gui.if_(SetIcon::dispose),
		KeyEvent.VK_ESCAPE);
	
	private Color lastBG;
	
	BL(SetIcon gui, IconData data)
	{
		super(gui);
		this.lastBG = Utils.isBright(gui.getBackground()) ? LIGHT_ICON_BG : DARK_ICON_BG;
		if (data != null)
		{
			this.icon = data;
			if (data.bgColor() != null)
				this.lastBG = data.bgColor();
		}
	}
	
	void chooseIssuerIcon(ActionEvent _ae)
	{
		gui.if_(
			gui -> new ChooseIcon(
				gui,
				ModalityType.APPLICATION_MODAL,
				gce -> gce.getReturnValue().if_(t ->
				{
					setIcon(t.first(), t.second());
				})));
	}
	
	void chooseUserIcon(ActionEvent _ae)
	{
		gui.if_(
			gui -> new ChooseIcon(
				gui,
				ModalityType.APPLICATION_MODAL,
				gce -> gce.getReturnValue().if_(t ->
				{
					setUserIcon(t.first(), t.second());
				})));
	}
	
	void setBgColor(Color bg)
	{
		gui.if_(gui -> {
			if (bg != null)
			{
				this.lastBG = bg;
				gui.lblBgColor.setIcon(TotpCategory.getColoredIcon(bg));
			}
			this.icon = new IconData(
				this.icon.issuerName(),
				this.icon.issuerImage(),
				this.icon.userName(),
				this.icon.userImage(),
				bg);
			gui.icons.setIconBG(bg);
		});
	}
	
	@SneakyThrows
	private <Res> Res getDataIfAvailable(DataFlavor flavor, Function<Object, Res> action, Res fallback)
	{
		if (clipboard.isDataFlavorAvailable(flavor))
			return action.apply(clipboard.getData(flavor));
		return fallback;
	}
	
	@SuppressWarnings("unchecked")
	void pasteImage(ActionEvent _ae)
	{
		if (getDataIfAvailable(DataFlavor.imageFlavor, img -> setIcon(null, (BufferedImage) img), false))
			return;
		if (getDataIfAvailable(DataFlavor.javaFileListFlavor, f -> setIconFromFile((List<File>) f), false))
			return;
		if (getDataIfAvailable(DataFlavor.stringFlavor, string -> setIconFromUrl((String) string), false))
			return;
		setIcon(null, null);
	}
	
	@SuppressWarnings("unchecked")
	void pasteUserImage(ActionEvent _ae)
	{
		if (getDataIfAvailable(DataFlavor.imageFlavor, img -> setUserIcon(null, (BufferedImage) img), false))
			return;
		if (getDataIfAvailable(DataFlavor.javaFileListFlavor, f -> setUserIconFromFile((List<File>) f), false))
			return;
		if (getDataIfAvailable(DataFlavor.stringFlavor, string -> setUserIconFromUrl((String) string), false))
			return;
		setUserIcon(null, null);
	}
	
	private boolean setIcon(String name, BufferedImage img)
	{
		setIcon(new IconData(name, img, icon.userName(), icon.userImage(), icon.bgColor()));
		return true;
	}
	
	private boolean setUserIcon(String name, BufferedImage img)
	{
		setIcon(new IconData(icon.issuerName(), icon.issuerImage(), name, img, icon.bgColor()));
		return true;
	}
	
	private void setIcon(IconData ico)
	{
		this.icon = ico;
		gui.if_(gui -> {
			gui.chckbxUserIcon.setEnabled(true);
			gui.chckbxBackgroundColor.setEnabled(true);
			
			gui.btnApply.setEnabled(ico.issuerImage() != null);
			
			gui.icons.setIssuerIcon(ico.issuerImage());
			gui.txtUrl.setText(ico.issuerName() != null ? ico.issuerName() : "");
			gui.txtUserUrl.setEnabled(ico.issuerName() != null);
			
			gui.icons.setUserIcon(ico.userImage());
			gui.txtUserUrl.setText(ico.userName() != null ? ico.userName() : "");
		});
	}
	
	@SneakyThrows
	private boolean setIconFromFile(List<File> files)
	{
		return setIconFromUrl(files.get(0).toURI().toURL().toString());
	}
	
	@SneakyThrows
	private boolean setUserIconFromFile(List<File> files)
	{
		return setUserIconFromUrl(files.get(0).toURI().toURL().toString());
	}
	
	private boolean setIconFromUrl(String address)
	{
		try
		{
			return setIcon(address, ImageIO.read(new URL(address)));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean setUserIconFromUrl(String address)
	{
		try
		{
			return setUserIcon(address, ImageIO.read(new URL(address)));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	void browse(ActionEvent _ae)
	{
		final var file = browseFile();
		if (file != null)
			setIconFromUrl(file);
	}
	
	void browseUser(ActionEvent _ae)
	{
		final var file = browseFile();
		if (file != null)
			setUserIconFromUrl(file);
	}
	
	private String browseFile()
	{
		return gui.map(gui -> {
			final var chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(false);
			chooser.setFileFilter(new FileNameExtensionFilter("Image Files", ImageIO.getReaderFileSuffixes()));
			if (chooser.showOpenDialog(gui) != JFileChooser.APPROVE_OPTION)
				return null;
			try
			{
				return chooser.getSelectedFile().toURI().toURL().toString();
			}
			catch (MalformedURLException e)
			{
				e.printStackTrace();
				return null;
			}
		}).get(() -> null);
	}
	
	void userIconEnabled(ActionEvent ae)
	{
		gui.if_(gui -> {
			final var		source	= (JCheckBox) ae.getSource();
			final boolean	enabled	= source.isSelected();
			gui.btnPasteUser.setEnabled(enabled);
			gui.btnBrowseUser.setEnabled(enabled);
			gui.btnChooseUser.setEnabled(enabled);
			if (!enabled)
			{
				this.icon = new IconData(
					this.icon.issuerName(),
					this.icon.issuerImage(),
					null,
					null,
					this.icon.bgColor());
				setIcon(this.icon);
			}
		});
	}
	
	void backgroundColorEnabled(ActionEvent ae)
	{
		gui.if_(gui -> {
			final var		source	= (JCheckBox) ae.getSource();
			final boolean	enabled	= source.isSelected();
			gui.lblBgColor.setVisible(enabled);
			setBgColor(enabled ? lastBG : null);
		});
	}
	
	final MouseListener colorListener = new MouseAdapter()
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
						res -> res.getReturnValue().if_(color -> setBgColor(color)),
						lastBG,
						mousePos)));
		}
	};
	
	void abort(ActionEvent _ae)
	{
		gui.if_(SetIcon::dispose);
	}
	
	void apply(ActionEvent _ae)
	{
		final var gce = new GuiCloseEvent<>(Result.SUCCESS, Opt.empty(), icon);
		gui.if_(gui -> gui.dispose(gce));
	}
	
	@Override
	protected void disposeAction()
	{
		gui.if_(TinyTotp.windowStates.setIcon::persistBounds);
	}
}
