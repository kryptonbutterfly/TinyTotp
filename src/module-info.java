module DesktopTOTP
{
	exports d.t.totp.prefs to com.google.gson;
	
	requires transitive java.desktop;
	requires de.tinycodecrank.mathUtils;
	requires de.tinycodecrank.Monads;
	requires com.google.gson;
	requires lombok;
	requires de.tinycodecrank.SwingUtils;
	requires org.apache.commons.codec;
	requires de.tinycodecrank.System;
	requires java.base;
	requires de.tinycodecrank.ReflectionUtils;
	requires de.tinycodecrank.Cache;
	requires de.tinycodecrank.bounded;
	requires linear_algebra;
}