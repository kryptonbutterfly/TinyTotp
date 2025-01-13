package kryptonbutterfly.totp.ui.prefs.cat;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.misc.Utils;

public class Time implements PrefsCat
{
	private final JScrollPane	scrollPane;
	private final JCheckBox		chckbxUseTimeServer	= new JCheckBox("Use Timeserver");
	private final JTextField	textServerAddress	= new JTextField();
	private final JPanel		panelTimeServer		= new JPanel();
	
	private final JLabel	lblTimeServer	= new JLabel("NTP Server");
	private final JButton	btnTestServer	= new JButton("Test Server");
	private final JPanel	panel_2			= new JPanel();
	private final Component	glue			= Box.createGlue();
	private final Component	glue_1			= Box.createGlue();
	
	public Time()
	{
		final var panel = new JPanel(new BorderLayout(5, 5));
		this.scrollPane = new JScrollPane(panel);
		
		final var verticalBox = Box.createVerticalBox();
		panel.add(verticalBox, BorderLayout.NORTH);
		
		final var panel_1 = new JPanel();
		verticalBox.add(panel_1);
		panel_1.setLayout(new BorderLayout(5, 5));
		
		chckbxUseTimeServer.setToolTipText("Use a timeserver instead of the system time to generate totp passwords.");
		panel_1.add(chckbxUseTimeServer, BorderLayout.NORTH);
		panelTimeServer
			.setBorder(new TitledBorder(null, "Timeserver", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panel_1.add(panelTimeServer, BorderLayout.CENTER);
		panelTimeServer.setLayout(new BorderLayout(5, 5));
		
		panelTimeServer.add(glue, BorderLayout.WEST);
		
		panelTimeServer.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(5, 5));
		panel_2.add(lblTimeServer, BorderLayout.WEST);
		panel_2.add(textServerAddress, BorderLayout.CENTER);
		textServerAddress.setColumns(10);
		panel_2.add(btnTestServer, BorderLayout.EAST);
		
		panelTimeServer.add(glue_1, BorderLayout.EAST);
		btnTestServer.addActionListener(this::testServer);
		
		panel_1.add(new JSeparator(), BorderLayout.SOUTH);
		chckbxUseTimeServer.addActionListener(this::toggleTimeServer);
		
		final var verticalGlue = Box.createVerticalGlue();
		panel.add(verticalGlue, BorderLayout.CENTER);
		
	}
	
	private void toggleTimeServer(ActionEvent _ae)
	{
		final boolean enabled = chckbxUseTimeServer.isSelected();
		panelTimeServer.setEnabled(enabled);
		lblTimeServer.setEnabled(enabled);
		btnTestServer.setEnabled(enabled);
		textServerAddress.setEnabled(enabled);
	}
	
	private void testServer(ActionEvent _ae)
	{
		EventQueue.invokeLater(() -> {
			scrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try
			{
				final long		offset	= Utils.getNtpTimeOffset(textServerAddress.getText());
				final String	state	= offset < 0 ? "ahead" : "behind";
				final var		date	= new Date(System.currentTimeMillis() + offset);
				final var		msg		= """
						Connection to NTP Server successful.\n Your system is %s by %dms.\nThe adjusted time is %s.
						""".formatted(state, offset, date);
				JOptionPane.showMessageDialog(scrollPane, msg, "Connection Success", JOptionPane.PLAIN_MESSAGE);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				JOptionPane
					.showMessageDialog(scrollPane, e.getMessage(), "Connection Failed", JOptionPane.ERROR_MESSAGE);
			}
			scrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		});
	}
	
	@Override
	public JComponent content()
	{
		return this.scrollPane;
	}
	
	@Override
	public String prefsPath()
	{
		return "Time";
	}
	
	@Override
	public void init()
	{
		chckbxUseTimeServer.setSelected(TinyTotp.config.useTimeServer);
		textServerAddress.setText(TinyTotp.config.ntpServer);
		toggleTimeServer(null);
	}
	
	@Override
	public void persist()
	{
		TinyTotp.config.useTimeServer	= chckbxUseTimeServer.isSelected();
		TinyTotp.config.ntpServer		= textServerAddress.getText();
	}
}