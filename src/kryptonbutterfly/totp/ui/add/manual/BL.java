package kryptonbutterfly.totp.ui.add.manual;

import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.apache.commons.codec.binary.Base32;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import kryptonbutterfly.functions.void_.Consumer_;
import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.misc.UrlQueryParams;
import kryptonbutterfly.totp.misc.Utils;
import kryptonbutterfly.totp.prefs.TotpCategory;
import kryptonbutterfly.totp.prefs.TotpEntry;
import kryptonbutterfly.totp.ui.misc.KeyTypedAdapter;
import kryptonbutterfly.totp.ui.qrexport.QrExportGui;
import kryptonbutterfly.totp.ui.seticon.IconData;
import kryptonbutterfly.totp.ui.seticon.SetIcon;
import kryptonbutterfly.util.swing.Logic;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import kryptonbutterfly.util.swing.events.GuiCloseEvent.Result;

final class BL extends Logic<AddKey, char[]>
{
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
			@Override
			public void mouseClicked(MouseEvent e)
			{
				gui.if_(
					gui ->
					{
						final var iconData = new IconData(
							gui.iconName,
							gui.comboIcon.getIssuerIcon(),
							gui.userIconName,
							gui.userIconName == null ? null : gui.comboIcon.getUserIcon(),
							gui.comboIcon.getIconBG());
						
						EventQueue.invokeLater(
							() -> new SetIcon(
								gui,
								ModalityType.APPLICATION_MODAL,
								iconData,
								gce -> gce.getReturnValue().if_(data ->
								{
									gui.comboIcon.setIconBG(data.bgColor());
									gui.iconName = data.issuerName();
									gui.comboIcon.setIssuerIcon(data.issuerImage());
									gui.userIconName = data.userName();
									gui.comboIcon.setUserIcon(data.userImage());
								})));
					});
			}
		};
	}
	
	void exportQR(ActionEvent ae)
	{
		gui.if_(gui -> {
			final var	account		= gui.txtAccountname.getText();
			final var	secretKey	= gui.txtSecretkey.getText();
			final var	issuer		= gui.txtIssuer.getText();
			final var	url			= new UrlQueryParams(issuer, secretKey, account).toUrl();
			Utils.generateQr(url, Color.BLACK, Color.WHITE, 200, ErrorCorrectionLevel.H)
				.if_(
					qr -> EventQueue
						.invokeLater(() -> new QrExportGui(gui, ModalityType.APPLICATION_MODAL, Consumer_.sink(), qr)));
		});
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
			totpEntry.icon		= gui.iconName;
			totpEntry.userIcon	= gui.userIconName;
			totpEntry.iconBG	= gui.comboIcon.getIconBG();
			
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