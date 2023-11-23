package d.t.totp.prefs;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.google.gson.annotations.Expose;

import de.tinycodecrank.monads.opt.Opt;

public final class TotpConfig
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
	public ArrayList<TotpEntry> entries = new ArrayList<>();
	
	@Expose
	public ArrayList<TotpCategory> categories = new ArrayList<TotpCategory>();
	
	public Opt<TotpCategory> getCategoryByName(String name)
	{
		return Opt.convert(
			categories.stream()
				.filter(c -> c.name.equals(name))
				.findAny());
	}
}