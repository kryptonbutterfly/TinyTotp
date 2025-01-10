package kryptonbutterfly.totp.ui.seticon;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.TotpConstants;
import kryptonbutterfly.totp.misc.Assets;
import kryptonbutterfly.totp.ui.components.ComboIcon;
import kryptonbutterfly.util.swing.ApplyAbortPanel;
import kryptonbutterfly.util.swing.ObservableDialog;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;

@SuppressWarnings("serial")
public final class SetIcon extends ObservableDialog<BL, IconData, IconData> implements TotpConstants
{
	private static final String	BROWSE	= "Browse …";
	private static final String	SELECT	= "Select";
	
	final JTextField	txtUrl		= new JTextField();
	final JTextField	txtUserUrl	= new JTextField();
	final ComboIcon		icons		= new ComboIcon();
	
	private final JPanel	applyAbortPanel	= new JPanel(new BorderLayout(0, 0));
	private final JButton	btnPaste		= new JButton();
	private final JButton	btnBrowse		= new JButton(BROWSE);
	
	final JButton	btnPasteUser	= new JButton();
	final JButton	btnBrowseUser	= new JButton(BROWSE);
	
	private final JButton	btnChoose		= new JButton(SELECT);
	final JButton			btnChooseUser	= new JButton(SELECT);
	
	private final JLabel	lblIssuerIcon	= new JLabel("Issuer icon");
	final JCheckBox			chckbxUserIcon	= new JCheckBox("User icon");
	
	final JCheckBox	chckbxBackgroundColor	= new JCheckBox("Background Color");
	final JLabel	lblBgColor				= new JLabel();
	
	JButton btnApply;
	
