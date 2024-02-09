package kryptonbutterfly.totp;

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

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.os.BaseDirectory;
import kryptonbutterfly.os.Platforms;
import kryptonbutterfly.reflectionUtils.Accessor;
import kryptonbutterfly.totp.misc.Assets;
import kryptonbutterfly.totp.misc.HtmlColors;
import kryptonbutterfly.totp.prefs.TotpConfig;
import kryptonbutterfly.totp.prefs.TotpImageCache;
import kryptonbutterfly.totp.prefs.TotpWindowStates;
import kryptonbutterfly.totp.ui.main.MainGui;
import kryptonbutterfly.totp.ui.passwd.PasswdGui;
import kryptonbutterfly.util.swing.ObservableGui;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import kryptonbutterfly.util.swing.state.PersistableValue;
import lombok.SneakyThrows;

public class TinyTotp implements TotpConstants
{
	private static Gson GSON = createGson();
	
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
		EventQueue.invokeLater(() -> new PasswdGui(gce -> {
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
		writeToFileAsJson(windowStates, WINDOW_STATES);
		writeToFileAsJson(imageCache, CACHE);
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
		return new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(Color.class, new ColorAdapter())
			.setExclusionStrategies(new ExclusionStrategy()
			{
				@Override
				public boolean shouldSkipField(FieldAttributes f)
				{
					return f.getAnnotation(PersistableValue.class) == null && f.getAnnotation(Expose.class) == null;
				}
				
				@Override
				public boolean shouldSkipClass(Class<?> clazz)
				{
					return false;
				}
			})
			.create();
	}
	
	private static final void backgroundInit()
	{
		Arrays.stream(HtmlColors.values()).parallel().forEach(value -> value.getIcon());
	}
}