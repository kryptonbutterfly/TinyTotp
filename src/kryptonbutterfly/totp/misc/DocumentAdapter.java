package kryptonbutterfly.totp.misc;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class DocumentAdapter implements DocumentListener
{
	@Override
	public void changedUpdate(DocumentEvent e)
	{}
	
	@Override
	public void insertUpdate(DocumentEvent e)
	{}
	
	@Override
	public void removeUpdate(DocumentEvent e)
	{}
}
