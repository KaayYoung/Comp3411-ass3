/*********************************************
 *  Agent.java 
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411/9414/9814 Artificial Intelligence
 *  UNSW Session 1, 2018
*/

// TODO: go to 4 corners of current view, set up one global variable to store the number of expands

import java.util.*;
import java.io.*;
import java.net.*;

import static java.lang.Math.abs;

public class Agent {

    final static int EAST = 0;
    final static int NORTH = 1;
    final static int WEST = 2;
    final static int SOUTH = 3;

    final static int INITIALCOORD = 200;

    private HashMap<Coordinate, Character> map;
    private static HashMap<String, Integer> backpack;
    private static HashMap<Coordinate, Integer> explored;

    private LinkedList<Coordinate> ItemToTake;
    private LinkedList<Coordinate> TreeToCut;
    private LinkedList<State> toTreasure;
    private LinkedList<Character> moves_to_treasure;
    private LinkedList<State> toItem;
    private LinkedList<Character> moves_to_item;
    private LinkedList<State> toStart;
    private LinkedList<Character> moves_back_start;
    private LinkedList<State> toTree;
    private LinkedList<Character> moves_to_tree;
    private LinkedList<Character> expand_map_steps;

    private Coordinate TreasureCoord;
    private static Coordinate curr_location;
    private int direction;
    private int tempDirection;

    private int time = 0;

    private char lastMove;
    private char currMove;

    private int num_stones;
	private boolean takeTreasure;
	private boolean backHome;
	private boolean goingToItem;
	private boolean goingToTree;
    private boolean found;

	private boolean goAround;
    private boolean expand_water;

    Astar findPath;
	
    public Agent () {
        map = new HashMap<>();
        backpack = new HashMap<>();
        explored = new HashMap<>();

        ItemToTake = new LinkedList<>();
        TreeToCut = new LinkedList<>();
        toTreasure = new LinkedList<>();
        moves_to_treasure = new LinkedList<>();
        toItem = new LinkedList<>();
        moves_to_item = new LinkedList<>();
        toStart = new LinkedList<>();
        moves_back_start = new LinkedList<>();
        toTree = new LinkedList<>();
        moves_to_tree = new LinkedList<>();
        expand_map_steps = new LinkedList<>();

        TreasureCoord = new Coordinate(INITIALCOORD, INITIALCOORD);
        curr_location = new Coordinate(0, 0);
        direction = NORTH;
        tempDirection = NORTH;

        lastMove = '%';
        currMove = '%';
        num_stones = 0;

        takeTreasure = false;
        backHome = false;
        goingToItem = false;
        goingToTree = false;
        found = false;

        goAround = false;
        expand_water = false;

        findPath = new Astar();
    }

    public void updateMap(char view[][]) {

        
        if (currMove == '%') {
            initializeMap(view);
        } else if (currMove == 'r') {
            clockwise();
        } else if (currMove == 'l') {
            anticlockwise();
        } else if (currMove == 'f') {
            MoveForward(view);
        } else if (lastMove == 'c') {
        	goingToTree = false;
        }
 
        if (map.get(curr_location) == '$') {
            // TreasureCoord.setX(INITIALCOORD);
            // TreasureCoord.setY(INITIALCOORD);
            found = true;
            map.put(curr_location, ' ');
            backpack.put("Treasure", 1);
        } else if (map.get(curr_location) == 'k') {
            map.put(curr_location, ' ');
            goingToItem = false;
            ItemToTake.remove(curr_location);
            backpack.put("Key", 1);
        } else if (map.get(curr_location) == 'a') {
            map.put(curr_location, ' ');
            goingToItem = false;
            ItemToTake.remove(curr_location);
            backpack.put("Axe", 1);
        } else if (map.get(curr_location) == 'o') {
            num_stones++;
            map.put(curr_location, ' ');
            goingToItem = false;
            ItemToTake.remove(curr_location);
            backpack.put("Stones", num_stones);
        }

    }