	public SetIcon(
		Window owner,
		ModalityType modality,
		IconData data,
		Consumer<GuiCloseEvent<IconData>> closeListener)
	{
		super(owner, modality, closeListener, data);
		TinyTotp.windowStates.setIcon.setBounds(this);
		setTitle("Set Icon");
		setIconImage(Assets.MISSING_ICON_IMG);
		
		if (data != null)
		{
			if (data.issuerImage() != null)
				icons.setIssuerIcon(data.issuerImage());
			if (data.userImage() != null)
				icons.setUserIcon(data.userImage());
		}
		
		getContentPane().add(applyAbortPanel, BorderLayout.SOUTH);
		applyAbortPanel.add(new JSeparator(), BorderLayout.NORTH);
		
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths	= new int[] { 15, 15, 15 };
		gbl_panel.rowHeights	= new int[] { 15, 15, 15 };
		gbl_panel.columnWeights	= new double[] { 0.5, 0.0, 0.5 };
		gbl_panel.rowWeights	= new double[] { 0.5, 0.0, 0.5 };
		final var panel = new JPanel(gbl_panel);
		getContentPane().add(panel, BorderLayout.CENTER);
		
		final var gbc_glue = new GridBagConstraints();
		gbc_glue.fill	= GridBagConstraints.BOTH;
		gbc_glue.insets	= new Insets(0, 0, 0, 5);
		gbc_glue.gridx	= 0;
		gbc_glue.gridy	= 0;
		panel.add(Box.createGlue(), gbc_glue);
		
		final var gbc_glue_1 = new GridBagConstraints();
		gbc_glue_1.fill		= GridBagConstraints.BOTH;
		gbc_glue_1.insets	= new Insets(0, 0, 0, 5);
		gbc_glue_1.gridx	= 1;
		gbc_glue_1.gridy	= 0;
		panel.add(Box.createGlue(), gbc_glue_1);
		
		final var gbc_glue_2 = new GridBagConstraints();
		gbc_glue_2.fill		= GridBagConstraints.BOTH;
		gbc_glue_2.insets	= new Insets(0, 0, 0, 5);
		gbc_glue_2.gridx	= 2;
		gbc_glue_2.gridy	= 0;
		panel.add(Box.createGlue(), gbc_glue_2);
		
		final var gbc_glue_3 = new GridBagConstraints();
		gbc_glue_3.fill		= GridBagConstraints.BOTH;
		gbc_glue_3.insets	= new Insets(0, 0, 0, 5);
		gbc_glue_3.gridx	= 0;
		gbc_glue_3.gridy	= 1;
		panel.add(Box.createGlue(), gbc_glue_3);
		
		final var gbc_icons = new GridBagConstraints();
		gbc_icons.fill		= GridBagConstraints.BOTH;
		gbc_icons.insets	= new Insets(0, 0, 0, 5);
		gbc_icons.gridx		= 1;
		gbc_icons.gridy		= 1;
		panel.add(icons, gbc_icons);
		
		final var gbc_glue_4 = new GridBagConstraints();
		gbc_glue_4.fill		= GridBagConstraints.BOTH;
		gbc_glue_4.insets	= new Insets(0, 0, 0, 5);
		gbc_glue_4.gridx	= 2;
		gbc_glue_4.gridy	= 1;
		panel.add(Box.createGlue(), gbc_glue_4);
		
		final var gbc_glue_5 = new GridBagConstraints();
		gbc_glue_5.fill		= GridBagConstraints.BOTH;
		gbc_glue_5.insets	= new Insets(0, 0, 0, 5);
		gbc_glue_5.gridx	= 0;
		gbc_glue_5.gridy	= 2;
		panel.add(Box.createGlue(), gbc_glue_5);
		
		final var gbc_glue_6 = new GridBagConstraints();
		gbc_glue_6.fill		= GridBagConstraints.BOTH;
		gbc_glue_6.insets	= new Insets(0, 0, 0, 5);
		gbc_glue_6.gridx	= 1;
		gbc_glue_6.gridy	= 2;
		panel.add(Box.createGlue(), gbc_glue_6);
		
		final var gbc_glue_7 = new GridBagConstraints();
		gbc_glue_7.fill		= GridBagConstraints.BOTH;
		gbc_glue_7.gridx	= 2;
		gbc_glue_7.gridy	= 2;
		panel.add(Box.createGlue(), gbc_glue_7);
		
		final var verticalBox = Box.createVerticalBox();
		getContentPane().add(verticalBox, BorderLayout.NORTH);
		
		final var gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths	= new int[] { 50, 110, 20, 20, 0 };
		gbl_panel_3.rowHeights		= new int[] { 34, 34, 0 };
		gbl_panel_3.columnWeights	= new double[] { 0.0, 65535.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_3.rowWeights		= new double[] { 0.0, 0.0, Double.MIN_VALUE };
		
		final var panel_3 = new JPanel(gbl_panel_3);
		verticalBox.add(panel_3);
		
		final var gbc_lblIssuerIcon = new GridBagConstraints();
		gbc_lblIssuerIcon.fill		= GridBagConstraints.VERTICAL;
		gbc_lblIssuerIcon.insets	= new Insets(0, 0, 5, 5);
		gbc_lblIssuerIcon.gridx		= 0;
		gbc_lblIssuerIcon.gridy		= 0;
		panel_3.add(lblIssuerIcon, gbc_lblIssuerIcon);
		
		final var gbc_txtUrl = new GridBagConstraints();
		gbc_txtUrl.fill		= GridBagConstraints.BOTH;
		gbc_txtUrl.insets	= new Insets(0, 0, 5, 5);
		gbc_txtUrl.gridx	= 1;
		gbc_txtUrl.gridy	= 0;
		panel_3.add(txtUrl, gbc_txtUrl);
		
		txtUrl.setColumns(10);
		txtUrl.setEditable(false);
		if (data.issuerName() != null)
			txtUrl.setText(data.issuerName());
		
		final var gbc_btnChooseIcon = new GridBagConstraints();
		gbc_btnChooseIcon.fill		= GridBagConstraints.VERTICAL;
		gbc_btnChooseIcon.insets	= new Insets(0, 0, 5, 5);
		gbc_btnChooseIcon.gridx		= 2;
		gbc_btnChooseIcon.gridy		= 0;
		panel_3.add(btnChoose, gbc_btnChooseIcon);
		btnChoose.setToolTipText("");
		
		final var gbc_btnPaste = new GridBagConstraints();
		gbc_btnPaste.fill	= GridBagConstraints.VERTICAL;
		gbc_btnPaste.insets	= new Insets(0, 0, 5, 5);
		gbc_btnPaste.gridx	= 3;
		gbc_btnPaste.gridy	= 0;
		panel_3.add(btnPaste, gbc_btnPaste);
		btnPaste.setToolTipText("Paste URL");
		btnPaste.setIcon(Assets.getClipboardByBackground(getBackground()));
		
		final var gbc_btnBrowse = new GridBagConstraints();
		gbc_btnBrowse.fill		= GridBagConstraints.VERTICAL;
		gbc_btnBrowse.insets	= new Insets(0, 0, 5, 0);
		gbc_btnBrowse.gridx		= 4;
		gbc_btnBrowse.gridy		= 0;
		panel_3.add(btnBrowse, gbc_btnBrowse);
		
		final var gbc_chckbxUserIcon = new GridBagConstraints();
		gbc_chckbxUserIcon.fill		= GridBagConstraints.VERTICAL;
		gbc_chckbxUserIcon.insets	= new Insets(0, 0, 0, 5);
		gbc_chckbxUserIcon.gridx	= 0;
		gbc_chckbxUserIcon.gridy	= 1;
		panel_3.add(chckbxUserIcon, gbc_chckbxUserIcon);
		chckbxUserIcon.setSelected(data.userName() != null);
		
		chckbxUserIcon.setEnabled(data.issuerName() != null);
		txtUserUrl.setEnabled(data.userName() != null);
		txtUserUrl.setEditable(false);
		txtUserUrl.setColumns(10);
		if (data.userName() != null)
			txtUserUrl.setText(data.userName());
		
		final var gbc_txtUserUrl = new GridBagConstraints();
		gbc_txtUserUrl.fill		= GridBagConstraints.BOTH;
		gbc_txtUserUrl.insets	= new Insets(0, 0, 0, 5);
		gbc_txtUserUrl.gridx	= 1;
		gbc_txtUserUrl.gridy	= 1;
		panel_3.add(txtUserUrl, gbc_txtUserUrl);
		
		final var gbc_btnChooseUser = new GridBagConstraints();
		gbc_btnChooseUser.fill		= GridBagConstraints.VERTICAL;
		gbc_btnChooseUser.insets	= new Insets(0, 0, 0, 5);
		gbc_btnChooseUser.gridx		= 2;
		gbc_btnChooseUser.gridy		= 1;
		panel_3.add(btnChooseUser, gbc_btnChooseUser);
		btnChooseUser.setEnabled(data.userName() != null);
		
		final var gbc_btnPasteUser = new GridBagConstraints();
		gbc_btnPasteUser.fill	= GridBagConstraints.VERTICAL;
		gbc_btnPasteUser.insets	= new Insets(0, 0, 0, 5);
		gbc_btnPasteUser.gridx	= 3;
		gbc_btnPasteUser.gridy	= 1;
		btnPasteUser.setEnabled(data.userName() != null);
		panel_3.add(btnPasteUser, gbc_btnPasteUser);
		btnPasteUser.setIcon(Assets.getClipboardByBackground(getBackground()));
		
		final var gbc_btnBrowse_1 = new GridBagConstraints();
		gbc_btnBrowse_1.fill	= GridBagConstraints.VERTICAL;
		gbc_btnBrowse_1.gridx	= 4;
		gbc_btnBrowse_1.gridy	= 1;
		panel_3.add(btnBrowseUser, gbc_btnBrowse_1);
		btnBrowseUser.setEnabled(data.userName() != null);
		
		final var panel_1 = new JPanel(new BorderLayout(0, 0));
		verticalBox.add(panel_1);
		
		final var panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.WEST);
		chckbxBackgroundColor.setEnabled(data.issuerName() != null);
		chckbxBackgroundColor.setSelected(data.bgColor() != null);
		panel_2.add(chckbxBackgroundColor);
		lblBgColor.setVisible(data.bgColor() != null);
		
		panel_2.add(lblBgColor);
		
		businessLogic.if_(bl -> this.init(bl, data));
		setVisible(true);
	}
	
