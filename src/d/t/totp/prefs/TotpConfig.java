package d.t.totp.prefs;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

import d.t.totp.misc.HtmlColors;
import de.tinycodecrank.monads.opt.Opt;

public final class TotpConfig
{
	@Expose
	public ArrayList<TotpEntry> entries = new ArrayList<>();
	
	@Expose
	public ArrayList<TotpCategory> categories = new ArrayList<TotpCategory>();
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