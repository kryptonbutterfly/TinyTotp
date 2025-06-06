module DesktopTOTP
{
	exports kryptonbutterfly.totp.prefs to com.google.gson;
	
	requires transitive java.desktop;
	requires kryptonbutterfly.mathUtils;
	requires kryptonbutterfly.Monads;
	requires transitive kryptonbutterfly.SwingUtils;
	requires transitive kryptonbutterfly.checkRelease;
	requires kryptonbutterfly.System;
	requires kryptonbutterfly.ReflectionUtils;
	requires kryptonbutterfly.Cache;
	requires kryptonbutterfly.bounded;
	requires kryptonbutterfly.linear_algebra;
	requires org.apache.commons.codec;
	requires java.base;
	requires com.google.gson;
	requires lombok;
	requires transitive webcam.capture;
	requires com.google.zxing;
	requires org.apache.commons.net;
}