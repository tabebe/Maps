package gui;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import edu.brown.cs.ta7.maps.Node;
import edu.brown.cs.ta7.maps.Way;

public class nodeSerializer implements JsonSerializer<Node>{

	
	
	public JsonElement serialize(Node src, Type typeOfSrc, 
			JsonSerializationContext context) {
		
		JsonObject obj = new JsonObject();
		obj.add("lat", new JsonPrimitive(src.getCoors().get(0)));
		obj.add("long", new JsonPrimitive(src.getCoors().get(1)));
		return obj;
	}
	
	
	
}