    public void initializeMap (char view[][]) {
        backpack.put("Raft",0);
        backpack.put("Key",0);
        backpack.put("Stones",0);
        backpack.put("Treasure",0);
        backpack.put("Axe",0);
        int x = -2;
        for (int col = 0; col < 5; col++) {
            int y = 2;
            for (int row = 0; row < 5; row++) {
                Coordinate coord = new Coordinate(x, y);
                if (x == 0 && y ==0) {
                    // initialize the starting point as #
//                    direction = 0;
                    map.put(coord, '#');
                    explored.put(coord, 0);
                } else {
                    map.put(coord, view[row][col]);
                    explored.put(coord, 0);
                    if (view[row][col] == 'k' || view[row][col] == 'a' || view[row][col] == 'o' ) {
                        ItemToTake.add(coord);
                    }

                    if (view[row][col] == '$') {
                        TreasureCoord = coord;
                    }

                    if (view[row][col] == 'T') {
                        TreeToCut.add(coord);
                    }
                }

                y--;
            }
            x++;
        }
        System.out.println("ininininininini");
    }

    public void clockwise() {
        direction--;
        direction = direction % 4;
        if (direction < 0) direction = direction + 4;
    }

    public void anticlockwise() {
        direction++;
        direction = direction % 4;
    }



    public void isItemInView (char item, Coordinate coord) {
        if (item == 'k' || item == 'a' || item == 'o') {
            if (!ItemToTake.contains(coord) && map.containsKey(coord)) ItemToTake.add(coord);
        }
        if (item == '$') {
            TreasureCoord = coord;
        }
        if (item == 'T') {
        	if (!TreeToCut.contains(coord)) TreeToCut.add(coord);
        }
    }

    public void MoveForward(char view[][]) {

        explored.put(curr_location, explored.get(curr_location) +  1);
        System.out.println("curr_move in moveforward:" + currMove);
        if (direction == EAST) {
            curr_location.setX(curr_location.getX() + 1);
            int expandPart = 0;

            // We can see one more column when move EAST, get the coordinate for
            // that column from the first row to the fifth row
            for (int row = 2; row >= -2; row--) {
                Coordinate expandRow = new Coordinate(curr_location.getX() + 2, curr_location.getY() + row);
                char expandChar = view[0][expandPart];
                map.put(expandRow, expandChar);
                if(!explored.containsKey(expandRow)) explored.put(expandRow, 0);
                isItemInView(expandChar, expandRow);
                expandPart++;
            }
        } else if (direction == NORTH) {
            curr_location.setY(curr_location.getY() + 1);
            int expandPart = 0;
            for (int col = -2; col <= 2; col++) {
                Coordinate expandCol = new Coordinate(curr_location.getX() + col, curr_location.getY() + 2);
                char expandChar = view[0][expandPart];
                map.put(expandCol, expandChar);
                if(!explored.containsKey(expandCol)) explored.put(expandCol, 0);
                isItemInView(expandChar, expandCol);
                expandPart++;
            }
        } else if (direction == WEST) {
            curr_location.setX(curr_location.getX() - 1);
            int expandPart = 0;
            for (int row = -2; row <= 2; row++) {
                Coordinate expandRow = new Coordinate(curr_location.getX() - 2, curr_location.getY() + row);
                char expandChar = view[0][expandPart];
                map.put(expandRow, expandChar);
                if(!explored.containsKey(expandRow)) explored.put(expandRow, 0);
                isItemInView(expandChar, expandRow);
                expandPart++;
            }
        } else if (direction == SOUTH) {
            curr_location.setY(curr_location.getY() - 1);
            int expandPart = 0;
            for (int col = 2; col >= -2; col --) {
                Coordinate expandCol = new Coordinate(curr_location.getX() + col, curr_location.getY() - 2);
                char expandChar = view[0][expandPart];
                map.put(expandCol, expandChar);
                if(!explored.containsKey(expandCol)) explored.put(expandCol, 0);
                isItemInView(expandChar, expandCol);
                expandPart++;
            }
        }

        if (lastMove == 'c') {
        	System.out.println("last:" + lastMove);
        	System.out.println("cuuuuuut");
           	System.out.println(curr_location.getX() + " " + curr_location.getY());
            for (int i = 0; i < TreeToCut.size(); i++) {
                if (curr_location.equals(TreeToCut.get(i))) {
                   	System.out.println("removeTree:" + TreeToCut.get(i).getX() + " " + TreeToCut.get(i).getY());
                    TreeToCut.remove(TreeToCut.get(i));
                }
            }
            lastMove = '%';
        }
    }


