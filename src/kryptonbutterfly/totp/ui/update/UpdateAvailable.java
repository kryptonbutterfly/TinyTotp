package kryptonbutterfly.totp.ui.update;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Window;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import kryptonbutterfly.checkRelease.data.ReleaseInfo;
import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.util.swing.ApplyAbortPanel;
import kryptonbutterfly.util.swing.ObservableDialog;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;

@SuppressWarnings("serial")
public class UpdateAvailable extends ObservableDialog<BL, Void, Void>
{
	private final JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
	
	final String url;
	
	public UpdateAvailable(
		Window owner,
		ModalityType modality,
		ReleaseInfo releaseInfo,
		Consumer<GuiCloseEvent<Void>> closeListener)
	{
		super(owner, modality, closeListener);
		this.url = releaseInfo.html_url;
		TinyTotp.windowStates.updateAvailableWindow.setBounds(this);
		
		setTitle("New Version available");
		GridBagConstraints gbc_lblANewVersion = new GridBagConstraints();
		gbc_lblANewVersion.anchor	= GridBagConstraints.NORTHWEST;
		gbc_lblANewVersion.fill		= GridBagConstraints.HORIZONTAL;
		gbc_lblANewVersion.insets	= new Insets(0, 0, 5, 5);
		gbc_lblANewVersion.gridx	= 1;
		gbc_lblANewVersion.gridy	= 0;
		
		final var verticalBox = Box.createVerticalBox();
		getContentPane().add(verticalBox, BorderLayout.NORTH);
		
		verticalBox.add(new JLabel("A new version is available."));
		verticalBox.add(Box.createVerticalStrut(10));
		
		final var horizontalBox = Box.createHorizontalBox();
		horizontalBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		verticalBox.add(horizontalBox);
		
		horizontalBox.add(new JLabel(releaseInfo.tag_name));
		horizontalBox.add(Box.createHorizontalStrut(10));
		
		final var lblVersionTitle = new JLabel(releaseInfo.name);
		lblVersionTitle.setFont(new Font(lblVersionTitle.getFont().getFontName(), Font.BOLD, 18));
		horizontalBox.add(lblVersionTitle);
		
		final var panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		panel.add(Box.createVerticalGlue(), BorderLayout.CENTER);
		
		if (TinyTotp.currentVersion != null)
		{
			final var horizontalBox_1 = Box.createHorizontalBox();
			panel.add(horizontalBox_1, BorderLayout.SOUTH);
			
			horizontalBox_1.add(new JLabel("Current version:"));
			horizontalBox_1.add(Box.createHorizontalStrut(10));
			
			final var lblCurrentversion = new JLabel(TinyTotp.currentVersion.toGitTag());
			horizontalBox_1.add(lblCurrentversion);
		}
		
		bottomPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.NORTH);
		add(bottomPanel, BorderLayout.SOUTH);
		
		businessLogic.if_(this::init);
		
		setVisible(true);
	}
	
	private void init(BL bl)
	{
		final var applyAbortPanel = new ApplyAbortPanel("Open in Browser", bl::openInBrowser, "Not now", bl::notNow);
		bottomPanel.add(applyAbortPanel, BorderLayout.CENTER);
	}
	
	@Override
	protected BL createBusinessLogic(Void args)
	{
		return new BL(this);
	}
}