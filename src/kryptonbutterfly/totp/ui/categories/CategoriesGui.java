package kryptonbutterfly.totp.ui.categories;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.HashMap;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.TotpConstants;
import kryptonbutterfly.totp.misc.HtmlColors;
import kryptonbutterfly.util.swing.ApplyAbortPanel;
import kryptonbutterfly.util.swing.ObservableDialog;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;

@SuppressWarnings("serial")
public final class CategoriesGui extends ObservableDialog<BL, HashMap<String, String>, Void> implements TotpConstants
{
	Box contentBox = Box.createVerticalBox();
	
	JLabel		icon	= new JLabel();
	JTextField	name	= new JTextField();
	Color		color	= null;
	
	JButton btnApply;
	
	public CategoriesGui(
		Window owner,
		ModalityType modality,
		Consumer<GuiCloseEvent<HashMap<String, String>>> closeListener)
	{
		super(owner, modality, closeListener);
		TinyTotp.windowStates.categoryDialog.setBounds(this);
		setTitle("Categories");
		
		businessLogic.if_(this::init);
		
		setVisible(true);
	}
	
	@Override
	protected BL createBusinessLogic(Void args)
	{
		return new BL(this);
	}
	
	private void init(BL bl)
	{
		JScrollPane scrollPane = new JScrollPane(contentBox);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);
		
		final var bottomPanel = new JPanel(new BorderLayout());
		add(bottomPanel, BorderLayout.SOUTH);
		
		final var addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		bottomPanel.add(addPanel, BorderLayout.CENTER);
		addPanel.add(icon);
		icon.addMouseListener(bl.colorListener());
		
		addPanel.add(name);
		name.setColumns(15);
		name.addKeyListener(bl.enterName());
		name.addKeyListener(bl.escapeListener());
		
		final var btnAdd = new JButton(BUTTON_ADD);
		bottomPanel.add(btnAdd, BorderLayout.EAST);
		btnAdd.addActionListener(bl::add);
		btnAdd.setForeground(HtmlColors.LightGreen.color());
		btnAdd.addKeyListener(bl.escapeListener());
		
		final var applyAbort = new ApplyAbortPanel(BUTTON_ABORT, bl::abort, BUTTON_APPLY, bl::apply);
		bottomPanel.add(applyAbort, BorderLayout.SOUTH);
		applyAbort.btnButton1.addKeyListener(bl.escapeListener());
		btnApply = applyAbort.btnButton2;
		btnApply.addKeyListener(bl.escapeListener());
		
		bl.initAdd(this);
		
		TinyTotp.config.categories
			.forEach(
				c -> contentBox
					.add(new CategoryComponent(this, c, bl::remove, bl.escapeListener(), bl.enterTypedListener())));
	}
}