package kryptonbutterfly.totp.ui.misc.color;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import kryptonbutterfly.monads.sum.of2.Sum2;
import kryptonbutterfly.totp.misc.HtmlColors;
import kryptonbutterfly.totp.prefs.TotpCategory;

@SuppressWarnings("serial")
public class HtmlColorRenderer extends JLabel implements ListCellRenderer<HtmlColors>
{
	private final Sum2<HtmlColors, Color> currentColor;
	
	public HtmlColorRenderer(Color currentColor)
	{
		this.currentColor = HtmlColors.fromColor(currentColor)
			.map(Sum2::<HtmlColors, Color>left)
			.get(() -> Sum2.right(currentColor));
	}
	
	@Override
	public Component getListCellRendererComponent(
		JList<? extends HtmlColors> list,
		HtmlColors value,
		int index,
		boolean isSelected,
		boolean cellHasFocus)
	{
		if (value != null)
			return setHtmlColor(value);
		return currentColor.fold(this::setHtmlColor, color ->
		{
			final var text = HtmlColors.getClosest(color)
				.map(c -> "~ " + c.name())
				.get(() -> "Custom Color");
			this.setIcon(TotpCategory.getColoredIcon(color));
			this.setText(text);
			return this;
		});
	}
	
	private Component setHtmlColor(HtmlColors color)
	{
		this.setIcon(color.getIcon());
		this.setText(color.name());
		return this;
	}
}