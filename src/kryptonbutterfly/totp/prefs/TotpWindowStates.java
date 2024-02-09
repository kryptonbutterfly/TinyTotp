package kryptonbutterfly.totp.prefs;

import com.google.gson.annotations.Expose;

import kryptonbutterfly.util.swing.state.WindowState;

public final class TotpWindowStates
{
	@Expose
	public WindowState mainWindow = new WindowState(100, 100, 400, 600);
	
	@Expose
	public WindowState passwdWindow = new WindowState(100, 100, 300, 120);
	
	@Expose
	public WindowState createDialog = new WindowState(100, 100, 400, 350);
	
	@Expose
	public WindowState categoryDialog = new WindowState(100, 100, 400, 600);
	
	@Expose
	public WindowState qrScan = new WindowState(100, 100, 640, 400);
	
	@Expose
	public WindowState qrExport = new WindowState(100, 100, 230, 280);
}