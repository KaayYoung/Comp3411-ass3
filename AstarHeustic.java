import java.util.HashMap;

import static java.lang.Math.abs;

/**
 * This method implements Heuristic interface.
 * It checks if the next step is possible or not.
 * If the next step is invalid, then return a huge heuristic,
 * else return a normal heuristic (sum of difference of x-axis and difference of y-axis).
 * @author Yaoyang Cai, z5092306
 * @author Jingshi Yang z5110579
 */
public class AstarHeustic implements Heuristic{

	private final int INFINITY = 100000;
	private final int LARGE = 1000;
	
	/**
	 * Calculate the heuristic of current state
	 */
    @Override
    public int calculateHeuristic(State curr_state, Coordinate goal, HashMap<Coordinate, Character> map, HashMap<String, Integer> backpack, boolean onWater) {
        Coordinate curr_point = curr_state.getCurr_coord();

        /* calculate heuristic */
        int heuristic = abs(curr_point.getX() - goal.getX()) + abs(curr_point.getY() - goal.getY());

        if (map.get(curr_point) == '.') {
        	/* if moving to area that outside of the border of the map */
            heuristic = INFINITY;
        } else if (map.get(curr_point) == 'T' && backpack.get("Axe") == 0) {
        	/* if moving to a tree without an axe */
            heuristic = INFINITY;
        } else if (map.get(curr_point) == '-' && backpack.get("Key") == 0) {
        	/* if moving to a door without a key */
            heuristic = INFINITY;
        } else if (map.get(curr_point) == '~' && curr_state.getNumOfStones() > 0 ) {
        	/* if moving to a water without a stone */
        	/* here is different, the heuristic would be set to LARGE since it is still possible */
            heuristic = LARGE;
        } else if (map.get(curr_point) == '~' && curr_state.getNumOfStones() == 0 && curr_state.isHave_raft() == 0) {
            /* if moving to a water without a stone or a raft */
        	heuristic = INFINITY;
        }  else if (map.get(curr_point) == '*'){
        	/* if moving to a wall */
            heuristic = INFINITY;
        } else if (map.get(curr_point) == 'O') {
        	/* if standing on a stone */
            heuristic = abs(curr_point.getX() - goal.getX()) + abs(curr_point.getY() - goal.getY());
        } else if (map.get(curr_point) == 'T' && map.get(curr_state.getPre_state().getCurr_coord()) == '~') {
        	/* if moving from water to a tree */
        	/* it is impossible to cut a tree on a raft */
        	heuristic = INFINITY;
        }

        if (onWater && map.get(curr_point) != '~') {
        	/* while exploring map on water, only walk on water */
            heuristic = INFINITY;
        } 

        return heuristic;
    }

}
