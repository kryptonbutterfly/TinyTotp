package kryptonbutterfly.totp.ui.update;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.util.swing.Logic;

final class BL extends Logic<UpdateAvailable, Void>
{
	BL(UpdateAvailable gui)
	{
		super(gui);
	}
	
	void openInBrowser(ActionEvent _ae)
	{
		gui.if_(gui -> {
			try
			{
				if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
					Desktop.getDesktop().browse(new URI(gui.url));
			}
			catch (IOException | URISyntaxException e)
			{
				e.printStackTrace();
			}
			gui.dispose();
		});
	}
	
	void notNow(ActionEvent _ae)
	{
		gui.if_(UpdateAvailable::dispose);
	}
	
	@Override
	protected void disposeAction()
	{
		gui.if_(TinyTotp.windowStates.updateAvailableWindow::persistBounds);
	}
}
