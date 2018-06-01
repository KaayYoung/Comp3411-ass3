import java.util.HashMap;

import static java.lang.Math.abs;

public class AstarHeustic implements Heuristic{

    // TODO: when use stone and raft, need to decrease the number of stones and raft

    @Override
    public int calculateHeuristic(State curr_state, Coordinate goal, HashMap<Coordinate, Character> map, HashMap<String, Integer> backpack, boolean expand_water) {
        Coordinate curr_point = curr_state.getCurr_coord();

        int heuristic = abs(curr_point.getX() - goal.getX()) + abs(curr_point.getY() - goal.getY());

        if (map.get(curr_point) == '.') {
            heuristic = 100000;
        } else if (map.get(curr_point) == 'T' && backpack.get("Axe") == 0) {
            heuristic = 100000;
        } else if (map.get(curr_point) == '-' && backpack.get("Key") == 0) {
            heuristic = 100000;
        } else if (map.get(curr_point) == '~' && curr_state.getNumOfStones() == 0 && curr_state.getIsHave_raft() == 0) {
            heuristic = 100000;
        } else if (map.get(curr_point) == '~' && curr_state.getNumOfStones() > 0) {
            heuristic = 1000;
        } else if (map.get(curr_point) == '*'){
            heuristic = 100000;
        }

        // if(water_round < 2){

        //     if(map.get(curr_point) == ' ' && map.get(curr_point) == 'O'){
        //         heuristic = 100000;
        //     }
        // } else{
        //     // heuristic return back to normal
        //     // find a path to the treasure and return back to start
        //     // find a path to item
        //     // find a path to tree
        // }

        // A

        return heuristic;
    }

}
