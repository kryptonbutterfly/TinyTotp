package d.t.totp.ui.main;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.function.UnaryOperator;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import d.t.totp.TotpConstants;
import d.t.totp.TinyTotp;
import d.t.totp.misc.Assets;
import d.t.totp.misc.ImageUtils;
import d.t.totp.misc.TotpGenerator;
import d.t.totp.prefs.TotpEntry;
import d.t.totp.ui.add.manual.AddKey;
import de.tinycodecrank.math.linalgebra.vector.Vec2i;
import de.tinycodecrank.util.swing.events.GuiCloseEvent;

@SuppressWarnings("serial")
public final class TotpComponent extends JPanel implements TotpConstants
{
	private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	private static final Dimension	min	= new Dimension(250, COMPONENT_MIN_HEIGHT);
	private static final Dimension	max	= new Dimension(Integer.MAX_VALUE, COMPONENT_MAX_HEIGHT);
	
	private TotpEntry entry;
	
	private boolean	dirty		= true;
	private long	nextUpdate	= Long.MIN_VALUE;
	
	private final RemoveListener removeListener;
	
	private final JPanel	categoryPanel	= new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
	private final JButton	buttonEdit		= new JButton(Assets.getEditByBackground(getBackground()));
	private final JButton	buttonRemove	= new JButton(Assets.getDeleteByBackground(getBackground()));
	private final JLabel	iconLabel		= new JLabel();
	private final JButton	totp			= new JButton();
	private final JLabel	userName		= new JLabel();
	private final JLabel	issuerName		= new JLabel();
	
	public TotpComponent(TotpEntry entry, RemoveListener removeListener, char[] password)
	{
		this.entry			= entry;
		this.removeListener	= removeListener;
		
		setLayout(new BorderLayout(5, 5));
		
		categoryPanel.add(Box.createHorizontalStrut(CATEGORY_MARKER_WIDTH));
		add(categoryPanel, BorderLayout.WEST);
		
		final var editPanel = Box.createVerticalBox();
		add(editPanel, BorderLayout.EAST);
		editPanel.add(buttonEdit);
		buttonEdit.addActionListener(edit(password));
		editPanel.add(buttonRemove);
		buttonRemove.addActionListener(this::remove);
		
		final var contentPanel = new JPanel(new BorderLayout(5, 5));
		add(contentPanel, BorderLayout.CENTER);
		contentPanel.add(iconLabel, BorderLayout.WEST);
		
		final var descriptionPanel = Box.createVerticalBox();
		contentPanel.add(descriptionPanel, BorderLayout.CENTER);
		descriptionPanel.add(totp);
		totp.setFont(totp.getFont().deriveFont(28F));
		totp.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		totp.addActionListener(this::copyTotp);
		
		descriptionPanel.add(userName);
		descriptionPanel.add(issuerName);
		
		add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);
		
		setMinimumSize(min);
		setMaximumSize(max);
		
		init(entry, password);
	}
	
	public void updateCategory(HashMap<String, String> renameMap)
	{
		final var newName = renameMap.getOrDefault(entry.category, entry.category);
		TinyTotp.config.getCategoryByName(newName).if_(cat ->
		{
			entry.category = cat.name;
			categoryPanel.setToolTipText(cat.name);
			categoryPanel.setBackground(cat.color);
		}).else_(() -> entry.category = null);
	}
	
	/**
	 * @param currentTime
	 *            The current time in ms
	 * @param password
	 *            The password used to encrypt the Totp secret keys.
	 * @return true if the ui content has been updated, otherwise false.
	 */
	public boolean update(final long currentTime, char[] password)
	{
		final long currentTimeFrame = calcTimeFrame(currentTime);
		if (nextUpdate < currentTime)
			dirty = true;
		
		if (!dirty)
			return false;
		
		nextUpdate = millisFromTimeFrame(currentTimeFrame + 1);
		totp.setText(TotpGenerator.generateTotp(entry, password, currentTimeFrame));
		dirty = false;
		return true;
	}
	
	private long calcTimeFrame(long currentTime)
	{
		return currentTime / (1000 * entry.totpValidForSeconds);
	}
	
	private long millisFromTimeFrame(long timeFrame)
	{
		return timeFrame * 1000 * entry.totpValidForSeconds;
	}
	
	public TotpEntry entry()
	{
		return entry;
	}
	
	private void setIcon(String iconName)
	{
		this.entry.icon = iconName;
		final var icon = TinyTotp.imageCache.getImage(iconName)
			.map(this.ensureScale(ICON_WIDTH))
			.map(ImageIcon::new)
			.get(() -> Assets.MISSING_ICON);
		iconLabel.setIcon(icon);
	}
	
	private ActionListener edit(char[] password)
	{
		return ae -> EventQueue.invokeLater(() ->
		{
			final var owner = SwingUtilities.getWindowAncestor(this);
			new AddKey(
				owner,
				ModalityType.APPLICATION_MODAL,
				r -> editResult(r, password),
				password,
				"Edit TOTP Entry",
				entry);
		});
	}
	
	private void remove(ActionEvent ae)
	{
		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
			this,
			"Are you sure you want to delete the entry?",
			"Delete Entry",
			JOptionPane.YES_NO_OPTION))
		{
			removeListener.remove(this);
		}
	}
	
	private void editResult(GuiCloseEvent<TotpEntry> result, char[] password)
	{
		result.getReturnValue().if_(e -> init(e, password));
	}
	
	private void init(TotpEntry entry, char[] password)
	{
		this.dirty		= true;
		this.nextUpdate	= Long.MIN_VALUE;
		
		this.entry = entry;
		this.userName.setText(entry.accountName);
		this.issuerName.setText(entry.issuerName);
		setIcon(entry.icon);
		TinyTotp.config.getCategoryByName(entry.category)
			.if_(c ->
			{
				categoryPanel.setBackground(c.color);
				categoryPanel.setToolTipText(c.name);
			});
		update(System.currentTimeMillis(), password);
	}
	
	private UnaryOperator<BufferedImage> ensureScale(Vec2i maxSize)
	{
		return rawImage -> ImageUtils.scaleDownToMax(rawImage, maxSize);
		
		// final int maxHeight = maxWidth;
		// return rawImage ->
		// {
		// if (maxWidth >= rawImage.getWidth() && maxHeight >= rawImage.getHeight())
		// return rawImage;
		// float imageAspect = rawImage.getWidth() / rawImage.getHeight();
		//
		// final float factor;
		// if (imageAspect > 1)
		// factor = ((float) maxWidth / rawImage.getWidth());
		// else
		// factor = ((float) maxHeight / rawImage.getHeight());
		//
		// final var transform = new AffineTransform();
		// transform.scale(factor, factor);
		// return new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC)
		// .filter(rawImage, null);
		// };
	}
	
	private void copyTotp(ActionEvent ae)
	{
		final String passwd = totp.getText();
		if (passwd == null || passwd.isBlank())
			return;
		final var selection = new StringSelection(passwd);
		clipboard.setContents(selection, selection);
	}
	
	@FunctionalInterface
	public static interface RemoveListener
	{
		void remove(TotpComponent element);
	}
}