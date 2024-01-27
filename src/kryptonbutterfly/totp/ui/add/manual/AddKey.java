package kryptonbutterfly.totp.ui.add.manual;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.apache.commons.codec.binary.Base32;

import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.TotpConstants;
import kryptonbutterfly.totp.misc.Assets;
import kryptonbutterfly.totp.misc.UrlQueryParams;
import kryptonbutterfly.totp.prefs.TotpCategory;
import kryptonbutterfly.totp.prefs.TotpEntry;
import kryptonbutterfly.util.swing.ApplyAbortPanel;
import kryptonbutterfly.util.swing.ObservableDialog;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;

@SuppressWarnings("serial")
public final class AddKey extends ObservableDialog<BL, TotpEntry, char[]> implements TotpConstants
{
	final JTextField		txtAccountname		= new JTextField();
	final JTextField		txtSecretkey		= new JTextField();
	final JTextField		txtIssuer			= new JTextField();
	JComboBox<TotpCategory>	comboBoxCategory;
	final JLabel			lblIcon				= new JLabel();
	String					iconName			= null;
	final JSpinner			spinnerTimeFrame	= new JSpinner(new SpinnerNumberModel(30, 0, 180, 5));
	final JSpinner			spinnerTotpLength	= new JSpinner(new SpinnerNumberModel(6, 6, 10, 1));
	
	final JButton btnSecretKey = new JButton();
	
	JButton btnApply;
	
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
		UrlQueryParams parsed,
		char[] password,
		String title)
	{
		super(owner, modality, closeListener, password);
		TinyTotp.windowStates.createDialog.setBounds(this);
		setTitle(title);
		
		businessLogic.if_(this::init);
		
		txtAccountname.setText(parsed.account());
		txtSecretkey.setText(parsed.secretKey());
		txtIssuer.setText(parsed.issuer());
		
		setVisible(true);
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
		TinyTotp.windowStates.createDialog.setBounds(this);
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
			
			if (original.icon != null && !original.icon.isBlank())
				lblIcon.setToolTipText(original.icon);
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
		
		getContentPane().add(verticalBox, BorderLayout.CENTER);
		{
			gridPanel.add(new JLabel(LABEL_ACCOUNT_NAME));
			
			gridPanel.add(txtAccountname);
			txtAccountname.setColumns(10);
			txtAccountname.addKeyListener(bl.enterListener);
			txtAccountname.addKeyListener(bl.escapeListener);
		}
		
		{
			gridPanel.add(new JLabel(LABEL_ISSUER));
			
			gridPanel.add(txtIssuer);
			txtIssuer.setColumns(10);
			txtIssuer.addKeyListener(bl.enterListener);
			txtIssuer.addKeyListener(bl.escapeListener);
		}
		
		{
			gridPanel.add(new JLabel(LABEL_SECRET_KEY), BorderLayout.CENTER);
			
			gridPanel.add(txtSecretkey);
			txtSecretkey.setColumns(10);
			txtSecretkey.addKeyListener(bl.enterListener);
			txtSecretkey.addKeyListener(bl.escapeListener);
		}
		
		{
			gridPanel.add(new JLabel(LABEL_PASSWORD_INTERVAL));
			
			gridPanel.add(spinnerTimeFrame);
			if (spinnerTimeFrame.getEditor() instanceof DefaultEditor editor)
			{
				editor.getTextField().addKeyListener(bl.enterListener);
				editor.getTextField().addKeyListener(bl.escapeListener);
			}
		}
		
		{
			gridPanel.add(new JLabel(LABEL_PASSWORD_LENGTH));
			
			gridPanel.add(spinnerTotpLength);
			if (spinnerTotpLength.getEditor() instanceof DefaultEditor editor)
			{
				editor.getTextField().addKeyListener(bl.enterListener);
				editor.getTextField().addKeyListener(bl.escapeListener);
			}
		}
		
		{
			gridPanel.add(new JLabel(LABEL_CATEGORY));
			
			comboBoxCategory = new JComboBox<>(TinyTotp.config.categories.toArray(TotpCategory[]::new));
			comboBoxCategory.setRenderer(new CategoryRenderer());
			gridPanel.add(comboBoxCategory);
			comboBoxCategory.addKeyListener(bl.categoryEscapeListener);
			comboBoxCategory.addPopupMenuListener(bl.categoryPopupListener);
			comboBoxCategory.addKeyListener(bl.categoryEnterListener);
		}
		
		JPanel panel = new JPanel();
		verticalBox.add(panel);
		
		panel.add(lblIcon);
		lblIcon.setIcon(Assets.MISSING_ICON);
		lblIcon.addMouseListener(bl.iconClickListener());
		lblIcon.setToolTipText(TOOLTIP_ADDKEY_IMPORT_ICON);
		
		verticalBox.add(Box.createVerticalGlue());
		
		{
			final var applyAbort = new ApplyAbortPanel(BUTTON_ABORT, bl::abort, BUTTON_APPLY, bl::apply);
			getContentPane().add(applyAbort, BorderLayout.SOUTH);
			btnApply = applyAbort.btnButton2;
			
			applyAbort.btnButton1.addKeyListener(bl.escapeListener);
			applyAbort.btnButton2.addKeyListener(bl.escapeListener);
		}
	}
}