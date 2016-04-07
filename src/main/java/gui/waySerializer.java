package gui;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import edu.brown.cs.ta7.maps.Node;
import edu.brown.cs.ta7.maps.Way;

public class waySerializer implements JsonSerializer<Way>{
	
	private static final nodeSerializer S = new nodeSerializer();
	
	
	public JsonElement serialize(Way src, Type typeOfSrc, 
			JsonSerializationContext context) {
		ArrayList<Double> coors = new ArrayList<Double>(); 
		Node node = new Node("id");
		JsonObject obj = new JsonObject();
		//JsonElement start = S.serialize()
	} 
	
	
}
