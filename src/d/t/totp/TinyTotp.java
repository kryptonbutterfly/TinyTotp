package d.t.totp;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import d.t.totp.misc.Assets;
import d.t.totp.misc.HtmlColors;
import d.t.totp.prefs.TotpConfig;
import d.t.totp.prefs.TotpImageCache;
import d.t.totp.prefs.TotpWindowStates;
import d.t.totp.ui.main.MainGui;
import d.t.totp.ui.passwd.PasswdGui;
import de.tinycodecrank.monads.opt.Opt;
import de.tinycodecrank.os.BaseDirectory;
import de.tinycodecrank.os.Platforms;
import de.tinycodecrank.reflectionUtils.Accessor;
import de.tinycodecrank.util.swing.ObservableGui;
import de.tinycodecrank.util.swing.events.GuiCloseEvent;
import lombok.SneakyThrows;

public class TinyTotp implements TotpConstants
{
	private static Gson GSON = createGson();
	
	// private static final File CONFIG_FOLDER = new File(
	// Platforms.getAppDataFile(),
	// ".config/tinycodecrank/tinyTotp");
	private static final BaseDirectory BASE_DIR = Platforms.baseDir(ORG_NAME, PROGRAM_NAME);
	
	private static final File	CONFIG			= new File(BASE_DIR.dataHome(), "config.json");
	private static final File	CACHE			= new File(BASE_DIR.cacheHome(), IMG_CACHE_MAPPINGS);
	private static final File	WINDOW_STATES	= new File(BASE_DIR.stateHome(), "windowStates.json");
	
	public static final TotpConfig			config			= loadData();
	public static final TotpWindowStates	windowStates	= loadWindowStates();
	public static final TotpImageCache		imageCache		= loadImageCache();
	
	public static void main(String[] args)
	{
		backgroundInit();
		imageCache.loadFromCache();
		setLookAndFeel();
		ObservableGui.setDefaultAppImage(Opt.of(Assets.ICON));
		EventQueue.invokeLater(() -> new PasswdGui(gce ->
		{
			gce.getReturnValue()
				.if_(passwd -> EventQueue.invokeLater(() -> new MainGui(TinyTotp::terminateListener, passwd)))
				.else_(() -> terminateListener(null));
		}, windowStates.passwdWindow));
	}
	
	@SneakyThrows
	private static TotpConfig loadData()
	{
		if (!CONFIG.exists())
			return new TotpConfig();
		return GSON.fromJson(Files.readString(CONFIG.toPath()), TotpConfig.class);
	}
	
	@SneakyThrows
	private static TotpWindowStates loadWindowStates()
	{
		if (!WINDOW_STATES.exists())
			return new TotpWindowStates();
		return GSON.fromJson(Files.readString(WINDOW_STATES.toPath()), TotpWindowStates.class);
	}
	
	@SneakyThrows
	private static TotpImageCache loadImageCache()
	{
		if (!CACHE.exists())
			return new TotpImageCache(CACHE);
		final var cache = GSON.fromJson(Files.readString(CACHE.toPath()), TotpImageCache.class);
		cache.file(CACHE);
		return cache;
	}
	
	private static void terminateListener(GuiCloseEvent<Void> gce)
	{
		writeToFileAsJson(config, CONFIG);
		writeToFileAsJson(imageCache, imageCache.file());
	}
	
	@SneakyThrows
	private static void writeToFileAsJson(Object data, File file)
	{
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		
		Files.writeString(
			file.toPath(),
			GSON.toJson(data),
			StandardOpenOption.CREATE,
			StandardOpenOption.WRITE,
			StandardOpenOption.TRUNCATE_EXISTING);
	}
	
	private static void setLookAndFeel()
	{
		try
		{
			final Toolkit toolkit = Toolkit.getDefaultToolkit();
			new Accessor<>(toolkit, toolkit.getClass().getDeclaredField("awtAppClassName"))
				.applyObj(Field::set, PROGRAM_DOCK_NAME);
		}
		catch (
			InaccessibleObjectException
			| IllegalArgumentException
			| IllegalAccessException
			| NoSuchFieldException
			| SecurityException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (
			ClassNotFoundException
			| InstantiationException
			| IllegalAccessException
			| UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
	}
	
	private static Gson createGson()
	{
		final var builder = new GsonBuilder();
		builder.setPrettyPrinting();
		builder.excludeFieldsWithoutExposeAnnotation();
		builder.registerTypeAdapter(Color.class, new ColorAdapter());
		
		return builder.create();
	}
	
	private static final void backgroundInit()
	{
		Arrays.stream(HtmlColors.values()).parallel().forEach(value -> value.getIcon());
	}
}