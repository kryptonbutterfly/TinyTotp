package d.t.totp.ui.add.manual;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.apache.commons.codec.binary.Base32;

import d.t.totp.TotpConstants;
import d.t.totp.TinyTotp;
import d.t.totp.misc.Assets;
import d.t.totp.prefs.TotpCategory;
import d.t.totp.prefs.TotpEntry;
import de.tinycodecrank.util.swing.ApplyAbortPanel;
import de.tinycodecrank.util.swing.ObservableDialog;
import de.tinycodecrank.util.swing.events.GuiCloseEvent;

@SuppressWarnings("serial")
public final class AddKey extends ObservableDialog<BL, TotpEntry, char[]> implements TotpConstants
{
	JTextField			txtAccountname;
	JTextField			txtSecretkey;
	JTextField			txtIssuer;
	JComboBox<TotpCategory>	comboBoxCategory;
	JLabel				lblIcon;
	String				iconName			= null;
	JSpinner			spinnerTimeFrame	= new JSpinner(new SpinnerNumberModel(30, 0, 180, 5));
	JSpinner			spinnerTotpLength	= new JSpinner(new SpinnerNumberModel(6, 6, 10, 1));
	
	public AddKey(
		Window owner,
		ModalityType modality,
		Consumer<GuiCloseEvent<TotpEntry>> closeListener,
		char[] password,
		String title)
	{
		this(owner, modality, closeListener, password, title, null);
	}
	
	public AddKey(
		Window owner,
		ModalityType modality,
		Consumer<GuiCloseEvent<TotpEntry>> closeListener,
		char[] password,
		String title,
		TotpEntry original)
	{
		super(owner, modality, closeListener, password);
		TinyTotp.config.createDialog.setBounds(this);
		setTitle(title);
		
		businessLogic.if_(this::init);
		
		if (original != null)
		{
			txtAccountname.setText(original.accountName);
			txtSecretkey.setText(new Base32().encodeAsString(original.decryptSecret(password)));
			txtIssuer.setText(original.issuerName);
			TinyTotp.config.getCategoryByName(original.category).if_(comboBoxCategory::setSelectedItem);
			iconName = original.icon;
			TinyTotp.imageCache.getImage(iconName).map(ImageIcon::new).if_(lblIcon::setIcon);
			spinnerTimeFrame.setValue(original.totpValidForSeconds);
			spinnerTotpLength.setValue(original.totpLength);
			
		}
		setVisible(true);
	}
	
	@Override
	protected BL createBusinessLogic(char[] password)
	{
		return new BL(this, password);
	}
	
	private void init(BL bl)
	{
		Box			verticalBox	= Box.createVerticalBox();
		final var	gridPanel	= new JPanel(new GridLayout(0, 2, 5, 5));
		verticalBox.add(gridPanel);
		add(verticalBox, BorderLayout.CENTER);
		
		JLabel lblAccountName = new JLabel(LABEL_ACCOUNT_NAME);
		gridPanel.add(lblAccountName);
		
		txtAccountname = new JTextField();
		gridPanel.add(txtAccountname);
		txtAccountname.setColumns(10);
		txtAccountname.addKeyListener(bl.cycleFocus());
		txtAccountname.addKeyListener(bl.escapeListener());
		
		JLabel lblIssuer = new JLabel(LABEL_ISSUER);
		gridPanel.add(lblIssuer);
		
		txtIssuer = new JTextField();
		gridPanel.add(txtIssuer);
		txtIssuer.setColumns(10);
		txtIssuer.addKeyListener(bl.cycleFocus());
		txtIssuer.addKeyListener(bl.escapeListener());
		
		JLabel lblSecretKey = new JLabel(LABEL_SECRET_KEY);
		gridPanel.add(lblSecretKey);
		
		txtSecretkey = new JTextField();
		gridPanel.add(txtSecretkey);
		txtSecretkey.setColumns(10);
		txtSecretkey.addKeyListener(bl.cycleFocus());
		txtSecretkey.addKeyListener(bl.escapeListener());
		
		{
			gridPanel.add(new JLabel(LABEL_PASSWORD_INTERVAL));
			gridPanel.add(spinnerTimeFrame);
			spinnerTimeFrame.addKeyListener(bl.cycleFocus());
			spinnerTimeFrame.addKeyListener(bl.escapeListener());
			
			gridPanel.add(new JLabel(LABEL_PASSWORD_LENGTH));
			gridPanel.add(spinnerTotpLength);
			spinnerTotpLength.addKeyListener(bl.cycleFocus());
			spinnerTotpLength.addKeyListener(bl.escapeListener());
		}
		
		JLabel lblCategory = new JLabel(LABEL_CATEGORY);
		gridPanel.add(lblCategory);
		
		comboBoxCategory = new JComboBox<>(TinyTotp.config.categories.toArray(TotpCategory[]::new));
		comboBoxCategory.setRenderer(new CategoryRenderer());
		gridPanel.add(comboBoxCategory);
		
		JPanel panel = new JPanel();
		verticalBox.add(panel);
		
		lblIcon = new JLabel(Assets.MISSING_ICON);
		lblIcon.addMouseListener(bl.iconClickListener());
		panel.add(lblIcon);
		
		verticalBox.add(Box.createVerticalGlue());
		
		final var applyAbort = new ApplyAbortPanel(BUTTON_ABORT, bl::abort, BUTTON_APPLY, bl::apply);
		add(applyAbort, BorderLayout.SOUTH);
		
		applyAbort.btnButton1.addKeyListener(bl.escapeListener());
		applyAbort.btnButton2.addKeyListener(bl.escapeListener());
		
	}
}