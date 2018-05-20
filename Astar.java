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
            }

            visited.add(curr_state);
            for (Coordinate possible: possibleMoves(curr_state.getCurr_coord(), map)) {
//                State nextState;
                State nextState = new State(possible, curr_state.getG_cost() + 1, h.calculateHeuristic(curr_state, goal, map, backpack), curr_state.getNumOfStones(), curr_state.isHave_raft(), curr_state);

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
        while (!curr_state.getCurr_coord().equals(start_state.getCurr_coord())) {
            pathToGoal.add(curr_state.getPre_state());
            curr_state = curr_state.getPre_state();
            totalCost = totalCost + curr_state.getPre_state().getF_cost();
        }

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
