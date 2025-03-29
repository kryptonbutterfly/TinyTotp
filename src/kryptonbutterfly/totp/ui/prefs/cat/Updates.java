package kryptonbutterfly.totp.ui.prefs.cat;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Objects;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import kryptonbutterfly.checkRelease.Cadence;
import kryptonbutterfly.checkRelease.SemVer;
import kryptonbutterfly.functions.void_.Consumer_;
import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.TotpConstants;
import kryptonbutterfly.totp.misc.Assets;
import kryptonbutterfly.totp.ui.update.UpdateAvailable;

public class Updates implements PrefsCat
{
	private final JScrollPane scrollPane;
	
	private final JLabel lblAppVerison = new JLabel(Assets.ICON_48);
	
	private final JComboBox<Cadence>	comboCadence					= new JComboBox<>(Cadence.values());
	private final JCheckBox				chckbxShowUpdateNotification	= new JCheckBox("Update notifications");
	
	private final JLabel lblCheckForUpdates;
	
	public Updates()
	{
		final var panel = new JPanel(new BorderLayout(5, 5));
		this.scrollPane = new JScrollPane(panel);
		
		final var verticalBox = Box.createVerticalBox();
		panel.add(verticalBox, BorderLayout.NORTH);
		
		final var panel_3 = new JPanel();
		verticalBox.add(panel_3);
		
		panel_3.add(lblAppVerison);
		
		final var panel_1 = new JPanel(new GridLayout(0, 2, 0, 0));
		verticalBox.add(panel_1);
		
		chckbxShowUpdateNotification.setToolTipText("Show update notification on application startup.");
		panel_1.add(chckbxShowUpdateNotification);
		
		chckbxShowUpdateNotification.addActionListener(this::toggleShowUpdateNotification);
		chckbxShowUpdateNotification.setHorizontalAlignment(SwingConstants.LEFT);
		
		panel_1.add(Box.createGlue());
		lblCheckForUpdates = new JLabel("Check for updates:");
		panel_1.add(lblCheckForUpdates);
		panel_1.add(comboCadence);
		
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
		chckbxShowUpdateNotification.setSelected(TinyTotp.releaseState.showUpdateNotification);
		
		setTitleAndVersion();
	}
	
	@Override
	public void persist()
	{
		TinyTotp.releaseState.cadence					= (Cadence) comboCadence.getSelectedItem();
		TinyTotp.releaseState.showUpdateNotification	= chckbxShowUpdateNotification.isSelected();
	}
	
	@Override
	public String prefsPath()
	{
		return "Updates";
	}
	
	private void setTitleAndVersion()
	{
		final var version = Objects.toString(TinyTotp.currentVersion, "?.?.?");
		lblAppVerison.setText("%s %s".formatted(TotpConstants.PROGRAM_NAME, version));
	}
	
	private void toggleShowUpdateNotification(ActionEvent ae)
	{
		lblCheckForUpdates.setEnabled(chckbxShowUpdateNotification.isSelected());
		comboCadence.setEnabled(chckbxShowUpdateNotification.isSelected());
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
