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

	/* Four directions */
    final static int EAST = 0;
    final static int NORTH = 1;
    final static int WEST = 2;
    final static int SOUTH = 3;

    /* Initial Coordinate (Can not be reached in this map) */
    final static int INITIALCOORD = 200;

    /* Record each cell of view into map */
    private HashMap<Coordinate, Character> map = new HashMap<>(); 
    /* Record key, axe, stones, raft and treasure */
    private HashMap<String, Integer> backpack = new HashMap<>(); 
    
    /* Record the Item we see in the view */
    private LinkedList<Coordinate> ItemToTake = new LinkedList<>();
    /* Record the tree we see in the view */
    private LinkedList<Coordinate> TreeToCut = new LinkedList<>();

    /**
	  * Use a linkedlist(State) to save the path for 
	  * treasure, item, starting point, tree, and the place to drop water
	  * Use the other linkedlist(Character) to translate
      * state lists to command(Character) list
      */
    private LinkedList<State> toTreasure = new LinkedList<>();
    private LinkedList<Character> moves_to_treasure = new LinkedList<>();
    private LinkedList<State> toItem = new LinkedList<>();
    private LinkedList<Character> moves_to_item = new LinkedList<>();
    private LinkedList<State> toStart = new LinkedList<>();
    private LinkedList<Character> moves_back_start = new LinkedList<>();
    private LinkedList<State> toTree = new LinkedList<>();
    private LinkedList<Character> moves_to_tree = new LinkedList<>();
    private LinkedList<State> todropwater = new LinkedList<>();
    private LinkedList<Character> moves_to_water = new LinkedList<>();
    private LinkedList<Character> expand_map_steps = new LinkedList<>();
    private LinkedList<Coordinate> cornerList = new LinkedList<>();
    private LinkedList<Coordinate> cornerExpand = new LinkedList<>();
    private LinkedList<Character> moves_to_corner = new LinkedList<>();

    private Coordinate curr_location = new Coordinate(0, 0);
    private Coordinate last_location = new Coordinate(0, 0);

    /** 
      * Record the location it should drop into water
      * when finishing exploring land, begin to drop water 
      */
    private Coordinate dropwater = new Coordinate(INITIALCOORD, INITIALCOORD);
    /* Record the treasure coordinate */
    private Coordinate TreasureCoord = new Coordinate(INITIALCOORD, INITIALCOORD);
    
    private int direction = NORTH;
    private int tempDirection = NORTH;

    private int used_stone = 0;

    private char lastMove = '%';
    private char currMove = '%';

    /* Below are boolean variables for checking different cases */
	private boolean takeTreasure = false;
	private boolean backHome = false;
	private boolean goingToItem = false;
	private boolean goingToTree = false;
    private boolean onWater = false;
    private boolean standingOnWater = false;
    private boolean canbetrue = false;
	private boolean used_raft = false;
	private boolean went_to_water = false;

	/* Astar */
    Astar findPath = new Astar();

    /**
      * updateMap(char) - update map according to what we see in view
      */
    public void updateMap(char view[][]) {

        if (currMove == '%') {
        	/* At the very beginning, initalize map */
            initializeMap(view);
        } else if (currMove == 'r') {
        	/* Turn right is clockwise */
            clockwise();
        } else if (currMove == 'l') {
        	/* Turn left is anticlockwise */
            anticlockwise();
        } else if (currMove == 'f') {
        	/* When move forward, call MoveForward() */
            MoveForward(view);
        } else if (lastMove == 'c') {
        	/* When cut tree, currMove is 'f', lastMove is 'c' */ 
        	goingToTree = false;
        }

        /**
		  *	Update backpack and map when the player get 
		  * key, axe, stone or treasure or cut tree or just
		  * get on land after use raft
          */
        if(map.get(curr_location) != '~' && map.get(last_location) == '~' && backpack.get("Stones") == 0) {
        	backpack.put("Raft", 0);
        	standingOnWater = false;
        }
 
        if (map.get(curr_location) == '$') {
        	/* When get treasure, set treasure coordinate to initialize state */
            TreasureCoord.setX(INITIALCOORD);
            TreasureCoord.setY(INITIALCOORD);
            map.put(new Coordinate(curr_location.getX(), curr_location.getY()), ' ');
            backpack.put("Treasure", 1);
        } else if (map.get(curr_location) == 'k') {
            map.put(new Coordinate(curr_location.getX(), curr_location.getY()), ' ');
            goingToItem = false;
            /* remove the item from ItemToTake list when we actually get it */
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
        } else if (map.get(curr_location) != '~' && map.get(last_location) == '~' && backpack.get("Stones") == 0) {
            backpack.put("Raft", 0);
            standingOnWater = false;
        } else if (map.get(curr_location) == '~' && backpack.get("Stones") == 0 && backpack.get("Raft") == 1) {
            standingOnWater = true;
        } else if (map.get(curr_location) == 'T') {
        	backpack.put("Raft", 1);
        	map.put(new Coordinate(curr_location.getX(), curr_location.getY()), ' ');
        }
    }

    /**
      * initializeMap(char) - Call at the very beginning, only called once
      */
    public void initializeMap (char view[][]) {
        backpack.put("Raft",0);
        backpack.put("Key",0);
        backpack.put("Stones",0);
        backpack.put("Treasure",0);
        backpack.put("Axe",0);
        cornerList.add(new Coordinate(0,0));

        int x = -2;
        /* for each column */
        for (int col = 0; col < 5; col++) {
            int y = 2;
            /* for each row */
            for (int row = 0; row < 5; row++) {
                Coordinate coord = new Coordinate(x, y);
                /* setup the content of the starting point is'#' */
                if (x == 0 && y ==0) {
                    map.put(coord, '#');
                } else {
                	/* 
                	 * Add the Item or Tree coordinate into list or setup Treasure coordinate
                	 * when initialize map if we see them 
                	 */
                    map.put(coord, view[row][col]);
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
    }

    /**
      * clockwise() - Call this function when turn right('r')
      */
    public void clockwise() {
        direction--;
        direction = direction % 4;
        if (direction < 0) direction = direction + 4;
    }

    /**
      * anticlockwise() - Call this function when turn left('l')
      */
    public void anticlockwise() {
    	direction++;
        direction = direction % 4;
    }

    /**
      * findDropWater(char) - during the player exploring land
      * find whether there exist some water which he can drop into
      * and record the location
      */
    public void findDropWater(char view[][]) {
    	int counter = 0;

    	/* check the front five characters are all water */
    	for (int i = 0; i < 5; i++) {
            if (view[1][i] == '~') {
                counter++;
            }
        } 

        /* store the coordinate of the spot that we can enter water */
		if (counter == 5) {
        	if (direction == EAST) {
                dropwater.setX(curr_location.getX() + 1);
                dropwater.setY(curr_location.getY());
            } else if (direction == NORTH) {
            	dropwater.setX(curr_location.getX());
                dropwater.setY(curr_location.getY() + 1);
            } else if (direction == WEST) {
            	dropwater.setX(curr_location.getX() - 1);
                dropwater.setY(curr_location.getY());
            } else if (direction == SOUTH) {
            	dropwater.setX(curr_location.getX());
                dropwater.setY(curr_location.getY() - 1);
            }
        }

        /* check the right five characters are all water */
        counter = 0;
        for (int i = 0; i < 5; i++) {
            if (view[i][3] == '~') {
                counter++;
            }
        } 

        /* store the coordinate of the spot that we can enter water */
        if (counter == 5) {
        	if (direction == EAST) {
        		dropwater.setX(curr_location.getX());
                dropwater.setY(curr_location.getY() - 1);
        	} else if (direction == NORTH) {
        		dropwater.setX(curr_location.getX() + 1);
                dropwater.setY(curr_location.getY());
        	} else if (direction == WEST) {
        		dropwater.setX(curr_location.getX());
                dropwater.setY(curr_location.getY() + 1);
        	} else if (direction == SOUTH) {
        		dropwater.setX(curr_location.getX() - 1);
                dropwater.setY(curr_location.getY());
        	}
        }
    }

    /**
      * MoveForward(char) - update map when move forward one step 
      * for each direction 
      */
    public void MoveForward(char view[][]) {

    	/* setup last_location just before curr_location will be changed */
        last_location.setX(curr_location.getX());
        last_location.setY(curr_location.getY());

        if (direction == EAST) {
            curr_location.setX(curr_location.getX() + 1);
            int expandPart = 0;

            /* We can see one more column when move EAST */
            for (int row = 2; row >= -2; row--) {
                Coordinate expandRow = new Coordinate(curr_location.getX() + 2, curr_location.getY() + row);
                char expandChar = view[0][expandPart];
                map.put(expandRow, expandChar);
                isItemInView(expandChar, expandRow);
                expandPart++;
            }
            findDropWater(view);
        } else if (direction == NORTH) {
            curr_location.setY(curr_location.getY() + 1);
            int expandPart = 0;
            /* Can see one more row when move NORTH */
            for (int col = -2; col <= 2; col++) {
                Coordinate expandCol = new Coordinate(curr_location.getX() + col, curr_location.getY() + 2);
                char expandChar = view[0][expandPart];
                map.put(expandCol, expandChar);
                isItemInView(expandChar, expandCol);
                expandPart++;
            }
            findDropWater(view);
        } else if (direction == WEST) {
            curr_location.setX(curr_location.getX() - 1);
            int expandPart = 0;
            /* Can see one more column when move west */
            for (int row = -2; row <= 2; row++) {
                Coordinate expandRow = new Coordinate(curr_location.getX() - 2, curr_location.getY() + row);
                char expandChar = view[0][expandPart];
                map.put(expandRow, expandChar);
                isItemInView(expandChar, expandRow);
                expandPart++;
            }
            findDropWater(view);
        } else if (direction == SOUTH) {
            curr_location.setY(curr_location.getY() - 1);
            int expandPart = 0;
            /* Can see one move row when move SOUTH */
            for (int col = 2; col >= -2; col --) {
                Coordinate expandCol = new Coordinate(curr_location.getX() + col, curr_location.getY() - 2);
                char expandChar = view[0][expandPart];
                map.put(expandCol, expandChar);
                isItemInView(expandChar, expandCol);
                expandPart++;
            }
            findDropWater(view);
        }

        /**
          * Probably should remove the tree when we actually cut it
          * not when we poll this tree from TreeToCut list
          */
        if (lastMove == 'c') {
            for (int i = 0; i < TreeToCut.size(); i++) {
                if (curr_location.equals(TreeToCut.get(i))) {
                   	
                    TreeToCut.remove(TreeToCut.get(i));
                }
            }
            lastMove = '%';
        }

        /**
          * When we arrive at the location where the player should drop into water 
          * The player should begin to explore water
          * setup up control variable(onWater) for future use
          */
        if (curr_location.equals(dropwater) && !canbetrue) {
            canbetrue = true;
            onWater = true;
            cornerList.clear();
            cornerList.add(dropwater);
        } 

        /* place a store on current location */
        if (map.get(curr_location) == '~' && backpack.get("Stones") > 0){
            backpack.put("Stones", backpack.get("Stones") - 1);
            map.put(new Coordinate(curr_location.getX(), curr_location.getY()), 'O');
        }    

    }

    /**
      * isItemInView(char, Coordinate) - update ItemToTaks list, TreeToCut list
      * and record Treasure Coordinate when we see them in view
      */
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

    /**
      * stateToMove(LinkedList) - Translate a list of states to a list of Commands(Character)
      */
    public LinkedList<Character> stateToMove(LinkedList<State> list_of_states) {

        LinkedList<Character> move_instructions = new LinkedList<>();
        LinkedList<Character> listOfMoves;

        State curr_state = list_of_states.pollLast();
        tempDirection = direction;
        
        while (!list_of_states.isEmpty()) {
            listOfMoves = moveDecision(curr_state.getCurr_coord(), list_of_states.peekLast().getCurr_coord());
            move_instructions.addAll(listOfMoves);
            curr_state = list_of_states.pollLast();
        }

        return move_instructions;
    }
    
    /**
      * moveDecision(Coordinate, Coordinate) - This function is called in stateToMove,
      * aiming to calculat out the commands we need('r', 'l', 'f', 'c', 'u')
      */
    public LinkedList<Character> moveDecision(Coordinate curr_coord, Coordinate next_coord) {

    	LinkedList<Character> list_moves = new LinkedList<>();
        /* used to spin to the correct direction */
        int curDirection = 0;

        if (curr_coord.getX() - next_coord.getX() < 0) {
            curDirection = EAST;
        } else if (curr_coord.getY() - next_coord.getY() < 0) {
            curDirection = NORTH;
        } else if (curr_coord.getX() - next_coord.getX() > 0) {
            curDirection = WEST;
        } else if (curr_coord.getY() - next_coord.getY() > 0) {
            curDirection = SOUTH;
        }

        /* store the curDirection before operations */
        int newDirection = curDirection;
        if (tempDirection - curDirection < 0) {
            if (tempDirection - curDirection == -3) {
                list_moves.add('r');
            } else {
                while (tempDirection - curDirection != 0) {
                    curDirection--;
                    list_moves.add('l');
                }
            }
        } else if (tempDirection - curDirection > 0) {
            if (tempDirection - curDirection == 3) {
              list_moves.add('l');
            } else {
                while (tempDirection - curDirection != 0) {
                    curDirection++;
                    list_moves.add('r');
                }
            }
        }

        if (map.get(next_coord) == '-' && backpack.get("Key") == 1) {
            list_moves.add('u');
            map.put(new Coordinate(next_coord.getX(), next_coord.getY()), ' ');
        } else if (map.get(next_coord) == 'T' && backpack.get("Axe") == 1) {
            list_moves.add('c');
        } 

        /* always 'f' after each opeartion */
        list_moves.add('f');

        /* after operations, restore the tempDirection */
        tempDirection = newDirection;

        return list_moves;
    }

    /**
      * checkObstacle(char, boolean) - When the player is on the land
      * the obstacle are T, -, *, ~, .
      * When the player is on water, all other objects are obstacle
      */
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

        /** 
          * Find Item with no risks 
          * goingToItem: ensure the player only go to get the item just polled from ItemToTake list
          * moves_to_item.isEmpty(): Basicly improve effiency
          * takeTreasure: The player isn't on the way to Treasure
          * onWater: The player is not on water
          * We go to get the item if the player won't use any stones or raft on the path to that item
          * To ensure the path is safe, we won't pass in any stones and raft 
          */
        if (!ItemToTake.isEmpty() && !goingToItem && moves_to_item.isEmpty() && !takeTreasure && !onWater) {
 			
 			/* Use loop to find the item which the player can get */
        	for (int i = 0; i < ItemToTake.size(); i++) {
        		Coordinate curr_Item = ItemToTake.get(i);
            	int initialH = abs(curr_location.getX() - curr_Item.getX()) + abs(curr_location.getY() - curr_Item.getY());
            	State curr_state = new State(curr_location, 0, initialH, 0, 0, null);
            	toItem = findPath.aStarSearch(curr_state, curr_Item, map, backpack, onWater);
            	/* Have found the path to that item, break */
                if(!toItem.isEmpty()) break;
        	}
            if (!toItem.isEmpty()) {
            	/* Translate to Commands */
            	moves_to_item = stateToMove(toItem);
            }
        }

        /* make moves to that item */
        if (!moves_to_item.isEmpty()) {
        	/* 
        	 * moves_to_corner is a list in which there are corner places the player needs to go to 
        	 * must not mess up the corner list with moves_to_item
        	 * explain more explicitly in our algorithm
        	 */
        	moves_to_corner.clear();
            goingToItem = true;
            currMove = moves_to_item.poll();

            if (currMove == 'c') {
                lastMove = currMove;
            }
            return currMove;
        }
        
        /**
          * Find treasure
          * 1. When we know where the treasure is
          * 2. We haven't found the treasure
          */
        if (TreasureCoord.getX() != 200 && !takeTreasure && backpack.get("Treasure") == 0){
            State curr_state = new State(curr_location, 0, 0, backpack.get("Stones"), -1, null);
            if (backpack.get("Raft") == 1) {
                curr_state.setHave_raft(1);
            } else {
                curr_state.setHave_raft(0);
            }
            
            /* run A Star Search */
            toTreasure = findPath.aStarSearch(curr_state, TreasureCoord, map, backpack, onWater);
            
            for(int i = 0; i < toTreasure.size(); i++) {
            	/* check the path is on water */
            	if(map.get(toTreasure.get(i).getCurr_coord()) == '~') {
            		went_to_water = true;
            	}
            	/* count used stones in finding treasure */
            	if(map.get(toTreasure.get(i).getCurr_coord()) == '~' && backpack.get("Stones") != 0) {
            		used_stone++;
            		backpack.put("Stones", backpack.get("Stones")-1);
            	}
            }

            /* translate */
            if (!toTreasure.isEmpty()) moves_to_treasure = stateToMove(toTreasure);
            
            /* check if the raft is used then set raft number to 0 */
            if (!moves_to_treasure.isEmpty()) {
            	if(went_to_water && used_stone == 0) {
            		used_raft = true;
            		backpack.put("Raft", 0);
            		went_to_water = false;
            	}
                takeTreasure = true; 
            } 
            
        }

        
        /** 
          * Find the path to come back
          * moves_back_start,isEmpty(): ensure path to back is empty
          * takeTreasure || backpack.get("Treasure") == 1: ensure we have found the treasure
          */
        if (moves_back_start.isEmpty() && (takeTreasure || backpack.get("Treasure") == 1)){
            if(used_stone != 0) {        		
            	backpack.put("Stones", backpack.get("Stones")+used_stone);
            } 

        	State curr_state;
        	/*
        	 * If the path to treasure has been calculated before,
        	 * find the path from Treasure location to starting point 
        	 * else 
        	 * find the path from current location to starting point
        	 */ 
        	if(takeTreasure) {      		
            	curr_state = new State(TreasureCoord, 0, 0, backpack.get("Stones"), backpack.get("Raft"), null);
            } else {          	
            	curr_state = new State(curr_location, 0, 0, backpack.get("Stones"), backpack.get("Raft"), null);
            }

            /* Starting point is (0, 0) */
            Coordinate start_point = new Coordinate(0,0);
            toStart = findPath.aStarSearch(curr_state, start_point, map, backpack, onWater);

            /* if possible to return back to start location */
            if (!toStart.isEmpty()) {
                direction = tempDirection;
                moves_back_start = stateToMove(toStart);
            } else {
            	/* set the raft number back to 1 if the return path is impossible to return */
            	used_stone = 0;            	
            	if(used_raft) {          		
            		backpack.put("Raft", 1);
            		used_raft = false;
            	}
            }

            /* recalculate the path to Treasure when we cannot find the path to back */
            if (!moves_back_start.isEmpty()) {
            	backHome = true;
            } else {            	
            	takeTreasure = false;
            }
        } 

        /**
          * we begin to get the treasure(On the way to treasure) is when we have found 
          * both the way to treasure and the way to back starting point
          */
        if (takeTreasure && backHome) {
        	moves_to_corner.clear();
        	if(!moves_to_treasure.isEmpty()){
        		currMove = moves_to_treasure.poll();
        	} else {
        		currMove = moves_back_start.poll();

        	}
        	return currMove;
        }        

        /* Find item with stones and raft */
        if (!ItemToTake.isEmpty() && !goingToItem && moves_to_item.isEmpty() && !takeTreasure && !onWater) {

        	for (int i = 0; i < ItemToTake.size(); i++) {
        		/* just retrieve the item, not poll from the list */
        		Coordinate curr_Item = ItemToTake.get(i);
            	int initialH = abs(curr_location.getX() - curr_Item.getX()) + abs(curr_location.getY() - curr_Item.getY());
            	State curr_state = new State(curr_location, 0, initialH, backpack.get("Stones"), backpack.get("Raft"), null);
            	toItem = findPath.aStarSearch(curr_state, curr_Item, map, backpack, onWater);
                if(!toItem.isEmpty()) break;
        	}
            if (!toItem.isEmpty()) {
            	moves_to_item = stateToMove(toItem);
            }
        }

        /* begin to move to that item */
        if (!moves_to_item.isEmpty()) {
        	moves_to_corner.clear();
            
            goingToItem = true;
            currMove = moves_to_item.poll();

            if (currMove == 'c') {
                lastMove = currMove;
            }           

            return currMove;
        }

        /**
          * Find the path to cut tree
          * goingToTree: ensure we cut the tree which is just polled from list
          * moves_to_tree.isEmpty(): Basicly improve effiency
          * backpack.get("Axe"): The player needs have axe 
          */
        if (!TreeToCut.isEmpty() && !goingToTree && moves_to_tree.isEmpty() && backpack.get("Axe") == 1 && !onWater) {
        	
            for (int i = 0; i < TreeToCut.size(); i++) {
            	/* just retrieve the tree, not poll from the list */
                Coordinate curr_tree = TreeToCut.get(i);
                int initialH = abs(curr_location.getX() - curr_tree.getX()) + abs(curr_location.getY() - curr_tree.getY());
                State curr_state = new State(curr_location, 0, initialH, backpack.get("Stones"), backpack.get("Raft"), null);
                toTree = findPath.aStarSearch(curr_state, curr_tree, map, backpack, onWater);

                /* When find the path to that tree, translate path to commands then break */
                if (!toTree.isEmpty()) {
                    moves_to_tree = stateToMove(toTree);
                    break;
                }
            }
        }

        /** 
         * Begin to move towards that tree 
         * Same with item, must not messy up moves_to_corner list with moves_to_tree
         */
        if (!moves_to_tree.isEmpty()) {
        	moves_to_corner.clear();
        	if (lastMove == 'c') {
				goingToTree = false;
        	} else {
        		goingToTree = true;
        		/* lastMove is just retrieve, currMove is poll */ 
        		lastMove = moves_to_tree.peek();
        	}
            
            currMove = moves_to_tree.poll();
            return currMove;
        } else {
            lastMove = currMove;
        }
        
        /**
          * Explore map(expand map) when we cannot find 
          * the way to treasure and the way back home
          */
        if (!takeTreasure && !backHome) {
            exploreMap(view);
        	if (!moves_to_corner.isEmpty()) {
                currMove = moves_to_corner.poll();
                return currMove;
            }
            
        }

        /**
          * After explore the land, we begin to explore water
          * moves_to_corner.isEmpty(): we know the player has already explored all places on land
          * onWater: if the player is on water, doesn't need to go the location where he dropped
          */
        if (!takeTreasure && !backHome && moves_to_corner.isEmpty() && !onWater) {
            int initialH = abs(curr_location.getX() - dropwater.getX()) + abs(curr_location.getY() - dropwater.getY());
            State curr_state = new State(curr_location, 0, initialH, backpack.get("Stones"), backpack.get("Raft"), null);
            todropwater = findPath.aStarSearch(curr_state, dropwater, map, backpack, onWater);
            if (!todropwater.isEmpty()) moves_to_water = stateToMove(todropwater);           
        }

        if (!moves_to_water.isEmpty()) {
            currMove = moves_to_water.poll();
            return currMove;
        }

        /**
          * After explored all locations on water, setup onWater false
          * Because when he explored all locations on water, he is still on water(due to our algorithm)
          * But in our algorithm, the number of raft has been decremented at that moment
          * Therefore we give him a free raft
          */
        onWater = false;
        backpack.put("Raft", 1);
        return currMove = 'z';
    }
    
    /**
      * exploreMap(view) - expand map on land or on water
      */
    public void exploreMap(char[][] view) {

    	/* 
    	 * scan left top corner of the current view
    	 * (-2, 2), (-1, 2), (0, 2), (-2, 1), (-1, 1), (0, 1)
    	 */ 
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3; j++){              
                Coordinate left_top = new Coordinate(curr_location.getX() - 2 + j, curr_location.getY() + 2 - i);
                /* If this point has been in cornerExpand list, don't put it in cornerList again */
                if(!cornerExpand.contains(left_top) && !cornerList.contains(left_top) && !checkObstacle(map.get(left_top), onWater)){
                    cornerList.add(left_top);
                }
            }
        }

        /*
         * scan right top of the current view
         */
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3; j++){
                Coordinate right_top = new Coordinate(curr_location.getX() + 2 - i, curr_location.getY() + 2 - j);
                if(!cornerExpand.contains(right_top) && !cornerList.contains(right_top) && !checkObstacle(map.get(right_top), onWater)){                   
                    cornerList.add(right_top);
                }
            }
        }

        /*
         * scan the right bottom location of the current view
         */
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3; j++){
                Coordinate right_bot = new Coordinate(curr_location.getX() + 2 - j, curr_location.getY() - 2 + i);
                if(!cornerExpand.contains(right_bot) && !cornerList.contains(right_bot) && !checkObstacle(map.get(right_bot), onWater)){                  
                    cornerList.add(right_bot);
                }
            }
        }

        /*
         * scan left bottom location of the current view
         */
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3; j++){
                Coordinate left_bot = new Coordinate(curr_location.getX() - 2 + i, curr_location.getY() - 2 + j);
                if(!cornerExpand.contains(left_bot) && !cornerList.contains(left_bot) && !checkObstacle(map.get(left_bot), onWater)){                 
                    cornerList.add(left_bot);
                }
            }
        }

        /* Find which corner we can reach */
    	for(int i = 0; i < cornerList.size(); i++) {
            State curr_state;
            /* Setup raft depending on whether we have raft */
            if (onWater) {
                curr_state = new State(curr_location, 0, 0, 0, 1, null);
            } else {
                curr_state = new State(curr_location, 0, 0, 0, 0, null);
            } 	

            /* Find the path to that point */	
            LinkedList<State> toCorner = findPath.aStarSearch(curr_state, cornerList.get(i), map, backpack, onWater);
            if(!toCorner.isEmpty()) moves_to_corner = stateToMove(toCorner);
            if(!moves_to_corner.isEmpty()) {
                break;
            }
    	}
    	
    	/* remove the corner location when we reach there */
    	for(int i = 0; i < cornerList.size(); i++) {
    		if(cornerList.get(i).equals(curr_location)) {
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
