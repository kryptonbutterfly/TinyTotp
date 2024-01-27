package kryptonbutterfly.totp.ui.qrexport;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.misc.Assets;
import kryptonbutterfly.util.swing.ObservableDialog;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;

@SuppressWarnings("serial")
public class QrExportGui extends ObservableDialog<BL, Void, Void>
{
	private final JButton btnOk = new JButton("ok");
	
	public QrExportGui(
		Window owner,
		ModalityType modality,
		Consumer<GuiCloseEvent<Void>> closeListener,
		BufferedImage qr)
	{
		super(owner, modality, closeListener);
		TinyTotp.windowStates.qrExport.setBounds(this);
		setTitle("Export Secret");
		setIconImage(Assets.getQr16ByBackground(getBackground()).getImage());
		
		add(new JLabel(new ImageIcon(qr)), BorderLayout.CENTER);
		
		final var footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(footerPanel, BorderLayout.SOUTH);
		footerPanel.add(btnOk);
		
		businessLogic.if_(this::init);
		
		setVisible(true);
	}
	
	@Override
	protected BL createBusinessLogic(Void args)
	{
		return new BL(this);
	}
	
	private void init(BL bl)
	{
		btnOk.addActionListener(bl::ok);
		btnOk.addKeyListener(bl.escapeListener);
	}
}
