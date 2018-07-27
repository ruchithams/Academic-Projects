import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

// Solving the 16-puzzle with A* using two heuristics:
// tiles-out-of-place and total-distance-to-move

public class NumberPuzzle {
    public static final int PUZZLE_WIDTH = 4;
    public static final int BLANK = 0;
    // BETTER:  false for tiles-displaced heuristic, true for Manhattan distance
    public static boolean BETTER = false;
    private static HashMap<NumberPuzzle, Integer> heuristicVal = new HashMap<NumberPuzzle, Integer>();
    
    // You can change this representation if you prefer.
    // If you don't, be careful about keeping the tiles and the blank
    // row and column consistent.
    private int[][] tiles;  // [row][column]
    private int blank_r, blank_c;   // blank row and column

    public static void main(String[] args) {
    	long startTime =System.nanoTime();
//        NumberPuzzle myPuzzle = readPuzzle();
    	
    	NumberPuzzle myPuzzle = new NumberPuzzle();
    	myPuzzle.tiles[0][0] = 10;
    	myPuzzle.tiles[0][1] = 2;
    	myPuzzle.tiles[0][2] = 0;
    	myPuzzle.tiles[0][3] = 4;
    	myPuzzle.tiles[1][0] = 1;
    	myPuzzle.tiles[1][1] = 5;    	
    	myPuzzle.tiles[1][2] = 3;
    	myPuzzle.tiles[1][3] = 8;
    	myPuzzle.tiles[2][0] = 9;
    	myPuzzle.tiles[2][1] = 7;
    	myPuzzle.tiles[2][2] = 6;
    	myPuzzle.tiles[2][3] = 12;
    	myPuzzle.tiles[3][0] = 13;
    	myPuzzle.tiles[3][1] = 14;
    	myPuzzle.tiles[3][2] = 11;
    	myPuzzle.tiles[3][3] = 15;
    	myPuzzle.blank_r = 0;
    	myPuzzle.blank_c = 2;
    	
//        myPuzzle.fetchManhattanDistance(myPuzzle);
//        myPuzzle.fetchTotalIncorrectTiles(myPuzzle);
    	
    	//System.out.println(startTime);
        LinkedList<NumberPuzzle> solutionSteps = myPuzzle.solve(BETTER);
        printSteps(solutionSteps);
        long endTime = System.nanoTime();
        //System.out.println(endTime);
        double elapsedTime = TimeUnit.NANOSECONDS.convert((endTime-startTime), TimeUnit.SECONDS);
        System.out.println(elapsedTime);
        DecimalFormat result = new DecimalFormat("##.00");
        System.out.println(result.format(elapsedTime));
    }

    NumberPuzzle() {
        tiles = new int[PUZZLE_WIDTH][PUZZLE_WIDTH];
    }

    // Used to read the user input
    static NumberPuzzle readPuzzle() {
        NumberPuzzle newPuzzle = new NumberPuzzle();
        
        Scanner myScanner = new Scanner(System.in);
        int row = 0;
        while (myScanner.hasNextLine() && row < PUZZLE_WIDTH) {
            String line = myScanner.nextLine();
            String[] numStrings = line.split(" ");
            for (int i = 0; i < PUZZLE_WIDTH; i++) {
                if (numStrings[i].equals("-")) {
                    newPuzzle.tiles[row][i] = BLANK;
                    newPuzzle.blank_r = row;
                    newPuzzle.blank_c = i;
                } else {
                    newPuzzle.tiles[row][i] = new Integer(numStrings[i]);
                }
            }
            row++;
        }
        return newPuzzle;
    }

    public String toString() {
        String out = "";
        for (int i = 0; i < PUZZLE_WIDTH; i++) {
            for (int j = 0; j < PUZZLE_WIDTH; j++) {
                if (j > 0) {
                    out += " ";
                }
                if (tiles[i][j] == BLANK) {
                    out += "-";
                } else {
                    out += tiles[i][j];
                }
            }
            out += "\n";
        }
        return out;
    }

