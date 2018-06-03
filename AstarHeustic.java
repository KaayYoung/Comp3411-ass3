import java.util.HashMap;

import static java.lang.Math.abs;

public class AstarHeustic implements Heuristic{

    // TODO: when use stone and raft, need to decrease the number of stones and raft

    @Override
    public int calculateHeuristic(State curr_state, Coordinate goal, HashMap<Coordinate, Character> map, HashMap<String, Integer> backpack, boolean onWater) {
        Coordinate curr_point = curr_state.getCurr_coord();

        int heuristic = abs(curr_point.getX() - goal.getX()) + abs(curr_point.getY() - goal.getY());

        if (map.get(curr_point) == '.') {
            //System.out.println("111111111111111");
            heuristic = 100001;
        } else if (map.get(curr_point) == 'T' && backpack.get("Axe") == 0) {
            //System.out.println("222222222222222");
            heuristic = 100002;
        } else if (map.get(curr_point) == '-' && backpack.get("Key") == 0) {
            //System.out.println("33333333333333");
            heuristic = 100003;
        } else if (map.get(curr_point) == '~' && curr_state.getNumOfStones() > 0 ) {
//            System.out.println("6666666666666");
            heuristic = 1000;
            //curr_state.setNumOfStones(curr_state.getNumOfStones() - 1);
        } else if (map.get(curr_point) == '~' && curr_state.getNumOfStones() == 0 && curr_state.isHave_raft() == 0) {

//            System.out.println("5555555555555555");
            heuristic = 100004;
        }  else if (map.get(curr_point) == '*'){
            //System.out.println("7777777777777");
            heuristic = 100005;
        } else if (map.get(curr_point) == 'O') {
            heuristic = abs(curr_point.getX() - goal.getX()) + abs(curr_point.getY() - goal.getY());
        } else if (map.get(curr_point) == 'T' && map.get(curr_state.getPre_state().getCurr_coord()) == '~') {
            heuristic = 100006;
        }

        if (onWater && map.get(curr_point) != '~') {
//            System.out.println("888888888");
            heuristic = 100007;
        } 

        return heuristic;
    }

}
