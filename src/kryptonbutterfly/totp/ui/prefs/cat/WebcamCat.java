package kryptonbutterfly.totp.ui.prefs.cat;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.github.sarxos.webcam.Webcam;

import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.misc.Margin;
import kryptonbutterfly.totp.misc.Utils;
import kryptonbutterfly.totp.ui.misc.AutoScalingImage;
import kryptonbutterfly.totp.ui.prefs.PreferencesGui;

public final class WebcamCat implements PrefsCat
{
	private final JScrollPane		scrollPane;
	private final JComboBox<Webcam>	webcams;
	private final AutoScalingImage	cameraDisplay	= new AutoScalingImage(new Margin(5));
	private final JLabel			resolutionLabel	= new JLabel();
	
	private final PreferencesGui gui;
	
	private boolean keepRunning = true;
	
	public WebcamCat(PreferencesGui gui, KeyListener escapeKeyListener)
	{
		this.gui = gui;
		final var panel = new JPanel(new BorderLayout(5, 5));
		this.scrollPane = new JScrollPane(panel);
		
		TinyTotp.webcamLock.await();
		
		webcams = new JComboBox<Webcam>(TinyTotp.supportedCams);
		webcams.addItemListener(this::selectionListener);
		panel.add(webcams, BorderLayout.NORTH);
		
		cameraDisplay.setHorizontalAlignment(SwingConstants.CENTER);
		cameraDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(cameraDisplay, BorderLayout.CENTER);
		
		resolutionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(resolutionLabel, BorderLayout.SOUTH);
		
		webcams.addKeyListener(escapeKeyListener);
	}
	
	@Override
	public JComponent content()
	{
		return this.scrollPane;
	}
	
	@Override
	public void init()
	{
		if (TinyTotp.webcamCache.preferredWebcam != null)
		{
			final var cam = Webcam.getWebcamByName(TinyTotp.webcamCache.preferredWebcam);
			if (cam != null)
				webcams.setSelectedItem(cam);
		}
		
		gui.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				super.windowClosed(e);
				keepRunning = false;
			}
		});
		
		scan();
	}
	
	private void selectionListener(ItemEvent ie)
	{
		if (ie.getStateChange() != ItemEvent.DESELECTED)
			return;
		final var webcam = (Webcam) ie.getItem();
		if (webcam == null)
			return;
		if (webcam.isOpen())
			webcam.close();
	}
	
	private void scan()
	{
		final var webcam = (Webcam) webcams.getSelectedItem();
		if (webcam != null)
		{
			if (!webcam.isOpen())
			{
				final var dims = TinyTotp.webcamCache.getMaxResolution(webcam.getName());
				webcam.setCustomViewSizes(dims.getSize());
				webcam.setViewSize(dims.getSize());
				resolutionLabel.setText(dims.toString());
				
				webcam.open();
			}
			
			final var image = webcam.getImage();
			cameraDisplay.setIcon(Utils.mirror(image));
			cameraDisplay.repaint();
		}
		if (keepRunning)
			EventQueue.invokeLater(this::scan);
		else if (webcam.isOpen())
			webcam.close();
	}
	
	@Override
	public void persist()
	{
		final Webcam cam = (Webcam) webcams.getSelectedItem();
		TinyTotp.webcamCache.preferredWebcam = cam == null ? null : cam.getName();
	}
	
	@Override
	public String prefsPath()
	{
		return "Webcam";
	}
}
