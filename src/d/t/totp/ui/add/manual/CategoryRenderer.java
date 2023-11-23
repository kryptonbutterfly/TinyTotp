package d.t.totp.ui.add.manual;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import d.t.totp.prefs.TotpCategory;

@SuppressWarnings("serial")
public class CategoryRenderer extends JLabel implements ListCellRenderer<TotpCategory>
{
	@Override
	public Component getListCellRendererComponent(
		JList<? extends TotpCategory> list,
		TotpCategory value,
		int index,
		boolean isSelected,
		boolean cellHasFocus)
	{
		if (value == null)
		{
			setText("");
			return this;
		}
		setText(value.name);
		setIcon(value.getIcon());
		return this;
	}
}