package kryptonbutterfly.totp.prefs;

import java.util.ArrayList;
import java.util.HashMap;

import com.github.sarxos.webcam.WebcamResolution;
import com.google.gson.annotations.Expose;

import kryptonbutterfly.totp.misc.Utils;

public class WebcamSettings
{
	@Expose
	public HashMap<String, ArrayList<WebcamResolution>> supportedResolutions = new HashMap<>();
	
	@Expose
	public String preferredWebcam = null;
	
	public WebcamResolution getMaxResolution(String camName)
	{
		final var res = supportedResolutions.get(camName);
		if (res == null)
			return null;
		res.sort(Utils::webcamResolutionComparator);
		return res.get(res.size() - 1);
	}
}
