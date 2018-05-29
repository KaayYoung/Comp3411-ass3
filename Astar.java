import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Astar {
    private Heuristic h;

    public Astar(){
        this.h = new AstarHeustic();
    }

    public LinkedList<State> aStarSearch(State start_state, Coordinate goal, HashMap<Coordinate, Character> map, HashMap<String, Integer> backpack) {

        LinkedList<State> pathToGoal = new LinkedList<>();
        PriorityQueue<State> toVisit = new PriorityQueue<>();
        PriorityQueue<State> visited = new PriorityQueue<>();

        // TODO: implement state class
        toVisit.add(start_state);

        while(!toVisit.isEmpty()) {

            State curr_state = toVisit.poll();
            // Get the path when we find the goal
            if (curr_state.getCurr_coord().equals(goal)) {
                pathToGoal = traceBack(curr_state, start_state);
                break;
            }

            visited.add(curr_state);
            for (Coordinate possible: possibleMoves(curr_state.getCurr_coord(), map)) {
//                State nextState;
                System.out.println("possible:" + possible.getX() + " " + possible.getY() + "curr_state:" + curr_state.getCurr_coord().getX() + curr_state.getCurr_coord().getY());
                //System.out.println("keeeeeeeey:" + backpack.get("Key"));
                State nextState = new State(possible, curr_state.getG_cost() + 1, 0, curr_state.getNumOfStones(), curr_state.isHave_raft(), curr_state);
                nextState.setH_cost(h.calculateHeuristic(nextState, goal, map, backpack));
                // System.out.println(nextState.getF_cost());
                if (visited.contains(nextState)) {
                    continue;
                }
                if (!toVisit.contains(nextState)) {
                    toVisit.add(nextState);
                }

            }
        }

        return pathToGoal;
    }

    public LinkedList<State> traceBack(State curr_state, State start_state) {

        LinkedList<State> pathToGoal = new LinkedList<>();
        int totalCost = 0;
        pathToGoal.add(curr_state);
        while (curr_state.getPre_state() != null) {
            // System.out.println("{");
            // System.out.println(curr_state.getPre_state().getCurr_coord().getX()+" "+curr_state.getPre_state().getCurr_coord().getY());
            // System.out.println("}");
            totalCost = totalCost + curr_state.getPre_state().getF_cost();
            System.out.println("Pre_fcost:" + curr_state.getPre_state().getF_cost());
            System.out.println("Pre_hcost:" + curr_state.getPre_state().getH_cost());
            //System.out.println("totalcost:" + totalCost);
            pathToGoal.add(curr_state.getPre_state());
            curr_state = curr_state.getPre_state();
            // if(curr_state == null) System.out.println("1");
            // if(curr_state.getPre_state() == null) System.out.println("2");
            
        }
        System.out.println("totalcost:" + totalCost);
//        System.out.println("break");
//        System.exit(0);
        if (totalCost >= 10000) {
            pathToGoal.clear();
            return pathToGoal;
        } else {
            return pathToGoal;
        }
    }

    public ArrayList<Coordinate> possibleMoves(Coordinate curr_coord, HashMap<Coordinate, Character> map) {

        ArrayList<Coordinate> listOfMoves = new ArrayList<>();
        Coordinate moveToEast = new Coordinate(curr_coord.getX() + 1, curr_coord.getY());
        Coordinate moveToNorth = new Coordinate(curr_coord.getX(), curr_coord.getY() + 1);
        Coordinate moveToWest = new Coordinate(curr_coord.getX() - 1, curr_coord.getY());
        Coordinate moveToSouth = new Coordinate(curr_coord.getX(), curr_coord.getY() - 1);

        if (map.containsKey(moveToEast) && map.get(moveToEast) != '*') {
            listOfMoves.add(moveToEast);
        }
        if (map.containsKey(moveToNorth) && map.get(moveToNorth) != '*') {
            listOfMoves.add(moveToNorth);
        }
        if (map.containsKey(moveToWest) && map.get(moveToWest) != '*') {
            listOfMoves.add(moveToWest);
        }
        if (map.containsKey(moveToSouth) && map.get(moveToSouth) != '*') {
            listOfMoves.add(moveToSouth);
        }
        return listOfMoves;
    }

}