    public NumberPuzzle copy() {
        NumberPuzzle clone = new NumberPuzzle();
        clone.blank_r = blank_r;
        clone.blank_c = blank_c;
        for (int i = 0; i < PUZZLE_WIDTH; i++) {
            for (int j = 0; j < PUZZLE_WIDTH; j++) {
                clone.tiles[i][j] = this.tiles[i][j];
            }
        }
        return clone;
    }

    // betterH:  if false, use tiles-out-of-place heuristic
    //           if true, use total-manhattan-distance heuristic
    LinkedList<NumberPuzzle> solve(boolean betterH) {
    	HashMap<NumberPuzzle, Integer> exploredPuzzles = new HashMap<>(); 
        HashMap<NumberPuzzle, NumberPuzzle> parentPuzzle = new HashMap<>();
        HashMap<NumberPuzzle, Integer> depthCostMap = new HashMap<>();
        PriorityQueue<NumberPuzzle> openPuzzleQueue = new PriorityQueue<>(1000,new PuzzleComparator());
        openPuzzleQueue.add(this);
        parentPuzzle.put(this, null);
        heuristicVal.put(this, 0);
        LinkedList<NumberPuzzle> result= new LinkedList<>();
        
        int depthCost = 0;
        //System.out.println("depthCost"+ depthCostMap.size());
        depthCostMap.put(this, depthCost);
        while( !openPuzzleQueue.isEmpty() )
        {
        	
        	NumberPuzzle selectedPuzzle = openPuzzleQueue.poll();
        	
        	if(selectedPuzzle.solved())
        	{
        		while(parentPuzzle.get(selectedPuzzle) != null)
        		{
        			result.add(0, selectedPuzzle);
        			selectedPuzzle = parentPuzzle.get(selectedPuzzle);
        		}
        		result.add(0, selectedPuzzle);
        		return result;
        	}
        	List<NumberPuzzle> children = fetchAllChildren(selectedPuzzle);
        	depthCost = depthCostMap.get(selectedPuzzle);
        	for(NumberPuzzle puzzle : children)
        	{
        		if(!exploredPuzzles.containsKey(puzzle))
        		{
        			
        			if(betterH)
        			{
        				//System.out.println("heuristic cost"+ depthCost + fetchManhattanDistance(puzzle) + 1);
        				heuristicVal.put(puzzle, depthCost + fetchManhattanDistance(puzzle) + 1);
        			}
        			else
        			{
        				heuristicVal.put(puzzle, depthCost + fetchTotalIncorrectTiles(puzzle) + 1);
        			}
        			depthCostMap.put(puzzle, depthCost+1);
        			openPuzzleQueue.add(puzzle);
        			parentPuzzle.put(puzzle, selectedPuzzle);
        			exploredPuzzles.put(puzzle, 1);
        			//System.out.println("depthCost inside for"+ depthCostMap.size());
        		}
        	}
        	//System.out.println("depthCost outside"+ depthCostMap.size());
        }
        
        return null;
        
    }
    
    
    private static class PuzzleComparator implements Comparator<NumberPuzzle>
    {

		@Override
		public int compare(NumberPuzzle o1, NumberPuzzle o2) {
			
			return (heuristicVal.get(o1) - heuristicVal.get(o2));
				
			
		}
    	
    }

    public boolean solved() {
        int shouldBe = 1;
        for (int i = 0; i < PUZZLE_WIDTH; i++) {
            for (int j = 0; j < PUZZLE_WIDTH; j++) {
                if (tiles[i][j] != shouldBe) {
                    return false;
                } else {
                    // Take advantage of BLANK == 0
                    shouldBe = (shouldBe + 1) % (PUZZLE_WIDTH*PUZZLE_WIDTH);
                }
            }
        }
        return true;
    }

