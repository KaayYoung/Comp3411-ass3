import java.util.HashMap;

/**
 * Heuristic interface
 * @author Yaoyang Cai, z5092306
 * @author jingshi Yang z5110579
 */
public interface Heuristic {
	/**
	 * calculate the heuristic of current state
	 * @param start_state
	 * @param goal
	 * @param map
	 * @param backpack
	 * @param onWater
	 * @return heuristic
	 */
    public int calculateHeuristic(State start_state, Coordinate goal, HashMap<Coordinate, Character> map, HashMap<String, Integer> backpack, boolean onWater);
}
