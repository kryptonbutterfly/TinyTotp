package kryptonbutterfly.totp.ui.chooseicon;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import kryptonbutterfly.collections.data.Tuple;
import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.TotpConstants;
import kryptonbutterfly.util.swing.ObservableDialog;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;

@SuppressWarnings("serial")
public final class ChooseIcon extends ObservableDialog<BL, Tuple<String, BufferedImage>, Void> implements TotpConstants
{
	final JPanel iconsPanel = new JPanel(new GridLayout(0, 1, 0, 0));
	
	private final JScrollPane scrollPane = new JScrollPane(iconsPanel);
	
	private final JButton btnAbort = new JButton(BUTTON_ABORT);
	
	public ChooseIcon(
		Window owner,
		ModalityType modality,
		Consumer<GuiCloseEvent<Tuple<String, BufferedImage>>> closeListener)
	{
		super(owner, modality, closeListener);
		TinyTotp.windowStates.chooseIcon.setBounds(this);
		setTitle("Select Icon");
		
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_INCREMENT);
		add(scrollPane, BorderLayout.CENTER);
		
		final var panelAbort = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(panelAbort, BorderLayout.SOUTH);
		panelAbort.add(btnAbort);
		
		businessLogic.if_(this::init);
		setVisible(true);
	}
	
	private void init(BL bl)
	{
		bl.init();
		
		btnAbort.addActionListener(bl::abort);
		btnAbort.addKeyListener(bl.escapeListener);
	}
	
	@Override
	protected BL createBusinessLogic(Void args)
	{
		return new BL(this);
	}
}
