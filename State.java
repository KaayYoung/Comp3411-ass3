import java.util.Objects;

/**
 * This class implements Comparable.
 * It stores the state of search which is using in A Star Search.
 * @author Yaoyang Cai, z5092306
 *
 */
public class State implements Comparable<State> {

    private Coordinate curr_coord;
    private int g_cost;
    private int h_cost;
    private int f_cost;
    private int numOfStones;
    private int isHave_raft;
    private State pre_state;
    private boolean standingOnWater;

    public State(Coordinate coord, int g_cost, int h_cost, int numOfStones, int isHave_raft, State pre_state) {
        this.curr_coord = coord;
        this.g_cost = g_cost;
        this.h_cost = h_cost;
        this.numOfStones = numOfStones;
        this.isHave_raft = isHave_raft;
        this.pre_state = pre_state;
    }

    /**
     * Override compareTo method to sort the Queue base on f cost
     */
    @Override
    public int compareTo(State o) {
        return this.getF_cost() - o.getF_cost();
    }

    /**
     * getter of standingOnWater
     * @return standingOnWater
     */
	public boolean isStandingOnWater() {
		return standingOnWater;
	}
	
	/**
	 * setter of standingOnWater
	 * @param standingOnWater
	 */
	public void setStandingOnWater(boolean standingOnWater) {
		this.standingOnWater = standingOnWater;
	}

	/**
	 * getter of current coordinate
	 * @return curr_coord
	 */
	public Coordinate getCurr_coord() {
        return curr_coord;
    }
	
	/**
	 * setter of current coordinate
	 * @param curr_coord
	 */
    public void setCurr_coord(Coordinate curr_coord) {
        this.curr_coord = curr_coord;
    }
    
    /**
     * getter of g cost
     * @return g_cost
     */
    public int getG_cost() {
        return g_cost;
    }

    /**
     * setter of g cost
     * @param g_cost
     */
    public void setG_cost(int g_cost) {
        this.g_cost = g_cost;
    }

    /**
     * getter of heuristic cost
     * @return h_cost
     */
    public int getH_cost() {
        return h_cost;
    }
    
    /**
     * setter of heuristic cost
     * @param h_cost
     */
    public void setH_cost(int h_cost) {
        this.h_cost = h_cost;
    }
    
    /**
     * getter of f cost
     * @return f_cost
     */
    public int getF_cost() {
        return f_cost;
    }

    /**
     * setter of f cost
     * @param f_cost
     */
    public void setF_cost(int f_cost) {
        this.f_cost = f_cost;
    }

    /**
     * getter of number of stones
     * @return numOfStones
     */
    public int getNumOfStones() {
        return numOfStones;
    }

    /**
     * setter of number of stones
     * @param numOfStones
     */
    public void setNumOfStones(int numOfStones) {
        this.numOfStones = numOfStones;
    }
    
    /**
     * getter of is have raft
     * @return isHave_raft
     */
    public int isHave_raft() {
        return isHave_raft;
    }

    /**
     * setter of is have raft
     * @param have_raft
     */
    public void setHave_raft(int have_raft) {
        isHave_raft = have_raft;
    }

    /**
     * getter of previous state
     * @return pre_state
     */
    public State getPre_state() {
        return pre_state;
    }

    /**
     * setter of previous state
     * @param pre_state
     */
    public void setPre_state(State pre_state) {
        this.pre_state = pre_state;
    }

    /**
     * Override equals method to use in contains method
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Objects.equals(curr_coord, state.curr_coord);
    }

    /**
     * Override hashCode method to use in contains method
     */
    @Override
    public int hashCode() {
        return Objects.hash(curr_coord);
    }
}
