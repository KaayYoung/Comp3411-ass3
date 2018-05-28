import java.util.HashMap;

import static java.lang.Math.abs;

public class AstarHeustic implements Heuristic{

    // TODO: when use stone and raft, need to decrease the number of stones and raft

    @Override
    public int calculateHeuristic(State curr_state, Coordinate goal, HashMap<Coordinate, Character> map, HashMap<String, Integer> backpack) {
        Coordinate curr_point = curr_state.getCurr_coord();

        int heuristic = abs(curr_point.getX() - goal.getX()) + abs(curr_point.getY() - goal.getY());

        if (map.get(curr_point) == '.') {
            heuristic = 100000;
        } else if (map.get(curr_point) == 'T' && backpack.get("Axe") == 0) {
            heuristic = 100000;
        } else if (map.get(curr_point) == '-' && backpack.get("Key") == 0) {
            heuristic = 100000;
        } else if (map.get(curr_point) == '~' && backpack.get("Stones") == 0 && backpack.get("Raft") == 0) {
            heuristic = 100000;
        } else if (map.get(curr_point) == '*'){
            heuristic = 100000;
        }

        return heuristic;
    }

}
