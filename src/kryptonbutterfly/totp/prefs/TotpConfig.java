package kryptonbutterfly.totp.prefs;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.totp.misc.HtmlColors;

public final class TotpConfig
{
	@Expose
	public ArrayList<TotpEntry> entries = new ArrayList<>();
	
	@Expose
	public ArrayList<TotpCategory> categories = new ArrayList<TotpCategory>();
	
	@Expose
	public boolean useTimeServer = false;
	
	@Expose
	public String ntpServer = "";
	
	{
		final var other = new TotpCategory();
		other.name	= "Other";
		other.color	= HtmlColors.GoldenRod.color;
		categories.add(other);
	}
	
	public Opt<TotpCategory> getCategoryByName(String name)
	{
		return Opt.convert(
			categories.stream()
				.filter(c -> c.name.equals(name))
				.findAny());
	}
}