/**
 * This class stores the coordinate
 * @author Yaoyang Cai, z5092306
 * @author jingshi Yang z5110579
 */
public class Coordinate {
	
	private int x;
	private int y;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Override the equals method
	 */
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof Coordinate)) return false;
		Coordinate coord = (Coordinate) o;
		return coord.getX() == x && coord.getY() == y;
	}
	
	/**
	 * Override the hashCode method
	 */
	@Override
	public int hashCode() {
		int hash = 19;
		hash = 29 * hash + x;
		hash = 29 * hash + y;
		return hash;
	}
	
	/**
	 * x coordinate getter
	 * @return - x
	 */
	public int getX() {
		return x;
	}

	/**
	 * x coordinate setter
	 * @param x
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * y coordinate getter
	 * @return - y
	 */
	public int getY() {
		return y;
	}

	/**
	 * y coordinate setter
	 * @param y
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	
}