    public LinkedList<Character> stateToMove(LinkedList<State> list_of_states) {

        LinkedList<Character> move_instructions = new LinkedList<>();
        LinkedList<Character> listOfMoves;

        State curr_state = list_of_states.pollLast();

        tempDirection = direction;

        while (!list_of_states.isEmpty()) {
            listOfMoves = moveDecision(curr_state.getCurr_coord(), list_of_states.peekLast().getCurr_coord());
            System.out.println("=============");
            System.out.println(listOfMoves);
            System.out.println("=============");
            move_instructions.addAll(listOfMoves);
            curr_state = list_of_states.pollLast();
        }

        return move_instructions;
    }

    public LinkedList<Character> moveDecision(Coordinate curr_coord, Coordinate next_coord) {

//    	System.out.println("-------------");
//    	System.out.println(direction);
//    	System.out.println("-------------");
    	LinkedList<Character> list_moves = new LinkedList<>();
        int newDireciont = 0;


        if (curr_coord.getX() - next_coord.getX() < 0) {
            System.out.println("E");
            newDireciont = EAST;
        } else if (curr_coord.getY() - next_coord.getY() < 0) {
            System.out.println("N");
            System.out.println(curr_coord.getX()+" "+curr_coord.getY());
            System.out.println(next_coord.getX()+" "+next_coord.getY());
            newDireciont = NORTH;
        } else if (curr_coord.getX() - next_coord.getX() > 0) {
            System.out.println("W");
            newDireciont = WEST;
        } else if (curr_coord.getY() - next_coord.getY() > 0) {
            System.out.println("S");
            newDireciont = SOUTH;
        }

        System.out.println("+++++++++++++++");
        System.out.println("Curr "+direction+" New "+newDireciont);
        System.out.println("+++++++++++++++");
        int newDirection = newDireciont;
        if (tempDirection - newDireciont < 0) {
            if (tempDirection - newDireciont == -3) {
                list_moves.add('r');
            } else {
                while (tempDirection - newDireciont != 0) {
                    newDireciont--;
                    list_moves.add('l');
                }
            }
        } else if (tempDirection - newDireciont > 0) {
            if (tempDirection - newDireciont == 3) {
              list_moves.add('l');
            } else {
                while (tempDirection - newDireciont != 0) {
                    newDireciont++;
                    list_moves.add('r');
                }
            }
        }


        if (map.get(next_coord) == '-' && backpack.get("Key") == 1) {
            list_moves.add('u');
            map.put(next_coord, ' ');
        } else if (map.get(next_coord) == 'T' && backpack.get("Axe") == 1) {
            list_moves.add('c');
            backpack.put("Raft", 1);
            map.put(next_coord, ' ');
        } else if (map.get(next_coord) == '~' && backpack.get("Stones") > 0) {
        	// System.exit(0);
        	num_stones--;
            backpack.put("Stones", num_stones);
            System.out.println("Stones:....................." + backpack.get("Stones"));
            map.put(next_coord, 'O');
        } else if (map.get(curr_coord) == '~' && map.get(next_coord) == ' ' && backpack.get("Stones") == 0) {
        	backpack.put("Raft", 0);
        }
        list_moves.add('f');
        tempDirection = newDirection;
        return list_moves;
    }


