package kryptonbutterfly.totp.ui.prefs.cat;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;

import kryptonbutterfly.totp.misc.DocumentAdapter;
import kryptonbutterfly.totp.misc.Password;

public final class PasswordCat implements PrefsCat
{
	private final JScrollPane		scrollPane;
	private final JPasswordField	pwdCurrentPassword		= new JPasswordField();
	private final JPasswordField	pwdConfirmNewPassword	= new JPasswordField();
	private final JPasswordField	pwdNewPassword			= new JPasswordField();
	private final JLabel			lblPasswordMessage		= new JLabel();
	private final JButton			btnChangePassword		= new JButton("Change");
	
	private final Password password;
	
	public PasswordCat(KeyListener escapeKeyListener, Password password, Consumer<Runnable> addSelectionListener)
	{
		this.password = password;
		
		final var panel = new JPanel(new BorderLayout(0, 0));
		this.scrollPane = new JScrollPane(panel);
		
		Box verticalBox = Box.createVerticalBox();
		panel.add(verticalBox, BorderLayout.NORTH);
		
		final var lblChangePassword = new JLabel("Change password");
		lblChangePassword.setAlignmentX(0.5f);
		verticalBox.add(lblChangePassword);
		lblChangePassword.setHorizontalAlignment(SwingConstants.CENTER);
		
		Component verticalStrut = Box.createVerticalStrut(5);
		verticalBox.add(verticalStrut);
		
		final var panel_2 = new JPanel();
		verticalBox.add(panel_2);
		panel_2.setLayout(new GridLayout(0, 2, 5, 5));
		
		final var lblCurrentPassword = new JLabel("Current Password");
		lblCurrentPassword.setHorizontalAlignment(SwingConstants.TRAILING);
		panel_2.add(lblCurrentPassword);
		
		panel_2.add(pwdCurrentPassword);
		
		final var lblNewPassword = new JLabel("New Password");
		lblNewPassword.setHorizontalAlignment(SwingConstants.TRAILING);
		panel_2.add(lblNewPassword);
		
		panel_2.add(pwdNewPassword);
		
		final var lblConfirmNewPassword = new JLabel("Confirm New Password");
		lblConfirmNewPassword.setHorizontalAlignment(SwingConstants.TRAILING);
		panel_2.add(lblConfirmNewPassword);
		
		panel_2.add(pwdConfirmNewPassword);
		
		Component verticalStrut_1 = Box.createVerticalStrut(5);
		verticalBox.add(verticalStrut_1);
		lblPasswordMessage.setAlignmentX(0.5f);
		verticalBox.add(lblPasswordMessage);
		
		Component verticalStrut_2 = Box.createVerticalStrut(5);
		verticalBox.add(verticalStrut_2);
		btnChangePassword.setAlignmentX(0.5f);
		verticalBox.add(btnChangePassword);
		
		btnChangePassword.setEnabled(false);
		btnChangePassword.addActionListener(this::changePassword);
		
		panel.add(Box.createVerticalGlue(), BorderLayout.CENTER);
		
		final var pwChangeListener = new DocumentAdapter()
		{
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				validate();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				validate();
			}
		};
		pwdNewPassword.getDocument().addDocumentListener(pwChangeListener);
		pwdCurrentPassword.getDocument().addDocumentListener(pwChangeListener);
		pwdConfirmNewPassword.getDocument().addDocumentListener(pwChangeListener);
		
		pwdCurrentPassword.addKeyListener(escapeKeyListener);
		pwdNewPassword.addKeyListener(escapeKeyListener);
		pwdConfirmNewPassword.addKeyListener(escapeKeyListener);
		btnChangePassword.addKeyListener(escapeKeyListener);
		addSelectionListener.accept(this::clear);
	}
	
	private void validate()
	{
		boolean canChangePassword = true;
		
		if (!Arrays.equals(password.password(), pwdCurrentPassword.getPassword()))
			canChangePassword &= false;
		
		final var	newPw			= pwdNewPassword.getPassword();
		final var	confirmNewPw	= pwdConfirmNewPassword.getPassword();
		if (!Arrays.equals(newPw, confirmNewPw))
		{
			lblPasswordMessage.setText("The passwords do not match.");
			canChangePassword &= false;
		}
		else
		{
			final var evaluation = Password.validate(pwdNewPassword.getPassword());
			canChangePassword &= evaluation.isValid();
			lblPasswordMessage.setText(evaluation.message());
		}
		
		this.btnChangePassword.setEnabled(canChangePassword);
	}
	
	private void changePassword(ActionEvent _ae)
	{
		this.password.password(pwdNewPassword.getPassword());
		pwdNewPassword.setText("");
		pwdCurrentPassword.setText("");
		pwdConfirmNewPassword.setText("");
	}
	
	private void clear()
	{
		pwdNewPassword.setText("");
		pwdCurrentPassword.setText("");
		pwdConfirmNewPassword.setText("");
	}
	
	@Override
	public JComponent content()
	{
		return this.scrollPane;
	}
	
	@Override
	public void init()
	{
		// nothing to do here!
	}
	
	@Override
	public void persist()
	{
		// nothing to do here!
	}
	
	@Override
	public String prefsPath()
	{
		return "Password";
	}
}
