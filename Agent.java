/*********************************************
 *  Agent.java 
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411/9414/9814 Artificial Intelligence
 *  UNSW Session 1, 2018
*/

import java.util.*;
import java.io.*;
import java.net.*;

public class Agent {

    final static int EAST = 0;
    final static int NORTH = 1;
    final static int WEST = 2;
    final static int SOUTH = 3;

    final static int INITIALCOORD = 200;

    private HashMap<Coordinate, Character> map;
    private HashMap<String, Integer> backpack;
    private ArrayList<Coordinate> ItemToTake;
    private ArrayList<Coordinate> TreeToCut;

    private Coordinate TreasureCoord;
    private Coordinate curr_location;
    private int direction;

    private char currMove;

//    private boolean isHave_axe;
//    private boolean isHave_key;
//    private boolean isHave_raft;
//    private boolean isHave_treasure;

    private int num_stones;



    public Agent () {
        map = new HashMap<>();
        backpack = new HashMap<>();
        ItemToTake = new ArrayList<>();
        TreeToCut = new ArrayList<>();
        TreasureCoord = new Coordinate(INITIALCOORD, INITIALCOORD);
        curr_location = new Coordinate(0, 0);
        direction = -1;
        currMove = '%';
//        isHave_axe = false;
//        isHave_key = false;
//        isHave_raft = false;
//        isHave_treasure = false;
        num_stones = 0;
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
            //isHave_treasure = true;
            backpack.put("Treasure", 1);
        } else if (map.get(curr_location) == 'k') {
            //isHave_key = true;
            map.put(curr_location, ' ');
            backpack.put("Key", 1);
        } else if (map.get(curr_location) == 'a') {
            //isHave_axe = true;
            map.put(curr_location, ' ');
            backpack.put("Axe", 1);
        } else if (map.get(curr_location) == 'o') {
            num_stones++;
            map.put(curr_location, ' ');
            backpack.put("Stones", num_stones);
        }

    }

    public void initializeMap (char view[][]) {
        int x = -2;
        for (int col = 0; col < 5; col++) {
            int y = 2;
            for (int row = 0; row < 5; row++) {
                Coordinate coord = new Coordinate(x, y);
                if (x == 0 && y ==0) {
                    // initialize the starting point as #
                    direction = 0;
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

    private void clockwise() {
        direction--;
        direction = direction % 4;
        if (direction < 0) direction = direction + 4;
    }

    private void anticlockwise() {
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

    private void MoveForward(char view[][]) {

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

    public void getTreasure() {

    }

    public char get_action( char view[][] ) {

        // REPLACE THIS CODE WITH AI TO CHOOSE ACTION

    //    int ch=0;
    //
    //    System.out.print("Enter Action(s): ");
    //
    //    try {
    //      while ( ch != -1 ) {
    //        // read character from keyboard
    //        ch  = System.in.read();
    //
    //        switch( ch ) { // if character is a valid action, return it
    //         case 'F': case 'L': case 'R': case 'C': case 'U':
    //         case 'f': case 'l': case 'r': case 'c': case 'u':
    //           return((char) ch );
    //        }
    //      }
    //    }
    //    catch (IOException e) {
    //      System.out.println ("IO error:" + e );
    //    }
    //
        updateMap(view);

//        if () {
//            getTreasure();
//        }

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
