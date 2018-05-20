import java.util.HashMap;

public interface Heuristic {

    public int calculateHeuristic(State start_state, Coordinate goal, HashMap<Coordinate, Character> map, HashMap<String, Integer> backpack);
}
