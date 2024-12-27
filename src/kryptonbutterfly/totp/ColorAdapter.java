package kryptonbutterfly.totp;

import java.awt.Color;
import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

class ColorAdapter extends TypeAdapter<Color>
{
	private static final String	RED		= "red";
	private static final String	GREEN	= "green";
	private static final String	BLUE	= "blue";
	private static final String	ALPHA	= "alpha";
	
	private static final int	RED_MASK	= 0b001;
	private static final int	GREEN_MASK	= 0b010;
	private static final int	BLUE_MASK	= 0b100;
	
	private static final int COMPLETE_MASK = 0b111;
	
	@Override
	public void write(JsonWriter out, Color value) throws IOException
	{
		if (value == null)
			out.nullValue();
		else
			out.beginObject()
				.name(RED)
				.value(value.getRed())
				.name(GREEN)
				.value(value.getGreen())
				.name(BLUE)
				.value(value.getBlue())
				.name(ALPHA)
				.value(value.getAlpha())
				.endObject();
	}
	
	@Override
	public Color read(JsonReader in) throws IOException
	{
		int fieldMask = 0;
		
		int	red		= 0;
		int	green	= 0;
		int	blue	= 0;
		int	alpha	= 0xFF;
		
		in.beginObject();
		
		while (in.hasNext())
		{
			final var token = in.peek();
			
			if (token.equals(JsonToken.NAME))
				switch (in.nextName())
				{
					case RED -> {
						red			= in.nextInt();
						fieldMask	|= RED_MASK;
					}
					case GREEN -> {
						green		= in.nextInt();
						fieldMask	|= GREEN_MASK;
					}
					case BLUE -> {
						blue		= in.nextInt();
						fieldMask	|= BLUE_MASK;
					}
					case ALPHA -> alpha = in.nextInt();
				}
		}
		
		in.endObject();
		
		if (COMPLETE_MASK != fieldMask)
			throw new IllegalStateException("Color requires a red, green and blue value!");
		
		return new Color(red, green, blue, alpha);
	}
}