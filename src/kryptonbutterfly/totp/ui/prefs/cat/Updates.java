package kryptonbutterfly.totp.ui.prefs.cat;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import kryptonbutterfly.checkRelease.Cadence;
import kryptonbutterfly.checkRelease.SemVer;
import kryptonbutterfly.functions.void_.Consumer_;
import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.ui.update.UpdateAvailable;

public class Updates implements PrefsCat
{
	private final JScrollPane scrollPane;
	
	private final JComboBox<Cadence>	comboCadence		= new JComboBox<>(Cadence.values());
	private final JTextField			textCurrentVersion	= new JTextField();
	
	public Updates()
	{
		final var panel = new JPanel(new BorderLayout(5, 5));
		this.scrollPane = new JScrollPane(panel);
		
		final var verticalBox = Box.createVerticalBox();
		panel.add(verticalBox, BorderLayout.NORTH);
		
		final var panel_1 = new JPanel();
		verticalBox.add(panel_1);
		panel_1.setLayout(new GridLayout(0, 2, 0, 0));
		
		final var lblCheckForUpdates = new JLabel("Check for updates:");
		panel_1.add(lblCheckForUpdates);
		
		panel_1.add(comboCadence);
		
		final var lblCurrentVersion = new JLabel("Current Version:");
		panel_1.add(lblCurrentVersion);
		
		textCurrentVersion.setEditable(false);
		textCurrentVersion.setEnabled(false);
		panel_1.add(textCurrentVersion);
		textCurrentVersion.setColumns(10);
		
		verticalBox.add(new JSeparator());
		
		final var panel_2 = new JPanel();
		verticalBox.add(panel_2);
		
		final var btnCheckVersion = new JButton("Check Version");
		btnCheckVersion.addActionListener(this::checkUpdate);
		panel_2.add(btnCheckVersion);
		
		panel.add(Box.createVerticalGlue(), BorderLayout.CENTER);
	}
	
	@Override
	public JComponent content()
	{
		return this.scrollPane;
	}
	
	@Override
	public void init()
	{
		comboCadence.setSelectedItem(TinyTotp.releaseState.cadence);
		
		if (TinyTotp.currentVersion == null)
			textCurrentVersion.setText("");
		else
			textCurrentVersion.setText(TinyTotp.currentVersion.toString());
	}
	
	@Override
	public void persist()
	{
		TinyTotp.releaseState.cadence = (Cadence) comboCadence.getSelectedItem();
	}
	
	@Override
	public String prefsPath()
	{
		return "Updates";
	}
	
	private void checkUpdate(ActionEvent ae)
	{
		final var latest = TinyTotp.queryNewest(true);
		
		if (latest != null)
		{
			final var	latestVersion	= SemVer.fromGitTag(latest.tag_name);
			final var	isNewer			= TinyTotp.currentVersion != null
					&& -1 == TinyTotp.currentVersion.compareTo(latestVersion);
			
			if (isNewer)
			{
				final var window = SwingUtilities.getWindowAncestor(scrollPane);
				EventQueue.invokeLater(
					() -> new UpdateAvailable(window, ModalityType.APPLICATION_MODAL, latest, Consumer_.sink()));
			}
			else
				JOptionPane.showMessageDialog(
					scrollPane,
					"Latest release is: " + latest.tag_name,
					"No Update Available",
					JOptionPane.PLAIN_MESSAGE);
		}
		else
			JOptionPane.showMessageDialog(
				scrollPane,
				"Checking the latest release failed!",
				"Check Release Failed",
				JOptionPane.ERROR_MESSAGE);
	}
}
