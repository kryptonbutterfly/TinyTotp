package kryptonbutterfly.totp.ui.prefs.cat;

import javax.swing.JComponent;

public interface PrefsCat
{
	public JComponent content();
	
	public void init();
	
	public void persist();
	
	public String prefsPath();
}
