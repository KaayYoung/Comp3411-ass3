/**
 * 
 * @author z5092306
 *
 */
public class Coordinate {
	private int x;
	private int y;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof Coordinate)) return false;
		Coordinate coord = (Coordinate) o;
		return coord.getX() == x && coord.getY() == y;
	}
	
	@Override
	public int hashCode() {
		int hash = 19;
		hash = 29 * hash + x;
		hash = 29 * hash + y;
		return hash;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	
}
