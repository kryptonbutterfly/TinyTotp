package d.t.totp.prefs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.google.gson.annotations.Expose;

import d.t.totp.TotpConstants;
import d.t.totp.misc.ImageUtils;
import de.tinycodecrank.monads.opt.Opt;
import lombok.SneakyThrows;
import lombok.val;

public final class TotpImageCache implements TotpConstants
{
	public TotpImageCache(File file)
	{
		this.configFile = file;
	}
	
	public TotpImageCache()
	{}
	
	private transient File configFile = null;
	
	@Expose
	public HashMap<String, String> imageMappings = new HashMap<>();
	
	private transient HashMap<String, BufferedImage> images = new HashMap<>();
	
	public File file()
	{
		return configFile;
	}
	
	public void file(File file)
	{
		this.configFile = file;
	}
	
	public void loadFromCache()
	{
		for (var value : imageMappings.values())
		{
			final var file = new File(configFile.getParentFile(), value + "." + PNG);
			try
			{
				Opt.of(file)
					.filter(File::exists)
					.mapThrows(ImageIO::read)
					.if_(image -> images.put(value, image));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public Opt<BufferedImage> getImage(String address)
	{
		if (address == null)
			return Opt.empty();
		return Opt.of(imageMappings.get(address))
			.map(images::get)
			.flatOr(() -> fetchImage(address));
	}
	
	private Opt<BufferedImage> fetchImage(String address)
	{
		if (!configFile.getParentFile().exists())
			configFile.getParentFile().mkdirs();
		
		final var name = generateCacheName(address);
		
		final var imgFile = new File(configFile.getParentFile(), name + "." + PNG);
		try
		{
			final var	url		= new URL(address);
			final var	image	= ImageUtils.scaleDownToMax(ImageIO.read(url), CACHED_IMAGE_MAX_SIZE);
			ImageIO.write(image, PNG, imgFile);
			images.put(name, image);
			imageMappings.put(address, name);
			return Opt.of(image);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return Opt.empty();
	}
	
	public Opt<String> addImage(BufferedImage image)
	{
		image = ImageUtils.scaleDownToMax(image, CACHED_IMAGE_MAX_SIZE);
		if (!configFile.getParentFile().exists())
			configFile.getParentFile().mkdirs();
		
		final var	uniqueName	= generateUniqueName();
		final var	name		= generateCacheName(uniqueName);
		
		final var imgFile = new File(configFile.getParentFile(), name + "." + PNG);
		try
		{
			ImageIO.write(image, PNG, imgFile);
			images.put(name, image);
			imageMappings.put(uniqueName, name);
			return Opt.of(name);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return Opt.empty();
		}
	}
	
	private String generateUniqueName()
	{
		String name = null;
		while (name == null || imageMappings.containsKey(name))
			name = UUID.randomUUID().toString();
		return name;
	}
	
	@SneakyThrows
	private String generateCacheName(String address)
	{
		val	digest	= MessageDigest.getInstance(SHA1);
		val	hash	= digest.digest(address.getBytes());
		
		val sb = new StringBuilder(hash.length * 2);
		for (byte b : hash)
			sb.append("%02X".formatted(0xFF & b));
		return sb.toString();
	}
}