import java.util.HashMap;

import static java.lang.Math.abs;

public class AstarHeustic implements Heuristic{

    // TODO: when use stone and raft, need to decrease the number of stones and raft

    @Override
    public int calculateHeuristic(State curr_state, Coordinate goal, HashMap<Coordinate, Character> map, HashMap<String, Integer> backpack, boolean onWater) {
        Coordinate curr_point = curr_state.getCurr_coord();

        int heuristic = abs(curr_point.getX() - goal.getX()) + abs(curr_point.getY() - goal.getY());

        if (map.get(curr_point) == '.') {
            heuristic = 100001;
        } else if (map.get(curr_point) == 'T' && backpack.get("Axe") == 0) {
            heuristic = 100002;
        } else if (map.get(curr_point) == '-' && backpack.get("Key") == 0) {
            heuristic = 100003;
        } else if (map.get(curr_point) == '~' && curr_state.getNumOfStones() > 0 ) {
            heuristic = 1000;
        } else if (map.get(curr_point) == '~' && curr_state.getNumOfStones() == 0 && curr_state.isHave_raft() == 0) {
            heuristic = 100004;
        }  else if (map.get(curr_point) == '*'){
            heuristic = 100005;
        } else if (map.get(curr_point) == 'O') {
            heuristic = abs(curr_point.getX() - goal.getX()) + abs(curr_point.getY() - goal.getY());
        } else if (map.get(curr_point) == 'T' && map.get(curr_state.getPre_state().getCurr_coord()) == '~') {
            heuristic = 100006;
        }

        if (onWater && map.get(curr_point) != '~') {
            heuristic = 100007;
        } 

        return heuristic;
    }

}
