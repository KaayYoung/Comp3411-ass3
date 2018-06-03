import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * This class implements A Star Algorithm
 * @author Yaoyang Cai, z5092306
 * @author Jingshi Yang z5110579
 */
public class Astar {
    private Heuristic h;

    public Astar(){
        this.h = new AstarHeustic();
    }

    /**
     * This method implements A Star Search
     * @param start_state
     * @param goal
     * @param map
     * @param backpack
     * @param onWater
     * @return pathToGoal
     */
    public LinkedList<State> aStarSearch(State start_state, Coordinate goal, HashMap<Coordinate, Character> map, HashMap<String, Integer> backpack, boolean onWater) {

        LinkedList<State> pathToGoal = new LinkedList<>();
        PriorityQueue<State> toVisit = new PriorityQueue<State>();
        ArrayList<State> visited = new ArrayList<State>();

        toVisit.add(start_state);

        while(!toVisit.isEmpty()) {

            State curr_state = toVisit.poll();
            /* break the loop if find a path to the goal */
            if (curr_state.getCurr_coord().equals(goal)) {
                pathToGoal = traceBack(curr_state, start_state, backpack);
                break;
            }

            visited.add(curr_state);
            for (Coordinate possible: possibleMoves(curr_state.getCurr_coord(), map)) {
                State nextState = new State(possible, curr_state.getG_cost() + 1, 0, curr_state.getNumOfStones(), curr_state.isHave_raft(), curr_state);

                /* set the number of raft to 0 */
                if (map.get(curr_state.getCurr_coord()) == '~' && map.get(possible) != '~' && curr_state.getNumOfStones() == 0) {
                    nextState.setHave_raft(0);
                }
                
                /* decrement the number of stones */
                if (map.get(curr_state.getCurr_coord()) == '~' && curr_state.getNumOfStones() > 0) {
                    nextState.setNumOfStones(nextState.getNumOfStones() - 1);
                } 

                /* calculate the heuristic and set h_cost and f_cost */
                int hCost = h.calculateHeuristic(nextState, goal, map, backpack, onWater);
                nextState.setH_cost(hCost);
                nextState.setF_cost(nextState.getG_cost() + nextState.getH_cost());
               
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

    /**
     * This method using trace back to return a list of states
     * @param curr_state
     * @param start_state
     * @param backpack
     * @return pathToGoal
     */
    public LinkedList<State> traceBack(State curr_state, State start_state, HashMap<String, Integer> backpack) {

        LinkedList<State> pathToGoal = new LinkedList<>();
        int totalCost = 0;
        pathToGoal.add(curr_state);
        
        /* add each state to the list */
        while (curr_state.getPre_state() != null) {
            totalCost = totalCost + curr_state.getF_cost();
            pathToGoal.add(curr_state.getPre_state());
            curr_state = curr_state.getPre_state(); 
        }

        /* if the cost if INFINITY, then clear the list */
        if (totalCost >= 100000) {
            pathToGoal.clear();
            return pathToGoal;
        } else {
            return pathToGoal;
        }
    }

    /**
     * This method returns all possible moves, which depends on the current coordinate
     * It checks four directions of possible moves.
     * @param curr_coord
     * @param map
     * @return listOfMoves
     */
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