    public void expandMap(char[][] view) {

    	char front_view = view[1][2];
    	char left_view = view[2][1];

    	char next_step = '%';

    	if (checkObstacle(left_view)) {
    	    goAround = true;
        }

        if (checkObstacle(front_view)) {
    	    next_step = 'r';
        } else {
    	    next_step = 'f';
        }

        if (goAround && !checkObstacle(left_view) && currMove != 'l') {
    	    next_step = 'l';
        }

        if (2 < explored.get(curr_location) && explored.get(curr_location) < 5) {
            // System.exit(0);
    	    goAround = false;
            if (backpack.get("Raft") == 1) {
            	System.out.println("expand_water:true");
                expand_water = true;
            }
        }
        expand_map_steps.add(next_step);
    }


    private boolean checkObstacle(char cell) {
        if (cell == 'T' || cell == '-' || cell == '*' || cell == '~' || cell == '.') {
            if (cell == '~' && expand_water == true) return false;
            return true;
        }
        return false;
    }


    public char get_action( char view[][] ) {

        updateMap(view);

        // Find the path to treasure
        if (found && !takeTreasure){ // && !goingToTreasure) {
        	System.out.println("On the way to Treasure");
            // int initialH = abs(curr_location.getX() - TreasureCoord.getX()) + abs(curr_location.getY() - TreasureCoord.getY());
            State curr_state = new State(curr_location, 0, 0, backpack.get("Stones"), backpack.get("Raft"), null);
            toTreasure = findPath.aStarSearch(curr_state, TreasureCoord, map, backpack, expand_water);
            
            if (!toTreasure.isEmpty()) moves_to_treasure = stateToMove(toTreasure);
            if (!moves_to_treasure.isEmpty()) takeTreasure = true; 
        }

        // Find the path to come back
        if (found && takeTreasure){ //&& !comingBack) {
        	System.out.println("Back to starting point from treasure");
            // int initialH = abs(curr_location.getX() - TreasureCoord.getX()) + abs(curr_location.getY() - TreasureCoord.getY());
            State curr_state = new State(TreasureCoord, 0, 0, backpack.get("Stones"), backpack.get("Raft"), null);
            Coordinate start_point = new Coordinate(0,0);
            toStart = findPath.aStarSearch(curr_state, start_point, map, backpack, expand_water);
            if (!toStart.isEmpty()) moves_back_start = stateToMove(toStart);
            if (!moves_back_start.isEmpty()) {
            	backHome = true;
            } else {
            	takeTreasure = false;
            }
        } 

        if (takeTreasure && backHome) {

        	if(!moves_to_treasure.isEmpty()){
        		currMove = moves_to_treasure.poll();
        	} else {
        		currMove = moves_back_start.poll();
        	}
        	return currMove;
        }

        // make moves to come back
        // if (!moves_back_start.isEmpty()) {
        // 	comingBack = true;
        // 	currMove = moves_back_start.poll();
        //     return currMove;
        // }
        if(time==300) System.exit(0);
        time++;





        System.out.println(ItemToTake);
        for (Coordinate t: ItemToTake) {
        	System.out.print("t:" + t.getX() + " " + t.getY() + "   ");
        	System.out.println("map_coor:" + map.get(t));
        }
        

        // Find the path to item, create goingToItem variable to make sure only find one item
        if (!ItemToTake.isEmpty() && !goingToItem && moves_to_item.isEmpty()) {
            Coordinate curr_Item = ItemToTake.peek();
            System.out.println("polledItem:" + curr_Item.getX() + " " + curr_Item.getY());
            System.out.println("curr_Item:" + curr_Item.getX()+" "+curr_Item.getY());
            int initialH = abs(curr_location.getX() - curr_Item.getX()) + abs(curr_location.getY() - curr_Item.getY());
            State curr_state = new State(curr_location, 0, initialH, backpack.get("Stones"), backpack.get("Raft"), null);
            toItem = findPath.aStarSearch(curr_state, curr_Item, map, backpack, expand_water);
            if (!toItem.isEmpty()) moves_to_item = stateToMove(toItem);
        }

        // make moves to item
        if (!moves_to_item.isEmpty()) {
            System.out.println("moves in item:" + moves_to_item);
            
            goingToItem = true;
            currMove = moves_to_item.poll();
            // TODO: lastmove
            if (currMove == 'c') {

                lastMove = currMove;
            }
            

            return currMove;
        }





  //       for (Coordinate t: TreeToCut){
  //       	System.out.println(t.getX()+" "+t.getY());	
  //       }
		
		// if (goingToTree == false) {
		// 	System.out.println("false");
		// } else {
		// 	System.out.println("true");
		// }

        // TODO: Cut tree
        if (!TreeToCut.isEmpty() && !goingToTree && moves_to_tree.isEmpty()) {
        	
            Coordinate curr_tree = TreeToCut.poll();
            int initialH = abs(curr_location.getX() - curr_tree.getX()) + abs(curr_location.getY() - curr_tree.getY());
            System.out.println("curr_tree:" + curr_tree.getX()+" "+curr_tree.getY());

            State curr_state = new State(curr_location, 0, initialH, backpack.get("Stones"), backpack.get("Raft"), null);
            toTree = findPath.aStarSearch(curr_state, curr_tree, map, backpack, expand_water);
            if (!toTree.isEmpty()) moves_to_tree = stateToMove(toTree);
        }

        if (!moves_to_tree.isEmpty()) {
        	if (lastMove == 'c') {
				goingToTree = false;
        	} else {
        		goingToTree = true;
        		lastMove = moves_to_tree.peek();
        	}
            
            currMove = moves_to_tree.poll();
            //System.out.println("currMove2:" + currMove);
            return currMove;
        } else {
            lastMove = currMove;
        }
        



        System.out.println("exit");
        if(toTree.isEmpty()) System.out.println("empty");


        // TODO: keep expanding map
        expandMap(view);
        currMove = expand_map_steps.poll();

        return currMove;
    }


