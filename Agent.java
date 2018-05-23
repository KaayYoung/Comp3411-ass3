/*********************************************
 *  Agent.java 
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411/9414/9814 Artificial Intelligence
 *  UNSW Session 1, 2018
*/

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
    private HashMap<String, Integer> backpack;
    private LinkedList<Coordinate> ItemToTake;
    private LinkedList<Coordinate> TreeToCut;
    private LinkedList<State> toTreasure;
    private LinkedList<Character> moves_to_treasure;
    private LinkedList<State> toItem;
    private LinkedList<Character> moves_to_item;
    private LinkedList<State> toStart;
    private LinkedList<Character> moves_back_start;

    private Coordinate TreasureCoord;
    private Coordinate curr_location;
    private int direction;

    private int time = 0;

    private char currMove;

    private int num_stones;
	private boolean comingBack;
	private boolean goingToTreasure;
	private boolean goingToItem;

    Astar findPath;
	
    public Agent () {
        map = new HashMap<>();
        backpack = new HashMap<>();
        ItemToTake = new LinkedList<>();
        TreeToCut = new LinkedList<>();
        toTreasure = new LinkedList<>();
        moves_to_treasure = new LinkedList<>();
        toItem = new LinkedList<>();
        moves_to_item = new LinkedList<>();
        toStart = new LinkedList<>();
        moves_back_start = new LinkedList<>();
        TreasureCoord = new Coordinate(INITIALCOORD, INITIALCOORD);
        curr_location = new Coordinate(0, 0);
        direction = NORTH;
        currMove = '%';
        num_stones = 0;
        goingToTreasure = false;
        goingToItem = false;
        comingBack = false;
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
        }

        if (map.get(curr_location) == '$') {
            TreasureCoord.setX(INITIALCOORD);
            TreasureCoord.setY(INITIALCOORD);
            map.put(curr_location, ' ');
            backpack.put("Treasure", 1);
        } else if (map.get(curr_location) == 'k') {
            map.put(curr_location, ' ');
            goingToItem = false;
            backpack.put("Key", 1);
        } else if (map.get(curr_location) == 'a') {
            map.put(curr_location, ' ');
            goingToItem = false;
            backpack.put("Axe", 1);
        } else if (map.get(curr_location) == 'o') {
            num_stones++;
            map.put(curr_location, ' ');
            goingToItem = false;
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
                } else {
                    map.put(coord, view[row][col]);
                    if (view[row][col] == 'k' || view[row][col] == 'a' || view[row][col] == 'o' ) {
                        ItemToTake.add(coord);
                    }

                    if (view[row][col] == '$') {
                        TreasureCoord = coord;
                    }

                    if (view[row][col] == 'T' && backpack.get("Axe") == 1) {
                        TreeToCut.add(coord);
                    }
                }

                y--;
            }
            x++;
        }
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
            ItemToTake.add(coord);
        }
        if (item == '$') {
            TreasureCoord = coord;
        }
        if (item == 'T') {
            TreeToCut.add(coord);
        }
    }

    public void MoveForward(char view[][]) {
    	
        if (direction == EAST) {
            curr_location.setX(curr_location.getX() + 1);
            int expandPart = 0;

            // We can see one more column when move EAST, get the coordinate for
            // that column from the first row to the fifth row
            for (int row = 2; row >= -2; row--) {
                Coordinate expandRow = new Coordinate(curr_location.getX() + 2, curr_location.getY() + row);
                char expandChar = view[0][expandPart];
                map.put(expandRow, expandChar);
                isItemInView(expandChar, expandRow);
                expandPart++;
            }
        } else if (direction == NORTH) {
            curr_location.setY(curr_location.getY() + 1);
            int expandPart = 0;
            for (int col = 2; col >= -2; col--) {
                Coordinate expandCol = new Coordinate(curr_location.getX() + col, curr_location.getY() + 2);
                char expandChar = view[0][expandPart];
                map.put(expandCol, expandChar);
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
                isItemInView(expandChar, expandRow);
                expandPart++;
            }
        } else if (direction == SOUTH) {
            curr_location.setY(curr_location.getY() - 1);
            int expandPart = 0;
            for (int col = -2; col <= 2; col ++) {
                Coordinate expandCol = new Coordinate(curr_location.getX() + col, curr_location.getY() - 2);
                char expandChar = view[0][expandPart];
                map.put(expandCol, expandChar);
                isItemInView(expandChar, expandCol);
                expandPart++;
            }
        }
    }


    public LinkedList<Character> stateToMove(LinkedList<State> list_of_states) {

        LinkedList<Character> move_instructions = new LinkedList<>();
        LinkedList<Character> listOfMoves;

        State curr_state = list_of_states.pollLast();

        while (!list_of_states.isEmpty()) {
            listOfMoves = moveDecision(curr_state.getCurr_coord(), list_of_states.peekLast().getCurr_coord());
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
            newDireciont = EAST;
        } else if (curr_coord.getY() - next_coord.getY() < 0) {
            newDireciont = NORTH;
        } else if (curr_coord.getX() - next_coord.getX() > 0) {
            newDireciont = WEST;
        } else if (curr_coord.getY() - next_coord.getY() > 0) {
            newDireciont = SOUTH;
        }
//        System.out.println("+++++++++++++++");
//        System.out.println(newDireciont);
//        System.out.println("+++++++++++++++");
        int newDirection = newDireciont;
        if (direction - newDireciont < 0) {
            if (direction - newDireciont == -3) {
                list_moves.add('r');
            } else {
                while (direction - newDireciont != 0) {
                    newDireciont--;
                    list_moves.add('l');
                }
            }
        } else if (direction - newDireciont > 0) {
            if (direction - newDireciont == 3) {
              list_moves.add('l');
            } else {
                while (direction - newDireciont != 0) {
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
            backpack.put("Stones", num_stones - 1);
            map.put(next_coord, 'O');
        }
        list_moves.add('f');
        direction = newDirection;
        return list_moves;
    }


    public void expandMap(char[][] view) {

    }

    public char get_action( char view[][] ) {

        updateMap(view);

        // Find the path to treasure
        if (TreasureCoord.getX() != 200 && moves_to_treasure.isEmpty() && !goingToTreasure) {
            int initialH = abs(curr_location.getX() - TreasureCoord.getX()) + abs(curr_location.getY() - TreasureCoord.getY());
            State curr_state = new State(curr_location, 0, initialH, backpack.get("Stones"), backpack.get("Raft"), null);
            toTreasure = findPath.aStarSearch(curr_state, TreasureCoord, map, backpack);
            
            if (!toTreasure.isEmpty()) moves_to_treasure = stateToMove(toTreasure);
        }

        // make moves to treasure
        if (!moves_to_treasure.isEmpty()) {
        	goingToTreasure = true;
        	currMove = moves_to_treasure.poll();
            return currMove;
        }
        
        // Find the path to come back
        if (backpack.get("Treasure") == 1 && !comingBack) {
            int initialH = abs(curr_location.getX() - TreasureCoord.getX()) + abs(curr_location.getY() - TreasureCoord.getY());
            State curr_state = new State(curr_location, 0, initialH, backpack.get("Stones"), backpack.get("Raft"), null);
            Coordinate start_point = new Coordinate(0,0);
            if(start_point != null) toStart = findPath.aStarSearch(curr_state, start_point, map, backpack);
            if (!toStart.isEmpty()) moves_back_start = stateToMove(toStart);
        }

        // make moves to come back
        if (!moves_back_start.isEmpty()) {
        	comingBack = true;
        	currMove = moves_back_start.poll();
            return currMove;
        }
        if(time==10) System.exit(0);
        time++;

        // Find the path to item, create goingToItem variable to make sure only find one item
        if (!ItemToTake.isEmpty() && !goingToItem) {
            Coordinate curr_Item = ItemToTake.poll();
            int initialH = abs(curr_location.getX() - curr_Item.getX()) + abs(curr_location.getY() - curr_Item.getY());
            State curr_state = new State(curr_location, 0, initialH, backpack.get("Stones"), backpack.get("Raft"), null);
            toItem = findPath.aStarSearch(curr_state, curr_Item, map, backpack);
            if (!toItem.isEmpty()) moves_to_item = stateToMove(toItem);
        }

        // make moves to item
        if (!moves_to_item.isEmpty()) {
            goingToItem = true;
            currMove = moves_to_item.poll();
            return currMove;
        }
        // TODO: keep expanding map
        expandMap(view);

        return 0;
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
                out.write( action );
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
