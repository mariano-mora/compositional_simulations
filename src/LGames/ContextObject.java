package LGames;

import java.util.HashMap;
import java.util.Map;

import LGames.Cognition.Category;
import LGames.ContextStrategic.ObjectColor;
import LGames.ContextStrategic.Shape;;

public class ContextObject {

	public Map<Category, IsCategorical> values = new HashMap<Category, IsCategorical>();
	public int x_position;
	public int y_position;
	
	
	public ContextObject(ObjectColor color, Shape shape, int x_position, int y_position) {
		this.values.put(Category.COLOUR,color);
		this.values.put(Category.SHAPE,shape);
		this.x_position = x_position;
		this.y_position = y_position;
	}
	
	
	public ContextObject(ObjectColor c, Shape s){
		this.values.put(Category.COLOUR,c);
		this.values.put(Category.SHAPE,s);
	}


}
