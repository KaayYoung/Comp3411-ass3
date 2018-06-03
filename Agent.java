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
    
    private LinkedList<Coordinate> CannotGet;
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
    private LinkedList<Coordinate> cornerList;
    private LinkedList<Coordinate> cornerExpand;
    private LinkedList<State> todropwater;
    private LinkedList<Character> moves_to_water;
    private LinkedList<State> todoor;
    private LinkedList<Character> moves_to_door;

    private Coordinate door;
    private Coordinate last_location;
    private Coordinate dropwater;
    private Coordinate TreasureCoord;
    private static Coordinate curr_location;
    private int direction;
    private int tempDirection;

    private int time = 0;
    private int index = 0;
    private int times = 0;
    private int used_stone = 0;

    private char lastMove;
    private char currMove;

    private int num_stones;
	private boolean takeTreasure;
	private boolean backHome;
	private boolean goingToItem;
	private boolean goingToTree;
    private boolean suc;
    private boolean onWater;
    private boolean standingOnWater;
    private boolean canbetrue;
	private boolean goAround;
	private boolean used_raft = false;
	private boolean went_to_water = false;

    Astar findPath;
	private LinkedList<Character> moves_to_corner;
	
    public Agent () {
        map = new HashMap<>();
        backpack = new HashMap<>();
        explored = new HashMap<>();

        CannotGet = new LinkedList<>();
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
        cornerList = new LinkedList<>();
        moves_to_corner = new LinkedList<>();
        cornerExpand = new LinkedList<>();
        todropwater = new LinkedList<>();
        moves_to_water = new LinkedList<>();
        todoor = new LinkedList<>();
        moves_to_door = new LinkedList<>();

        door = new Coordinate(INITIALCOORD, INITIALCOORD);
        last_location = new Coordinate(0, 0);
        dropwater = new Coordinate(INITIALCOORD, INITIALCOORD);
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
        suc = false;
        goAround = false;
        
        onWater = false;
        standingOnWater = false;
        canbetrue = false;
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
        
        if(map.get(curr_location) == null) System.out.println("1");
        if(map.get(last_location) == null) System.out.println("2");
        System.out.println("coord:"+curr_location.getX()+" "+curr_location.getY());
        if(map.get(new Coordinate(-3, 13)) == null) System.out.println("4");
        if(backpack.get("Stones") == null) System.out.println("3");
        if(map.get(curr_location) != '~' && map.get(last_location) == '~' && backpack.get("Stones") == 0) {
        	backpack.put("Raft", 0);
        	standingOnWater = false;
        }
 
        if (map.get(curr_location) == '$') {
            TreasureCoord.setX(INITIALCOORD);
            TreasureCoord.setY(INITIALCOORD);
            //found = true;
//            takeTreasure = true;
            map.put(new Coordinate(curr_location.getX(), curr_location.getY()), ' ');
            System.out.println("cur111111111:"+curr_location.getX()+" "+curr_location.getY());
            backpack.put("Treasure", 1);
        } else if (map.get(curr_location) == 'k') {
            map.put(new Coordinate(curr_location.getX(), curr_location.getY()), ' ');
            goingToItem = false;
            ItemToTake.remove(curr_location);
            backpack.put("Key", 1);
        } else if (map.get(curr_location) == 'a') {
            map.put(new Coordinate(curr_location.getX(), curr_location.getY()), ' ');
            goingToItem = false;
            ItemToTake.remove(curr_location);
            backpack.put("Axe", 1);
        } else if (map.get(curr_location) == 'o') {
            map.put(new Coordinate(curr_location.getX(), curr_location.getY()), ' ');
            goingToItem = false;
            ItemToTake.remove(curr_location);
            backpack.put("Stones", backpack.get("Stones") + 1);
            System.out.println("Stones added:"+backpack.get("Stones"));
        } else if (map.get(curr_location) != '~' && map.get(last_location) == '~' && backpack.get("Stones") == 0) {
        	System.out.println("decrement here");
            backpack.put("Raft", 0);
            standingOnWater = false;
        } else if (map.get(curr_location) == '~' && backpack.get("Stones") == 0 && backpack.get("Raft") == 1) {
            standingOnWater = true;
        } else if (map.get(curr_location) == 'T') {
        	backpack.put("Raft", 1);
        	map.put(new Coordinate(curr_location.getX(), curr_location.getY()), ' ');
        }
        
        
        
    }


    public void initializeMap (char view[][]) {
        backpack.put("Raft",0);
        backpack.put("Key",0);
        backpack.put("Stones",0);
        backpack.put("Treasure",0);
        backpack.put("Axe",0);
        cornerList.add(new Coordinate(0,0));

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
                    if (view[row][col] == '-') {
                        door.setX(coord.getX());
                        door.setY(coord.getY());
                    }
                }

                y--;
            }
            x++;
        }
        System.out.println("ininininininini");
    }

    public void clockwise() {
    	System.out.println("direction-- : "+direction);
        direction--;
        direction = direction % 4;
        if (direction < 0) direction = direction + 4;
    }

    public void anticlockwise() {
    	System.out.println("direction++ : "+direction);
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
        if (item == '-') {
            door.setX(coord.getX());
            door.setY(coord.getY());
        }
    }

    public void MoveForward(char view[][]) {

        last_location.setX(curr_location.getX());
        last_location.setY(curr_location.getY());
        if(explored.get(curr_location) == null) {
        	explored.put(curr_location, 0);
        } else {
        	explored.put(curr_location, explored.get(curr_location) +  1);
        }
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

            int counter = 0;
            for (int i = 0; i < 5; i++) {
                if (view[1][i] == '~') {
                    counter++;
                }
            } 
            if (counter == 5) {
                dropwater.setX(curr_location.getX() + 1);
                dropwater.setY(curr_location.getY());
            }

            counter = 0;
            for (int i = 0; i < 5; i++) {
                if (view[i][3] == '~') {
                    counter++;
                }
            } 
            if (counter == 5) {
                dropwater.setX(curr_location.getX());
                dropwater.setY(curr_location.getY() - 1);
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
            int counter = 0;
            for (int i = 0; i < 5; i++) {
                if (view[1][i] == '~') {
                    counter++;
                }
            } 
            if (counter == 5) {
                dropwater.setX(curr_location.getX());
                dropwater.setY(curr_location.getY() + 1);
            }

            counter = 0;
            for (int i = 0; i < 5; i++) {
                if (view[i][3] == '~') {
                    counter++;
                }
            } 
            if (counter == 5) {
                dropwater.setX(curr_location.getX() + 1);
                dropwater.setY(curr_location.getY());
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
            int counter = 0;
            for (int i = 0; i < 5; i++) {
                if (view[1][i] == '~') {
                    counter++;
                }
            } 
            if (counter == 5) {
                dropwater.setX(curr_location.getX() - 1);
                dropwater.setY(curr_location.getY());
            }

            counter = 0;
            for (int i = 0; i < 5; i++) {
                if (view[i][3] == '~') {
                    counter++;
                }
            } 
            if (counter == 5) {
                dropwater.setX(curr_location.getX());
                dropwater.setY(curr_location.getY() + 1);
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
            int counter = 0;
            for (int i = 0; i < 5; i++) {
                if (view[1][i] == '~') {
                    counter++;
                }
            } 
            if (counter == 5) {
                dropwater.setX(curr_location.getX());
                dropwater.setY(curr_location.getY() - 1);
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



        if (curr_location.equals(dropwater) && !canbetrue) {
            canbetrue = true;
            onWater = true;
            cornerList.clear();
            System.out.println("first clear");
            cornerList.add(dropwater);
        } 

        if (map.get(curr_location) == '~' && backpack.get("Stones") > 0){
            backpack.put("Stones", backpack.get("Stones") - 1);
            // System.out.println("Stones:....................." + backpack.get("Stones"));
            map.put(new Coordinate(curr_location.getX(), curr_location.getY()), 'O');
        }
        
        

    }


    public LinkedList<Character> stateToMove(LinkedList<State> list_of_states) {

        LinkedList<Character> move_instructions = new LinkedList<>();
        LinkedList<Character> listOfMoves;


        State curr_state = list_of_states.pollLast();
        // System.out.println("before:"+tempDirection);
        tempDirection = direction;
        // System.out.println("after:"+tempDirection);
        
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
        System.out.println("temp:"+tempDirection);
        System.out.println("nD:"+newDireciont);
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

        System.out.println("next_coord:" + next_coord.getX());
        if (map.get(next_coord) == '-' && backpack.get("Key") == 1) {
            list_moves.add('u');
            door.setX(INITIALCOORD);
            door.setY(INITIALCOORD);
            map.put(new Coordinate(next_coord.getX(), next_coord.getY()), ' ');
        } else if (map.get(next_coord) == 'T' && backpack.get("Axe") == 1) {
            list_moves.add('c');
        } 
        // else if (map.get(next_coord) == '~' && backpack.get("Stones") > 0) {
        // 	 //System.exit(0);
        	
        //     backpack.put("Stones", backpack.get("Stones") - 1);
        //     System.out.println("Stones:....................." + backpack.get("Stones"));
        //     map.put(next_coord, 'O');
        // } 
        list_moves.add('f');
        tempDirection = newDirection;
        return list_moves;
    }

    
    

    // public void expandMap(char[][] view) {

    // 	char front_view = view[1][2];
    // 	char left_view = view[2][1];

    // 	char next_step = '%';

    // 	if (checkObstacle(left_view)) {
    // 	    goAround = true;
    //     }

    //     if (checkObstacle(front_view)) {
    // 	    next_step = 'r';
    //     } else {
    // 	    next_step = 'f';
    //     }

    //     if (goAround && !checkObstacle(left_view) && currMove != 'l') {
    // 	    next_step = 'l';
    //     }

    //     if (2 < explored.get(curr_location) && explored.get(curr_location) < 5) {
    //         // System.exit(0);
    // 	    goAround = false;
    //         if (backpack.get("Raft") == 1) {
    //         	System.out.println("expand_water:true");
    //             expand_water = true;
    //         }
    //     }
    //     expand_map_steps.add(next_step);
    // }


    private boolean checkObstacle(char cell, boolean onWater) {
        if (onWater) {
            if (cell != '~') return true;
        } else {
            if (cell == 'T' || cell == '-' || cell == '*' || cell == '~' || cell == '.') return true;
        }
        return false;
    }


    public char get_action(char view[][]) {

        updateMap(view);

        if (!ItemToTake.isEmpty() && !goingToItem && moves_to_item.isEmpty() && !takeTreasure && !onWater) {

        	for (int i = 0; i < ItemToTake.size(); i++) {
        		Coordinate curr_Item = ItemToTake.get(i);
        		System.out.println("polledItem with no risk:" + curr_Item.getX() + " " + curr_Item.getY());
        		System.out.println("curr coord:"+curr_location.getX()+" "+curr_location.getY());
            	System.out.println("curr_Item:" + curr_Item.getX()+" "+curr_Item.getY());
            	System.out.println("item raft:"+backpack.get("Raft"));
            	int initialH = abs(curr_location.getX() - curr_Item.getX()) + abs(curr_location.getY() - curr_Item.getY());
            	State curr_state = new State(curr_location, 0, initialH, 0, 0, null);
            	toItem = findPath.aStarSearch(curr_state, curr_Item, map, backpack, onWater);
                if(!toItem.isEmpty()) break;
        	}
            if (!toItem.isEmpty()) {
            	moves_to_item = stateToMove(toItem);
            }
        }

        // make moves to item
        if (!moves_to_item.isEmpty()) {
        	moves_to_corner.clear();
        	System.out.println("second clear");
            System.out.println("moves in item:" + moves_to_item);
            
            goingToItem = true;
            currMove = moves_to_item.poll();
            if (currMove == 'c') {
                lastMove = currMove;
            }
            return currMove;
        }
        
        
//        if (door.getX() != INITIALCOORD && moves_to_door.isEmpty()) {
//            State curr_state = new State(curr_location, 0, 0, backpack.get("Stones"), backpack.get("Raft"), null);
//            todoor = findPath.aStarSearch(curr_state, door, map, backpack, onWater);
//            if (!todoor.isEmpty()) moves_to_door = stateToMove(todoor);
//        }
//        if (!moves_to_door.isEmpty()) {
//            currMove = moves_to_door.poll();
//
//            return currMove;
//        }

        System.out.println("Treasure:" + TreasureCoord.getX() );
        if (takeTreasure) {
            System.out.println("takeTreasure ==== true");
        }
        // Find the path to treasure
        if (TreasureCoord.getX() != 200 && !takeTreasure && backpack.get("Treasure") == 0){ // && !goingToTreasure) {
        	System.out.println("On the way to Treasure");
        	System.out.println("treasure or not:"+backpack.get("Treasure"));
            // int initialH = abs(curr_location.getX() - TreasureCoord.getX()) + abs(curr_location.getY() - TreasureCoord.getY());
            
            State curr_state = new State(curr_location, 0, 0, backpack.get("Stones"), -1, null);
            if (backpack.get("Raft") == 1) {
                curr_state.setHave_raft(1);
            } else {
                curr_state.setHave_raft(0);
            }
            System.out.println("curr_state_stone1: " + curr_state.getNumOfStones() + "  raft: " + curr_state.isHave_raft());
            toTreasure = findPath.aStarSearch(curr_state, TreasureCoord, map, backpack, onWater);
            System.out.println("curr_state_stone2: " + curr_state.getNumOfStones() + "  raft: " + curr_state.isHave_raft());
            //if(backpack.get("Stones") == 3) System.exit(0);
            for(int i = 0; i < toTreasure.size(); i++) {
            	if(map.get(toTreasure.get(i).getCurr_coord()) == '~') {
            		went_to_water = true;
            	}
            	if(map.get(toTreasure.get(i).getCurr_coord()) == '~' && backpack.get("Stones") != 0) {
            		used_stone++;
            		backpack.put("Stones", backpack.get("Stones")-1);
            		System.out.println("stone used in search"+used_stone);
//            		System.exit(0);
            	}
            }
            if (!toTreasure.isEmpty()) moves_to_treasure = stateToMove(toTreasure);
            
            if (!moves_to_treasure.isEmpty()) {
//            	if (map.get(curr_location) != '~') {
//            		standingOnWater = false;
//            	}
            	if(went_to_water && used_stone == 0) {
            		used_raft = true;
            		backpack.put("Raft", 0);
            		went_to_water = false;
            	}
//                if (standingOnWater) {
//                    if(backpack.get("Stones") == 0) backpack.put("Raft", 0);
//                    System.out.println("decrement there");
//                    used_raft = true;
//                } else {
//                	System.out.println("not standing");
//                }
                takeTreasure = true; 
            } 
            
        }
        if (takeTreasure) {
        	System.out.println("size of treasure"+moves_to_treasure.size());
        	System.out.println("takeTreasure: true");

        }

        
        // Find the path to come back
        if (moves_back_start.isEmpty() && (takeTreasure || backpack.get("Treasure") == 1)){ //&& !comingBack) {
        	System.out.println("Back to starting point from treasure");
            // int initialH = abs(curr_location.getX() - TreasureCoord.getX()) + abs(curr_location.getY() - TreasureCoord.getY());
            System.out.println("ready to increase stone");
        	System.out.println("num of stone:"+backpack.get("Stones"));
            if(used_stone != 0) {
        		System.out.println("increased");
            	backpack.put("Stones", backpack.get("Stones")+used_stone);
            } else {
            	System.out.println("did not increase");
            }
            
            
            System.out.println("num of stone:"+backpack.get("Stones"));
        	State curr_state;
        	if(takeTreasure) {
        		System.out.println("in here");
            	curr_state = new State(TreasureCoord, 0, 0, backpack.get("Stones"), backpack.get("Raft"), null);
            } else {
            	System.out.println("in there");
            	curr_state = new State(curr_location, 0, 0, backpack.get("Stones"), backpack.get("Raft"), null);
            }
//        	System.out.println("curr:"+map.get(curr_location));
//        	if(map.get(curr_location) == 'O') {
//        		curr_state.setNumOfStones(backpack.get("Stones")+1);
//        	}
            Coordinate start_point = new Coordinate(0,0);
            
            System.out.println("curr_state_stone_inBack1: " + curr_state.getNumOfStones() + "  raft: " + curr_state.isHave_raft());
            toStart = findPath.aStarSearch(curr_state, start_point, map, backpack, onWater);

            System.out.println("curr_state_stone_inBack2: " + curr_state.getNumOfStones() + "  raft: " + curr_state.isHave_raft());
            for(int i = 0; i < toStart.size(); i++) {
            	System.out.println("back coord:"+toStart.get(i).getCurr_coord().getX()+" "+toStart.get(i).getCurr_coord().getY());
            }
            if (!toStart.isEmpty()) {
//                System.out.println(direction);
//                System.out.println(tempDirection);
                direction = tempDirection;
//                System.exit(0);
                moves_back_start = stateToMove(toStart);
            } else {
//            	if (standingOnWater) {
//                    backpack.put("Raft", 1);
//                    System.out.println("decrement there");
//                }
//            	backpack.put("Stones", backpack.get("Stones")+used_stone);
            	used_stone = 0;
            	
            	if(used_raft) {
            		System.out.println("increase raft");
            		backpack.put("Raft", 1);
            		used_raft = false;
            	}
            }
            if (!moves_back_start.isEmpty()) {
//            	System.exit(0);
            	backHome = true;
            } else {
//                if (standingOnWater && map.get(curr_location) != '~') {
//                	System.out.println("increment here");
//                    backpack.put("Raft", 1);
//                }
//                System.out.println("treasure false");
            	
            	takeTreasure = false;
            }
        } 

        if (takeTreasure && backHome) {
        	moves_to_corner.clear();
        	System.out.println("third clear");
        		suc = true;
        	if(!moves_to_treasure.isEmpty()){
        		System.out.println("direction:"+direction);
        		currMove = moves_to_treasure.poll();
        	} else {
        		currMove = moves_back_start.poll();
        		System.out.println("11111direction:"+direction);
//        		System.exit(0);
        	}
        	return currMove;
        }

        // make moves to come back
        // if (!moves_back_start.isEmpty()) {
        // 	comingBack = true;
        // 	currMove = moves_back_start.poll();
        //     return currMove;
        // }
        // if(time==700) System.exit(0);
        // time++;




        System.out.println(ItemToTake);
        for (Coordinate t: ItemToTake) {
        	System.out.print("t:" + t.getX() + " " + t.getY() + "   ");
        	System.out.println("map_coor:" + map.get(t));
        }
        

        // Find the path to item, create goingToItem variable to make sure only find one item
        if (!ItemToTake.isEmpty() && !goingToItem && moves_to_item.isEmpty() && !takeTreasure && !onWater) {

        	for (int i = 0; i < ItemToTake.size(); i++) {
        		Coordinate curr_Item = ItemToTake.get(i);
        		System.out.println("polledItem:" + curr_Item.getX() + " " + curr_Item.getY());
        		System.out.println("curr coord:"+curr_location.getX()+" "+curr_location.getY());
            	System.out.println("curr_Item:" + curr_Item.getX()+" "+curr_Item.getY());
            	System.out.println("item raft:"+backpack.get("Raft"));
            	int initialH = abs(curr_location.getX() - curr_Item.getX()) + abs(curr_location.getY() - curr_Item.getY());
            	State curr_state = new State(curr_location, 0, initialH, backpack.get("Stones"), backpack.get("Raft"), null);
            	toItem = findPath.aStarSearch(curr_state, curr_Item, map, backpack, onWater);
                if(!toItem.isEmpty()) break;
        	}
            if (!toItem.isEmpty()) {
            	moves_to_item = stateToMove(toItem);
            }
        }

        // make moves to item
        if (!moves_to_item.isEmpty()) {
        	moves_to_corner.clear();
        	System.out.println("four clear");
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
        if (!TreeToCut.isEmpty() && !goingToTree && moves_to_tree.isEmpty() && backpack.get("Axe") == 1 && !onWater) {
        	
            for (int i = 0; i < TreeToCut.size(); i++) {
                Coordinate curr_tree = TreeToCut.get(i);
                int initialH = abs(curr_location.getX() - curr_tree.getX()) + abs(curr_location.getY() - curr_tree.getY());
                // if (onWater) {
                //     System.out.println("ooonnnnn wattter");
                // }
                System.out.println("curr_tree:" + curr_tree.getX()+" "+curr_tree.getY());
                System.out.println("tree Raft:" + backpack.get("Raft"));
                State curr_state = new State(curr_location, 0, initialH, backpack.get("Stones"), backpack.get("Raft"), null);
                toTree = findPath.aStarSearch(curr_state, curr_tree, map, backpack, onWater);
                System.out.println("tree is not null"+toTree.size());
                if (!toTree.isEmpty()) {
                    moves_to_tree = stateToMove(toTree);
                    break;
                }
            }

        }

        if (!moves_to_tree.isEmpty()) {
        	moves_to_corner.clear();
        	System.out.println("five clear");
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
        if (!takeTreasure && !backHome) {
//        	expandMap(view);
//        	currMove = expand_map_steps.poll();
            exploreMap(view);
        	if(moves_to_corner.isEmpty()) {
        		System.out.println("Empty");
        		
        		
        		for(int i = 0; i < cornerList.size(); i++) {
        			System.out.println("Coord:"+cornerList.get(i).getX()+" "+cornerList.get(i).getY());
        		}
        	} else {
        		System.out.println("Not Empty");
        	}
        	// System.out.println("coord:"+cornerList.peek().getX()+" "+cornerList.peek().getY());
        	if (!moves_to_corner.isEmpty()) {
                currMove = moves_to_corner.poll();
                return currMove;
            }
            // System.out.println("going to explore:"+ cornerList.peek().getX()+" "+cornerList.peek().getY());
        }

        

        if (!takeTreasure && !backHome && moves_to_corner.isEmpty() && !onWater) {
            int initialH = abs(curr_location.getX() - dropwater.getX()) + abs(curr_location.getY() - dropwater.getY());
            State curr_state = new State(curr_location, 0, initialH, backpack.get("Stones"), backpack.get("Raft"), null);
            todropwater = findPath.aStarSearch(curr_state, dropwater, map, backpack, onWater);
            if (!todropwater.isEmpty()) moves_to_water = stateToMove(todropwater);
            
        }

        if (!moves_to_water.isEmpty()) {
            currMove = moves_to_water.poll();
            return currMove;
            // System.out.println("todropwater:" + dropwater.getX() + " " + dropwater.getY());
            // System.exit(0);
        }

        onWater = false;
        backpack.put("Raft", 1);
        //System.exit(0);
        return currMove = 'z';
    }
    


    public void exploreMap(char[][] view) {
    	System.out.println("cornerList: ===========" + cornerList);
//    	if(cornerList.contains(curr_location)) {
        for(int i = 0; i < 2; i++){
            boolean break_flag = false;
            for(int j = 0; j < 3; j++){
                System.out.println("paaaaaaaaaaaaaasssssssssssssssssssssss44444");
                Coordinate left_top = new Coordinate(curr_location.getX() - 2 + j, curr_location.getY() + 2 - i);
                if(!cornerExpand.contains(left_top) && !cornerList.contains(left_top) && !checkObstacle(map.get(left_top), onWater)){
                    System.out.println("watttttter!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    cornerList.add(left_top);
                    break_flag = true;
                    break;
                }
            }
            if(break_flag) break;
        }
        for(int i = 0; i < 2; i++){
            boolean break_flag = false;
            for(int j = 0; j < 3; j++){
                Coordinate right_top = new Coordinate(curr_location.getX() + 2 - i, curr_location.getY() + 2 - j);
                if (checkObstacle(map.get(right_top), onWater)) {
                    System.out.println("paaaaaaaaaaaaaasssssssssssssssssssssss11111");
                }
                if(!cornerExpand.contains(right_top) && !cornerList.contains(right_top) && !checkObstacle(map.get(right_top), onWater)){
                    System.out.println("watttttter!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    cornerList.add(right_top);
                    break_flag = true;
                    break;
                }
            }
            if(break_flag) break;
        }

        for(int i = 0; i < 2; i++){
            boolean break_flag = false;

            for(int j = 0; j < 3; j++){
                System.out.println("paaaaaaaaaaaaaasssssssssssssssssssssss3333333");
                Coordinate right_bot = new Coordinate(curr_location.getX() + 2 - j, curr_location.getY() - 2 + i);
                if(!cornerExpand.contains(right_bot) && !cornerList.contains(right_bot) && !checkObstacle(map.get(right_bot), onWater)){
                    System.out.println("watttttter!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    cornerList.add(right_bot);
                    break_flag = true;
                    break;
                }
            }
            if(break_flag) break;
        }

        for(int i = 0; i < 2; i++){
            boolean break_flag = false;
            for(int j = 0; j < 3; j++){
                Coordinate left_bot = new Coordinate(curr_location.getX() - 2 + i, curr_location.getY() - 2 + j);
                if (checkObstacle(map.get(left_bot), onWater)) {
                    System.out.println("paaaaaaaaaaaaaasssssssssssssssssssssss22222");
                }
                if(!cornerExpand.contains(left_bot) && !cornerList.contains(left_bot) && !checkObstacle(map.get(left_bot), onWater)){
                    System.out.println("watttttter!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    cornerList.add(left_bot);
                    break_flag = true;
                    break;
                }
            }
            if(break_flag) break;
        }

        // } else {
        //     System.out.println("oooooooooooooooooooooooooooooooooooooooooooo");
        // }
//    	}
    	for(int i = 0; i < cornerList.size(); i++) {
            State curr_state;
            if (onWater) {
                curr_state = new State(curr_location, 0, 0, 0, 1, null);
            } else {
                curr_state = new State(curr_location, 0, 0, 0, 0, null);
            }
    		
            LinkedList<State> toCorner = findPath.aStarSearch(curr_state, cornerList.get(i), map, backpack, onWater);
            if(!toCorner.isEmpty()) moves_to_corner = stateToMove(toCorner);
            if(!moves_to_corner.isEmpty()) {

                break;
            }
    	}
    	
    	for(int i = 0; i < cornerList.size(); i++) {
            System.out.println("corner_cord:" + cornerList.get(i).getX() + " " + cornerList.get(i).getY());
            System.out.println("sadasdgaegs");

    		if(cornerList.get(i).equals(curr_location)) {
                System.out.println("cornerList_size:" + cornerExpand.size());
                System.out.println("add into cornerList:" + cornerList.get(i).getX() + " " + cornerList.get(i).getY());
    			cornerExpand.addLast(cornerList.get(i));
                cornerList.remove(i);
                break;
    		}
    	}
    	
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
