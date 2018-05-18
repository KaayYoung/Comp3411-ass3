/*********************************************
 *  Agent.java 
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411/9414/9814 Artificial Intelligence
 *  UNSW Session 1, 2018
*/

import com.sun.xml.internal.bind.v2.runtime.Coordinator;

import java.util.*;
import java.io.*;
import java.net.*;

public class Agent {

    final static int EAST = 0;
    final static int NORTH = 1;
    final static int WEST = 2;
    final static int SOUTH = 3;

    private HashMap<Coordinate, Character> map;

    private ArrayList<Coordinate> ItemToTake;
    private ArrayList<Coordinate> TreeToCut;

    private char currMove;

    private int currX;
    private int currY;
    private Coordinate curr_location;
    private int direction;

    private boolean isHave_axe;
    private boolean isHave_key;
    private boolean isHave_raft;

    private int num_stones;

    public Agent () {
        map = new HashMap<Coordinate, Character>();
        ItemToTake = new ArrayList<Coordinate>();
        currMove = '%';
        curr_location = new Coordinate(0, 0);
        direction = -1;
        isHave_axe = false;
        isHave_key = false;
        isHave_raft = false;
        num_stones = 0;
    }

    public void updataMap(char view[][]) {

        if (currMove == '%') {

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

                        if (view[row][col] == 'k' || view[row][col] == 'a' || view[row][col] == 's' || view[row][col] == '$') {
                            ItemToTake.add(coord);
                        }

                        if (view[row][col] == 'T' && !isHave_axe) {
                            TreeToCut.add(coord);
                        }
                    }

                    y--;
                }
                x++;
            }
        } else if (currMove == 'l') {
            anticlockwise();
        } else if (currMove == 'r') {
            clockwise();
        } else if (currMove == 'f') {
            MoveForward();
        }

        if (map.get(curr_location) == '$') {

        } else if (map.get(curr_location) == 'k') {

        } else if (map.get(curr_location) == 'a') {

        } else if (map.get(curr_location) == 'o') {

        }

    }

    private void MoveForward() {

        if (direction == EAST) {

        } else if (direction == NORTH) {

        } else if (direction == WEST) {

        } else if (direction == SOUTH) {

        }

    }

    private void anticlockwise() {

    }

    private void clockwise() {
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
        

        return 0;
    }

  void print_view( char view[][] )
  {
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

  public static void main( String[] args )
  {
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
