package kryptonbutterfly.totp.prefs;

import javax.swing.JFrame;

import com.google.gson.annotations.Expose;

public final class TotpWindowStates
{
	@Expose
	public GuiPrefs mainWindow = new GuiPrefs(100, 100, 400, 600, JFrame.NORMAL);
	
	@Expose
	public GuiPrefs passwdWindow = new GuiPrefs(100, 100, 300, 120, JFrame.NORMAL);
	
	@Expose
	public GuiPrefs createDialog = new GuiPrefs(100, 100, 400, 350, JFrame.NORMAL);
	
	@Expose
	public GuiPrefs categoryDialog = new GuiPrefs(100, 100, 400, 600, JFrame.NORMAL);
	
	@Expose
	public GuiPrefs qrScan = new GuiPrefs(100, 100, 640, 400, JFrame.NORMAL);
}