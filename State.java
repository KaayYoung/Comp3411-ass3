public class State implements Comparable<State> {

    private Coordinate curr_coord;
    private int g_cost;
    private int h_cost;
    private int f_cost;
    private int numOfStones;
    private boolean isHave_raft;
    private State pre_state;

    public State(Coordinate coord, int g_cost, int h_cost, int numOfStones, boolean isHave_raft, State pre_state) {
        this.curr_coord = coord;
        this.g_cost = g_cost;
        this.h_cost = h_cost;
        this.f_cost = g_cost + h_cost;
        this.numOfStones = numOfStones;
        this.isHave_raft = isHave_raft;
        this.pre_state = pre_state;
    }

    @Override
    public int compareTo(State o) {
        return this.getF_cost() - o.getF_cost();
    }

    public Coordinate getCurr_coord() {
        return curr_coord;
    }

    public void setCurr_coord(Coordinate curr_coord) {
        this.curr_coord = curr_coord;
    }

    public int getG_cost() {
        return g_cost;
    }

    public void setG_cost(int g_cost) {
        this.g_cost = g_cost;
    }

    public int getH_cost() {
        return h_cost;
    }

    public void setH_cost(int h_cost) {
        this.h_cost = h_cost;
    }

    public int getF_cost() {
        return f_cost;
    }

    public void setF_cost(int f_cost) {
        this.f_cost = f_cost;
    }

    public int getNumOfStones() {
        return numOfStones;
    }

    public void setNumOfStones(int numOfStones) {
        this.numOfStones = numOfStones;
    }

    public boolean isHave_raft() {
        return isHave_raft;
    }

    public void setHave_raft(boolean have_raft) {
        isHave_raft = have_raft;
    }

    public State getPre_state() {
        return pre_state;
    }

    public void setPre_state(State pre_state) {
        this.pre_state = pre_state;
    }
}
