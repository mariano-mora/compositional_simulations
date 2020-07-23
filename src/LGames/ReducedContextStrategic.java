package LGames;

import java.util.Random;

public class ReducedContextStrategic extends ContextStrategic {

	
	public ReducedContextStrategic(int cxtSize, boolean[] features, Random random) {
		super(cxtSize, features, random);
		this.maxColors = 2;
		this.maxShapes = 2;
	}
	
	
	@Override
	public void buildContext(){
		int counter = 0;
		for (int i=0;i<this.maxShapes;i++){
			for(int j=0;j<this.maxColors;j++){
				ContextObject object = new ContextObject(ObjectColor.values()[i], Shape.values()[j]);
				objects[counter++] = object;
			}
		}
	}

}