	private void init(BL bl, IconData data)
	{
		
		btnChoose.addActionListener(bl::chooseIssuerIcon);
		btnPaste.addActionListener(bl::pasteImage);
		btnBrowse.addActionListener(bl::browse);
		
		btnChooseUser.addActionListener(bl::chooseUserIcon);
		btnPasteUser.addActionListener(bl::pasteUserImage);
		btnBrowseUser.addActionListener(bl::browseUser);
		
		chckbxUserIcon.addActionListener(bl::userIconEnabled);
		
		txtUrl.addKeyListener(bl.escapeListener);
		txtUserUrl.addKeyListener(bl.escapeListener);
		
		btnChoose.addKeyListener(bl.escapeListener);
		btnChooseUser.addKeyListener(bl.escapeListener);
		
		btnPaste.addKeyListener(bl.escapeListener);
		btnBrowse.addKeyListener(bl.escapeListener);
		
		btnPasteUser.addKeyListener(bl.escapeListener);
		btnBrowseUser.addKeyListener(bl.escapeListener);
		btnChooseUser.addKeyListener(bl.escapeListener);
		
		chckbxUserIcon.addKeyListener(bl.escapeListener);
		
		chckbxBackgroundColor.addActionListener(bl::backgroundColorEnabled);
		chckbxBackgroundColor.addKeyListener(bl.escapeListener);
		
		lblBgColor.addMouseListener(bl.colorListener);
		
		bl.setBgColor(data.bgColor());
		
		final var applyAbort = new ApplyAbortPanel(BUTTON_ABORT, bl::abort, BUTTON_APPLY, bl::apply);
		getContentPane().add(applyAbort, BorderLayout.SOUTH);
		btnApply = applyAbort.btnButton2;
		
		applyAbort.btnButton1.addKeyListener(bl.escapeListener);
		applyAbort.btnButton2.addKeyListener(bl.escapeListener);
	}
	
	@Override
	protected BL createBusinessLogic(IconData data)
	{
		return new BL(this, data);
	}
}
