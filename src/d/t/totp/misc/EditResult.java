package d.t.totp.misc;

import d.t.totp.prefs.TotpEntry;

public record EditResult(
	boolean delete,
	TotpEntry entry)
{}