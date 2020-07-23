package LGames;

public abstract class Cognition {

	
	public enum Category {
		
		COLOUR(0, "C"), SHAPE(1, "S"), DESTINATION(2, "D");
		
		private int value;
		private String shortName;
		Category(int v, String s){
			this.value = v;
			this.shortName = s;
		}
		
		public int getValue(){
			return this.value;
		}
		public String getShortName(){
			return this.shortName;
		}
		
	}
	
		protected abstract void categoriseObjects();
}
