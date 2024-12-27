package kryptonbutterfly.totp.ui.qrimport;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.github.sarxos.webcam.Webcam;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.misc.ImageLuminanceSource;
import kryptonbutterfly.totp.misc.Utils;
import kryptonbutterfly.totp.ui.misc.KeyTypedAdapter;
import kryptonbutterfly.util.swing.Logic;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import kryptonbutterfly.util.swing.events.GuiCloseEvent.Result;

final class BL extends Logic<QrGui, Void>
{
	private static final Map<DecodeHintType, ?> HINTS = Map.of(DecodeHintType.TRY_HARDER, true);
	
	private boolean			keepScanning	= true;
	private final Webcam	webcam			= Webcam.getDefault();
	
	final KeyListener	escapeScanListener	= new KeyTypedAdapter(c -> abortScan(null), KeyEvent.VK_ESCAPE);
	final KeyListener	escapeMenuListener	= new KeyTypedAdapter(c -> abort(null), KeyEvent.VK_ESCAPE);
	
	BL(QrGui gui)
	{
		super(gui);
	}
	
	void abort(ActionEvent ae)
	{
		gui.if_(QrGui::dispose);
	}
	
	void abortScan(ActionEvent ae)
	{
		gui.if_(gui -> {
			gui.cardLayout.show(gui.cardPanel, QrGui.MENU_CARD);
			keepScanning = false;
			gui.btnScanCamera.requestFocus();
		});
	}
	
	void scanCamera(ActionEvent ae)
	{
		gui.if_(gui -> {
			keepScanning = true;
			gui.cardLayout.show(gui.cardPanel, QrGui.SCAN_CARD);
			gui.btnAbortScan.requestFocus();
			EventQueue.invokeLater(() -> scan(gui));
		});
	}
	
	void scanFile(ActionEvent ae)
	{
		gui.if_(gui -> {
			final var chooser = new JFileChooser();
			chooser.setFileFilter(new FileNameExtensionFilter("Image", ImageIO.getReaderFileSuffixes()));
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setFileHidingEnabled(false);
			chooser.setMultiSelectionEnabled(false);
			switch (chooser.showOpenDialog(gui))
			{
				case JFileChooser.APPROVE_OPTION -> {
					try
					{
						final var image = ImageIO.read(chooser.getSelectedFile());
						try
						{
							gui.dispose(new GuiCloseEvent<>(Result.SUCCESS, Opt.empty(), decode(image)));
						}
						catch (NotFoundException e)
						{
							JOptionPane.showMessageDialog(
								gui,
								"Could not detect a qr code in the selected image.",
								"No qr code found",
								JOptionPane.INFORMATION_MESSAGE);
						}
						catch (ChecksumException e)
						{
							JOptionPane.showMessageDialog(gui, e, "Invalid qr code", JOptionPane.ERROR_MESSAGE);
						}
						catch (FormatException e)
						{
							JOptionPane.showMessageDialog(gui, e, "Invalid qr code format", JOptionPane.ERROR_MESSAGE);
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				default -> {}
			}
		});
	}
	
	private void scan(QrGui gui)
	{
		try
		{
			if (!webcam.isOpen())
				webcam.open();
			
			final var image = webcam.getImage();
			gui.cameraDisplay.setIcon(new ImageIcon(Utils.mirror(image)));
			final var result = decode(image);
			
			keepScanning = false;
			webcam.close();
			
			gui.dispose(new GuiCloseEvent<>(Result.SUCCESS, Opt.empty(), result));
		}
		catch (NotFoundException | ChecksumException | FormatException e)
		{
			if (keepScanning)
				EventQueue.invokeLater(() -> scan(gui));
			else
				webcam.close();
		}
	}
	
	private String decode(BufferedImage image)
		throws NotFoundException,
		ChecksumException,
		FormatException
	{
		final var	bitmap	= new BinaryBitmap(new HybridBinarizer(new ImageLuminanceSource(image)));
		final var	res		= new QRCodeReader().decode(bitmap, HINTS);
		return res.getText();
	}
	
	@Override
	protected void disposeAction()
	{
		gui.if_(TinyTotp.windowStates.qrScan::persistBounds);
	}
}