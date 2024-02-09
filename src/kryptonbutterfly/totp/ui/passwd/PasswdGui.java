package kryptonbutterfly.totp.ui.passwd;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import kryptonbutterfly.util.swing.ObservableGui;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import kryptonbutterfly.util.swing.state.WindowState;

@SuppressWarnings("serial")
public final class PasswdGui extends ObservableGui<BL, char[], WindowState>
{
	final JPasswordField passwordField = new JPasswordField();
	
	public PasswdGui()
	{
		super(null);
		setBounds(100, 100, 300, 100);
		getContentPane().add(passwordField, BorderLayout.CENTER);
		
		JPanel		panel		= new JPanel();
		FlowLayout	flowLayout	= (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton btnUnlock = new JButton("unlock");
		panel.add(btnUnlock);
	}
	
	public PasswdGui(Consumer<GuiCloseEvent<char[]>> closeListener, WindowState prefs)
	{
		super(closeListener, prefs);
		
		setResizable(false);
		prefs.setBounds(this);
		setTitle("Unlock TOTP Keys");
		
		businessLogic.if_(this::init);
	}
	
	@Override
	protected BL createBusinessLogic(WindowState prefs)
	{
		return new BL(this, prefs);
	}
	
	private void init(BL bl)
	{
		add(passwordField, BorderLayout.CENTER);
		passwordField.addKeyListener(bl.enterListener(this));
		
		final var panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(panel, BorderLayout.SOUTH);
		final var btnUnlock = new JButton("unlock");
		panel.add(btnUnlock);
		btnUnlock.addActionListener(bl::unlock);
	}
}