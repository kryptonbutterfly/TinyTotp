package kryptonbutterfly.totp.ui.misc.color;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Window;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JComboBox;

import kryptonbutterfly.totp.misc.Assets;
import kryptonbutterfly.totp.misc.HtmlColors;
import kryptonbutterfly.util.swing.ObservableDialog;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;

@SuppressWarnings("serial")
public final class ColorChooserMenu extends ObservableDialog<BL, Color, Color>
{
	private static final int	WIDTH	= 320;
	private static final int	HEIGHT	= 60;
	
	private static final int	OFFSET_X	= 20;
	private static final int	OFFSET_Y	= 0;
	
	private final JButton		btnPickColor	= new JButton(Assets.COLOR_PICKER);
	final JComboBox<HtmlColors>	htmlColor		= new JComboBox<>(HtmlColors.values());
	
	public ColorChooserMenu(
		Window owner,
		ModalityType modality,
		Consumer<GuiCloseEvent<Color>> closeListener,
		Color currentColor,
		Point position)
	{
		super(owner, modality, closeListener, currentColor);
		setResizable(false);
		setBounds(position.x + OFFSET_X, position.y + OFFSET_Y - HEIGHT, WIDTH, HEIGHT);
		setUndecorated(true);
		setLayout(new FlowLayout());
		add(btnPickColor);
		add(htmlColor);
		htmlColor.setRenderer(new HtmlColorRenderer(currentColor));
		htmlColor.setSelectedItem(HtmlColors.fromColor(currentColor).get(() -> null));
		
		businessLogic.if_(this::init);
		setVisible(true);
	}
	
	@Override
	protected BL createBusinessLogic(Color currentColor)
	{
		return new BL(this, currentColor);
	}
	
	private void init(BL bl)
	{
		addWindowFocusListener(bl.focusListener());
		btnPickColor.addActionListener(bl::pickColor);
		btnPickColor.addKeyListener(bl.escapeKeyListener());
		htmlColor.addItemListener(bl::selectColor);
		htmlColor.addKeyListener(bl.escapeKeyListener());
	}
}