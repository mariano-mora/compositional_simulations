package LGames;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import LGames.Cognition.Category;

public class ContextStrategic extends Context {

	public enum ObjectColor implements IsCategorical {
		RED(Color.red, "red"), GREEN(Color.green, "green"), BLACK(Color.black, "black"), BLUE(Color.blue, "blue"), CYAN(
				Color.cyan, "cyan"), MAGENTA(Color.magenta, "magenta"), ORANGE(Color.orange, "orange"), PINK(Color.pink,
						"pink"), YELLOW(Color.yellow, "yellow"), GRAY(Color.gray, "gray"), DARK_GREY(Color.darkGray,
								"dark gray"), LIGHT_GRAY(Color.lightGray, "light gray"), WHITE(Color.white, "white");

		private Color color;
		private String name;

		private ObjectColor(Color c, String name) {
			this.color = c;
			this.name = name;
		}

		public String toString() {
			return name;
		}
		
		public String getName(){
			return this.name;
		}

		public Color getColor() {
			return this.color;
		}

		@Override
		public Category getCategory() {
			return Category.COLOUR;
		}
	}

	public enum Destination implements IsCategorical {
		LEFT(0, "left"), RIGHT(1, "right");
		private int value;
		private String name;

		private Destination(int value, String n) {
			this.value = value;
			this.name = n;
		}

		public int getValue() {
			return this.value;
		}
		
		public String toString(){
			return name;
		}
		
		public String getName(){
			return this.name;
		}

		@Override
		public Category getCategory() {
			return Category.DESTINATION;
		}
	}

	public enum Shape implements IsCategorical {
		CIRCLE(0, "circle"), SQUARE(1, "square"), RECTANGLE(2, "rectangle"), TRIANGLE(3, "triangle"), PENTAGON(4,
				"pentagon"), HEXAGON(5, "hexagon"), IRR_HEXAGON_1(6, "irreg hexagon"), IRR_HEXAGON_2(7,
						"irreg haxagon 2"), IRR_PENTAGON(8, "irreg pentagon"), CROSS(9, "cross");

		private int value;
		private String name;

		private Shape(int v, String n) {
			this.value = v;
			this.name = n;
		}

		public String toString() {
			return name;
		}

		public String getName(){
			return this.name;
		}
		
		@Override
		public Category getCategory() {
			return Category.SHAPE;
		}
	}

	public int maxShapes = Shape.values().length;
	public int maxColors = ObjectColor.values().length;
	

	public ContextStrategic(final int cxtSize, final boolean[] features, Random random) {
		super(cxtSize, features, true, random);
		objects = new ContextObject[cxtSize];
	}

	public ContextStrategic(final int cxtSize, final boolean[] features, Random random, int maxShapes, int maxColors) {
		this(cxtSize, features, random);
		this.maxShapes = maxShapes;
		this.maxColors = maxColors;
	}

	@Override
	public void buildContext() {
		for (int i = 0; i < cxtSize; i++) {
			ContextObject object = createObject();
			setPosition(object, i);
			objects[i] = object;
		}
	}

	protected void setPosition(ContextObject contextObject, int index) {
		int range = (int) (maxHeight0 - minHeight0) + 1;
		int pX = 0;
		int pY = 0;
		boolean fits = false;
		h = random.nextInt(range) + (int) minHeight0;
		b = random.nextInt(range) + (int) minHeight0;
		fits = false;
		while (!fits) {
			pX = (int) ((maxWidth - b) * random.nextDouble());
			pY = (int) ((maxHeight - h) * random.nextDouble());
			fits = true;
			for (int j = 0; j < index && fits; j++)
				fits = check_boundaries(pX, pY, b, h, j);
		}
		contextObject.x_position = pX;
		contextObject.y_position = pY;
		height[index] = h;
		width[index] = b;
		pointX[index] = pX;
		pointY[index] = pY;

	}

	public ObjectColor chooseColor(int c) {
		return ObjectColor.values()[c];
	}

	public Shape chooseShape(int s) {
		return Shape.values()[s];
	}

	public ContextObject selectObject() {
		return objects[random.nextInt(cxtSize)];
	}

	private ContextObject createObject() {
		ObjectColor c = this.chooseColor(random.nextInt(this.maxColors));
		Shape s = this.chooseShape(random.nextInt(this.maxShapes));
		return new ContextObject(c, s);
	}

}
