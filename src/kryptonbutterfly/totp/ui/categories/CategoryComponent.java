package kryptonbutterfly.totp.ui.categories;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import kryptonbutterfly.totp.TotpConstants;
import kryptonbutterfly.totp.misc.Assets;
import kryptonbutterfly.totp.prefs.TotpCategory;
import kryptonbutterfly.totp.ui.misc.color.ColorChooserMenu;

@SuppressWarnings("serial")
public final class CategoryComponent extends JPanel implements TotpConstants
{
	private static final Dimension max = new Dimension(Integer.MAX_VALUE, CATEGORY_MAX_HEIGHT);
	
	private final TotpCategory category;
	
	final JLabel					lblIcon		= new JLabel();
	final JTextField				txtCategory	= new JTextField();
	private final JButton			btnDelete	= new JButton(Assets.getDeleteByBackground(getBackground()));
	private final RemoveListener	removeListener;
	
	CategoryComponent(
		Window owner,
		TotpCategory category,
		RemoveListener removeListener,
		KeyListener escapeListener,
		KeyListener enterTypedListener)
	{
		this.category		= category.clone();
		this.removeListener	= removeListener;
		
		setLayout(new BorderLayout(0, 0));
		
		final var panel0 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		add(panel0, BorderLayout.CENTER);
		panel0.add(lblIcon);
		lblIcon.setIcon(category.getIcon());
		lblIcon.addMouseListener(this.colorListener(owner));
		
		panel0.add(txtCategory);
		txtCategory.setColumns(15);
		txtCategory.setText(category.name);
		txtCategory.addKeyListener(escapeListener);
		txtCategory.addKeyListener(enterTypedListener);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.EAST);
		
		panel.add(btnDelete);
		btnDelete.addActionListener(this::delete);
		btnDelete.addKeyListener(escapeListener);
		
		setMaximumSize(max);
	}
	
	public TotpCategory category()
	{
		return category;
	}
	
	private MouseListener colorListener(Window owner)
	{
		return new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				final var mousePos = e.getLocationOnScreen();
				EventQueue
					.invokeLater(
						() -> new ColorChooserMenu(
							owner,
							ModalityType.MODELESS,
							res -> res.getReturnValue().if_(color ->
							{
								category.color = color;
								lblIcon.setIcon(category.getIcon());
							}),
							category.color,
							mousePos));
			}
		};
	}
	
	private void delete(ActionEvent ae)
	{
		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
			this,
			"Are you sure you want to delete this category?",
			"Delete Category",
			JOptionPane.YES_NO_OPTION))
		{
			removeListener.remove(this);
		}
	}
	
	@FunctionalInterface
	public static interface RemoveListener
	{
		void remove(CategoryComponent element);
	}
}