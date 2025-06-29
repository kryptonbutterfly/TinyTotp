package kryptonbutterfly.totp;

import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.github.sarxos.webcam.Webcam;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import kryptonbutterfly.checkRelease.Checker;
import kryptonbutterfly.checkRelease.SemVer;
import kryptonbutterfly.checkRelease.data.ReleaseInfo;
import kryptonbutterfly.monads.opt.Opt;
import kryptonbutterfly.os.BaseDirectory;
import kryptonbutterfly.os.Platforms;
import kryptonbutterfly.reflectionUtils.Accessor;
import kryptonbutterfly.totp.misc.Assets;
import kryptonbutterfly.totp.misc.CheckReleaseState;
import kryptonbutterfly.totp.misc.HtmlColors;
import kryptonbutterfly.totp.misc.Lock;
import kryptonbutterfly.totp.misc.Utils;
import kryptonbutterfly.totp.prefs.ReleaseState;
import kryptonbutterfly.totp.prefs.TotpConfig;
import kryptonbutterfly.totp.prefs.TotpImageCache;
import kryptonbutterfly.totp.prefs.TotpWindowStates;
import kryptonbutterfly.totp.prefs.WebcamSettings;
import kryptonbutterfly.totp.ui.main.MainGui;
import kryptonbutterfly.totp.ui.passwd.PasswdGui;
import kryptonbutterfly.totp.ui.update.UpdateAvailable;
import kryptonbutterfly.util.swing.ObservableGui;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;
import kryptonbutterfly.util.swing.state.PersistableValue;
import lombok.SneakyThrows;

public class TinyTotp implements TotpConstants
{
	private static Gson GSON = createGson();
	
	private static final BaseDirectory BASE_DIR = Platforms.baseDir(ORG_NAME, PROGRAM_NAME);
	
	private static final File	CONFIG			= new File(BASE_DIR.dataHome(), "config.json");
	private static final File	CACHE			= new File(BASE_DIR.cacheHome(), "mappings.json");
	private static final File	WEBCAM_CACHE	= new File(BASE_DIR.cacheHome(), "webcam.json");
	private static final File	WINDOW_STATES	= new File(BASE_DIR.stateHome(), "windowStates.json");
	private static final File	VERSION_STATE	= new File(BASE_DIR.stateHome(), "versionState.json");
	
	public static final TotpConfig			config			= loadData();
	public static final TotpWindowStates	windowStates	= loadWindowStates();
	public static final TotpImageCache		imageCache		= loadImageCache();
	public static final ReleaseState		releaseState	= loadReleaseState();
	public static final WebcamSettings		webcamCache		= loadWebcamCache();
	
	public static Webcam[]		supportedCams;
	public static final Lock	webcamLock	= new Lock(true);
	
	private static final Checker checker = new Checker(
		ORG_NAME,
		PROGRAM_NAME,
		new CheckReleaseState(
			Utils::getUnixTime,
			releaseState));
	
	public static final SemVer currentVersion = currentVersion();
	
	public static void main(String[] args)
	{
		new Thread(() -> {
			supportedCams = Utils.initCams();
			webcamLock.notifyThread();
		}).start();
		queryNewest(false);
		ObservableGui.setDefaultAppImage(Opt.of(Assets.ICON));
		setLookAndFeel();
		
		final var lock2 = updateAvailable();
		backgroundInit();
		imageCache.loadFromCache();
		lock2.await();
		
		EventQueue.invokeLater(() -> new PasswdGui(gce -> {
			gce.getReturnValue()
				.if_(passwd -> EventQueue.invokeLater(() -> new MainGui(TinyTotp::terminateListener, passwd)))
				.else_(() -> terminateListener(null));
		}, windowStates.passwdWindow));
	}
	
	private static Lock updateAvailable()
	{
		if (releaseState.showUpdateNotification
				&& currentVersion != null
				&& currentVersion.isOlderThan(SemVer.fromGitTag(releaseState.latestVersion.tag_name)))
		{
			final var lock = new Lock(true);
			new Thread(() -> {
				new UpdateAvailable(
					null,
					ModalityType.MODELESS,
					releaseState.latestVersion,
					_gce -> lock.notifyThread());
			}).start();
			return lock;
		}
		return new Lock(false);
	}
	
	private static SemVer currentVersion()
	{
		try
		{
			return Checker.getLatestFromPomProperties("kryptonbutterfly", "tiny_totp");
		}
		catch (IllegalStateException | IllegalArgumentException | IOException e)
		{
			System.err.println("\n[IGNORING]\t");
			e.printStackTrace();
			System.err.println();
		}
		return null;
	}
	
	public static ReleaseInfo queryNewest(boolean force)
	{
		try
		{
			final var latestRelease = checker.latestRelease(force);
			if (latestRelease == null)
				return null;
			
			final var latestVersion = SemVer.fromGitTag(latestRelease.tag_name);
			if (currentVersion == null || currentVersion.isOlderThan(latestVersion))
				releaseState.latestVersion = latestRelease;
			
			return latestRelease;
		}
		catch (IOException | InterruptedException e)
		{
			System.err.print("\n[IGNORING]\t");
			e.printStackTrace();
			System.err.println();
			return null;
		}
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
	private static ReleaseState loadReleaseState()
	{
		if (!VERSION_STATE.exists())
			return new ReleaseState();
		return GSON.fromJson(Files.readString(VERSION_STATE.toPath()), ReleaseState.class);
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
	
	@SneakyThrows
	private static WebcamSettings loadWebcamCache()
	{
		if (!WEBCAM_CACHE.exists())
			return new WebcamSettings();
		return GSON.fromJson(Files.readString(WEBCAM_CACHE.toPath()), WebcamSettings.class);
	}
	
	private static void terminateListener(GuiCloseEvent<Void> gce)
	{
		writeToFileAsJson(config, CONFIG);
		writeToFileAsJson(windowStates, WINDOW_STATES);
		writeToFileAsJson(imageCache, CACHE);
		writeToFileAsJson(releaseState, VERSION_STATE);
		writeToFileAsJson(webcamCache, WEBCAM_CACHE);
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
			System.err.println("Failed setting program dock name. — Ignoring");
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
			System.err.println("Failed to apply System look and feel. – Ignoring");
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