package kryptonbutterfly.totp.ui.chooseicon;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import kryptonbutterfly.collections.data.Tuple;
import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.TotpConstants;
import kryptonbutterfly.totp.misc.Utils;
import kryptonbutterfly.totp.ui.misc.KeyTypedAdapter;
import kryptonbutterfly.util.swing.Logic;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import kryptonbutterfly.util.swing.events.GuiCloseEvent.Result;

final class BL extends Logic<ChooseIcon, Void>
{
	final KeyListener escapeListener = new KeyTypedAdapter(
		c -> gui.if_(ChooseIcon::dispose),
		KeyEvent.VK_ESCAPE);
	
	BL(ChooseIcon gui)
	{
		super(gui);
	}
	
	void init()
	{
		gui.if_(gui -> {
			TinyTotp.imageCache.imageMappings.forEach((name, hash) -> {
				TinyTotp.imageCache.getImage(name).if_(img -> {
					final var	icon	= Utils.scaleDownToMax(img, TotpConstants.ICON_SIZE);
					final var	btn		= new JButton(name, new ImageIcon(icon));
					btn.setHorizontalAlignment(SwingConstants.LEFT);
					btn.addActionListener(select(name, img));
					btn.addKeyListener(escapeListener);
					gui.iconsPanel.add(btn);
				});
			});
		});
	}
	
	private ActionListener select(String name, BufferedImage img)
	{
		return _ae -> gui
			.if_(gui -> gui.dispose(new GuiCloseEvent<>(Result.SUCCESS, Opt.empty(), new Tuple<>(name, img))));
	}
	
	void abort(ActionEvent _ae)
	{
		gui.if_(ChooseIcon::dispose);
	}
	
	@Override
	protected void disposeAction()
	{
		gui.if_(TinyTotp.windowStates.chooseIcon::persistBounds);
	}
}
