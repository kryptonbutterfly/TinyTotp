package kryptonbutterfly.totp.misc;

import kryptonbutterfly.totp.prefs.TotpEntry;

public record EditResult(
	boolean delete,
	TotpEntry entry)
{}