    static void printSteps(LinkedList<NumberPuzzle> steps) {
        for (NumberPuzzle s : steps) {
            System.out.println(s);
        }
    }
    
    
    public int fetchXPosition(int value)
    {
    	return (value-1)/ PUZZLE_WIDTH; 
    }
    
    public int fetchYPosition(int value)
    {
    	return (value-1) % PUZZLE_WIDTH;
    }
    
    public int fetchManhattanDistance(NumberPuzzle numPuzzle){
    	
    	int sum =0;
    	for(int i=0; i < PUZZLE_WIDTH; i++)
    	{
    		for(int j=0; j < PUZZLE_WIDTH; j++)
    		{
    		    if(numPuzzle.tiles[i][j] != BLANK)
    		    {
    		        sum += Math.abs(i - fetchXPosition(numPuzzle.tiles[i][j])) + Math.abs(j - fetchYPosition(numPuzzle.tiles[i][j]));
    		    }
    		    else
    		    {
    		        sum += Math.abs(i - (PUZZLE_WIDTH-1))  + Math.abs(j - (PUZZLE_WIDTH-1));
    		    }
    			
    		}
    	}
    	//System.out.println("manhattan distance"+ sum);
    	return sum;
    	
    	
    }
    
    
    public boolean isIncorrectTilePosition(int tileValue,int row,int column)
    {
        return ((((tileValue-1)/PUZZLE_WIDTH) != row ) || (((tileValue-1) %PUZZLE_WIDTH) != column) );
    } 
    
    public int fetchTotalIncorrectTiles(NumberPuzzle numPuzzle)
    {
        int count=0;
        for(int i=0; i < PUZZLE_WIDTH; i++)
    	{
    		for(int j=0; j < PUZZLE_WIDTH; j++)
    		{
    		    if(isIncorrectTilePosition(numPuzzle.tiles[i][j], i, j))
    		    {
    		        count++;
    		    }
    		    
    		}
    	}
    	//System.out.println("Incorrect tile count"+ count);
    	return count;
    }
    
    public NumberPuzzle FetchPuzzleByMovingBlank(NumberPuzzle numPuzzle, int new_blank_r, int new_blank_c )
    {
    
        NumberPuzzle newPuzzle = new  NumberPuzzle();
        int old_blank_r = numPuzzle.blank_r;
        int old_blank_c = numPuzzle.blank_c;
        int value = numPuzzle.tiles[new_blank_r][new_blank_c];
        
        for(int i=0; i< PUZZLE_WIDTH; i++)
        {
            for(int j=0; j< PUZZLE_WIDTH; j++)
            {
                if( (i == new_blank_r) && (j == new_blank_c))
                {
                    newPuzzle.tiles[i][j] = BLANK;
                    newPuzzle.blank_r = i;
                    newPuzzle.blank_c = j;
                }
                else if ((i == old_blank_r) && (j == old_blank_c))
                {
                    newPuzzle.tiles[i][j] = value;
                }
                else
                {
                    newPuzzle.tiles[i][j] = numPuzzle.tiles[i][j];
                }
                    
            }
        }
        return newPuzzle;
    }
    
    public List<NumberPuzzle> fetchAllChildren(NumberPuzzle numPuzzle)
    {
    	List<NumberPuzzle> puzzleList = new ArrayList<>();
        for(int i=numPuzzle.blank_r-1 ; i<= numPuzzle.blank_r+1; i++)
        {
            for(int j=numPuzzle.blank_c-1; j<= numPuzzle.blank_c+1; j++)
            {
            	if(!(i == numPuzzle.blank_r && j == numPuzzle.blank_c))
            	{
            		if(i >= 0 && j >=0 && i < PUZZLE_WIDTH && j <  PUZZLE_WIDTH && (Math.abs(i-numPuzzle.blank_r) +Math.abs(j-numPuzzle.blank_c))<=1 )
                    {
                        puzzleList.add(FetchPuzzleByMovingBlank(numPuzzle, i,j));
                    }
            	}
            	
            }
            
        }
        return puzzleList;
    }
    
    
    
    
   

}