    void print_view( char view[][] ) {

        int i,j;

        System.out.println("\n+-----+");
        for( i=0; i < 5; i++ ) {
          System.out.print("|");
          for( j=0; j < 5; j++ ) {
            if(( i == 2 )&&( j == 2 )) {
              System.out.print('^');
            }
            else {
              System.out.print( view[i][j] );
            }
          }
          System.out.println("|");
        }
        System.out.println("+-----+");
    }

    public static void main( String[] args ) {

            InputStream in  = null;
            OutputStream out= null;
            Socket socket   = null;
            Agent  agent    = new Agent();
            char   view[][] = new char[5][5];
            char   action   = 'F';
            int port;
            int ch;
            int i,j;

            if( args.length < 2 ) {
              System.out.println("Usage: java Agent -p <port>\n");
              System.exit(-1);
            }

            port = Integer.parseInt( args[1] );

            try { // open socket to Game Engine
              socket = new Socket( "localhost", port );
              in  = socket.getInputStream();
              out = socket.getOutputStream();
            }
            catch( IOException e ) {
              System.out.println("Could not bind to port: "+port);
              System.exit(-1);
            }

            try { // scan 5-by-5 wintow around current location
              while( true ) {
                for( i=0; i < 5; i++ ) {
                  for( j=0; j < 5; j++ ) {
                    if( !(( i == 2 )&&( j == 2 ))) {
                      ch = in.read();
                      if( ch == -1 ) {
                        System.exit(-1);
                      }
                      view[i][j] = (char) ch;
                    }
                  }
                }
                agent.print_view( view ); // COMMENT THIS OUT BEFORE SUBMISSION
                action = agent.get_action( view );
                System.out.println(action);
                out.write( action );
                System.out.println("Stones:" + backpack.get("Stones"));
                System.out.println("Raft:" + backpack.get("Raft"));
                System.out.println("explored:" + explored.get(curr_location));
              }
            }
            catch( IOException e ) {
              System.out.println("Lost connection to port: "+ port );
              System.exit(-1);
            }
            finally {
              try {
                socket.close();
              }
              catch( IOException e ) {}
            }
    }
}
