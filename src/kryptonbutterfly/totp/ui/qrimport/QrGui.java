package kryptonbutterfly.totp.ui.qrimport;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.misc.Assets;
import kryptonbutterfly.totp.misc.Margin;
import kryptonbutterfly.totp.ui.misc.AutoScalingImage;
import kryptonbutterfly.util.swing.ObservableDialog;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;

@SuppressWarnings("serial")
public final class QrGui extends ObservableDialog<BL, String, Void>
{
	static final String	SCAN_CARD	= "qr-scan-card";
	static final String	MENU_CARD	= "qr-menu-card";
	
	final CardLayout	cardLayout	= new CardLayout(0, 0);
	final JPanel		cardPanel	= new JPanel(cardLayout);
	
	private final JButton	btnAbort		= new JButton("abort");
	final JButton			btnAbortScan	= new JButton("abort");
	
	private final JButton	btnScanFile		= new JButton("open File");
	final JButton			btnScanCamera	= new JButton("scan Camera");
	
	final AutoScalingImage cameraDisplay = new AutoScalingImage(new Margin(5));
	
	public QrGui(Window owner, ModalityType modality, Consumer<GuiCloseEvent<String>> closeListener, String title)
	{
		super(owner, modality, closeListener);
		TinyTotp.windowStates.qrScan.setBounds(this);
		setTitle(title);
		setIconImage(Assets.getQr16ByBackground(getBackground()).getImage());
		getContentPane().setLayout(new BorderLayout());
		
		getContentPane().add(cardPanel, BorderLayout.CENTER);
		
		cardPanel.setBackground(Color.GREEN);
		{
			final var menuPanel = new JPanel(new BorderLayout());
			cardPanel.add(menuPanel);
			cardLayout.addLayoutComponent(menuPanel, MENU_CARD);
			final var centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			menuPanel.add(centerPanel, BorderLayout.CENTER);
			centerPanel.add(btnScanFile);
			centerPanel.add(btnScanCamera);
			btnScanCamera.setIcon(Assets.getCameraByBackground(getBackground()));
			
			final var abortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			abortPanel.add(btnAbort);
			final var bottomPanel = new JPanel(new BorderLayout());
			bottomPanel.add(abortPanel, BorderLayout.CENTER);
			bottomPanel.add(new JSeparator(), BorderLayout.NORTH);
			menuPanel.add(bottomPanel, BorderLayout.SOUTH);
		}
		
		{
			final var scanPanel = new JPanel(new BorderLayout());
			cardPanel.add(scanPanel);
			cardLayout.addLayoutComponent(scanPanel, SCAN_CARD);
			cameraDisplay.setHorizontalAlignment(SwingConstants.CENTER);
			cameraDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);
			scanPanel.add(cameraDisplay, BorderLayout.CENTER);
			final var abortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			abortPanel.add(btnAbortScan);
			final var bottomPanel = new JPanel(new BorderLayout());
			bottomPanel.add(abortPanel, BorderLayout.CENTER);
			bottomPanel.add(new JSeparator(), BorderLayout.NORTH);
			scanPanel.add(bottomPanel, BorderLayout.SOUTH);
		}
		
		businessLogic.if_(this::init);
		
		setVisible(true);
		
		cardLayout.show(cardPanel, MENU_CARD);
	}
	
	@Override
	protected BL createBusinessLogic(Void args)
	{
		return new BL(this);
	}
	
	private void init(BL bl)
	{
		btnAbort.addKeyListener(bl.escapeMenuListener);
		btnAbort.addActionListener(bl::abort);
		
		btnAbortScan.addKeyListener(bl.escapeScanListener);
		btnAbortScan.addActionListener(bl::abortScan);
		
		btnScanCamera.addKeyListener(bl.escapeMenuListener);
		btnScanCamera.addActionListener(bl::scanCamera);
		
		btnScanFile.addKeyListener(bl.escapeMenuListener);
		btnScanFile.addActionListener(bl::scanFile);
	}
}
