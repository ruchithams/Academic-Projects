import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class OnIce {

	public static final double GOLD_REWARD = 100.0;
	public static final double PIT_REWARD = -150.0;
	public static final double DISCOUNT_FACTOR = 0.5;
	public static final double EXPLORE_PROB = 0.2;  //for Q-learning
	public static final double LEARNING_RATE = 0.1;
	public static final int ITERATIONS = 10000;
	public static final int MAX_MOVES = 1000;
	public static final int ACTION_COUNT = 4;
	public static final int UP = 0;
	public static final int DOWN = 2;
	public static final int RIGHT = 1;
	public static final int LEFT = 3;
	public static final String[] DIRECTION = {"U","R", "D", "L"};
	public static final String PIT = "P";
	public static final String GOLD = "G";

	// Using a fixed random seed so that the behavior is a little
	// more reproducible across runs & students
	public static Random rng = new Random(2018);

	public static void main(String[] args) throws FileNotFoundException {
		Scanner myScanner = new Scanner(System.in);

		Problem problem = new Problem(myScanner);

		Policy policy = problem.solve(ITERATIONS);
		if (policy == null) {
			System.err.println("No policy.  Invalid solution approach?");
		} else {
			System.out.println(policy);
		}
		if (args.length > 0 && args[0].equals("eval")) {
			System.out.println("Average utility per move: " 
					+ tryPolicy(policy, problem));
		}
	}

	static class Problem {
		public String approach;
		public double[] moveProbs;
		public ArrayList<ArrayList<String>> map;

		// Format looks like
		// MDP    [approach to be used]
		// 0.7 0.2 0.1   [probability of going 1, 2, 3 spaces]
		// - - - - - - P - - - -   [space-delimited map rows]
		// - - G - - - - - P - -   [G is gold, P is pit]
		//
		// You can assume the maps are rectangular, although this isn't enforced
		// by this constructor.

		Problem (Scanner sc) {
			approach = sc.nextLine();
			String probsString = sc.nextLine();
			String[] probsStrings = probsString.split(" ");
			moveProbs = new double[probsStrings.length];
			for (int i = 0; i < probsStrings.length; i++) {
				try {
					moveProbs[i] = Double.parseDouble(probsStrings[i]);
				} catch (NumberFormatException e) {
					break;
				}
			}
			map = new ArrayList<ArrayList<String>>();
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] squares = line.split(" ");
				ArrayList<String> row = new ArrayList<String>(Arrays.asList(squares));
				map.add(row);
			}
		}

		Policy solve(int iterations) {
			if (approach.equals("MDP")) {
				MDPSolver mdp = new MDPSolver(this);
				return mdp.solve(this, iterations);
			} else if (approach.equals("Q")) {
				QLearner q = new QLearner(this);
				return q.solve(this, iterations);
			}
			return null;
		}

	}

	static class Policy {
		public String[][] bestActions;

		public Policy(Problem prob) {
			bestActions = new String[prob.map.size()][prob.map.get(0).size()];
		}

		public String toString() {
			String out = "";
			for (int r = 0; r < bestActions.length; r++) {
				for (int c = 0; c < bestActions[0].length; c++) {
					if (c != 0) {
						out += " ";
					}
					out += bestActions[r][c];
				}
				out += "\n";
			}
			return out;
		}
	}

	// Returns the average utility per move of the policy,
	// as measured from ITERATIONS random drops of an agent onto
	// empty spaces
	public static double tryPolicy(Policy policy, Problem prob) {
		int totalUtility = 0;
		int totalMoves = 0;
		for (int i = 0; i < ITERATIONS; i++) {
			// Random empty starting loc
			int row, col;
			do {
				row = rng.nextInt(prob.map.size());
				col = rng.nextInt(prob.map.get(0).size());
			} while (!prob.map.get(row).get(col).equals("-"));
			// Run until pit, gold, or MAX_MOVES timeout 
			// (in case policy recommends driving into wall repeatedly,
			// for example)
			for (int moves = 0; moves < MAX_MOVES; moves++) {
				totalMoves++;
				String policyRec = policy.bestActions[row][col];
				// Determine how far we go in that direction
				int displacement = 1;
				double totalProb = 0;
				double moveSample = rng.nextDouble();
				for (int p = 0; p <= prob.moveProbs.length; p++) {
					totalProb += prob.moveProbs[p];
					if (moveSample <= totalProb) {
						displacement = p+1;
						break;
					}
				}
				int new_row = row;
				int new_col = col;
				if (policyRec.equals("U")) {
					new_row -= displacement;
					if (new_row < 0) {
						new_row = 0;
					}
				} else if (policyRec.equals("R")) {
					new_col += displacement;
					if (new_col >= prob.map.get(0).size()) {
						new_col = prob.map.get(0).size()-1;
					}
				} else if (policyRec.equals("D")) {
					new_row += displacement;
					if (new_row >= prob.map.size()) {
						new_row = prob.map.size()-1;
					}
				} else if (policyRec.equals("L")) {
					new_col -= displacement;
					if (new_col < 0) {
						new_col = 0;
					}
				}
				row = new_row;
				col = new_col;
				if (prob.map.get(row).get(col).equals("G")) {
					totalUtility += GOLD_REWARD;
					// End the current trial
					break;
				} else if (prob.map.get(row).get(col).equals("P")) {
					totalUtility += PIT_REWARD;
					break;
				}
			}
		}

		return totalUtility/(double)totalMoves;
	}



	static class MDPSolver {

		// We'll want easy access to the real rewards while iterating, so
		// we'll keep both of these around
		public double[][] utilities;
		public double[][] rewards;

		public MDPSolver(Problem prob) {
			utilities = new double[prob.map.size()][prob.map.get(0).size()];
			rewards = new double[prob.map.size()][prob.map.get(0).size()];
			// Initialize utilities to the rewards in their spaces,
			// else 0
			for (int r = 0; r < utilities.length; r++) {
				for (int c = 0; c < utilities[0].length; c++) {
					String spaceContents = prob.map.get(r).get(c);
					if (spaceContents.equals("G")) {
						utilities[r][c] = GOLD_REWARD;
						rewards[r][c] = GOLD_REWARD;
					} else if (spaceContents.equals("P")) {
						utilities[r][c] = PIT_REWARD;
						rewards[r][c] = PIT_REWARD;
					} else {
						utilities[r][c] = 0.0;
						rewards[r][c] = 0.0;
					}
				}
			}
		}


        // Fetches the final policy
		Policy solve(Problem prob, int iterations) {
			Policy policy = new Policy(prob);
			for(int k=0; k <= ITERATIONS; k++)
			{
				for(int i=0; i < utilities.length; i++)
				{
					for(int j=0; j< utilities[0].length; j++)
					{
						calculateUtilityValue(prob, i, j, policy);
					}

				}
			}
			return policy;
		}

        // Fetch the index of next row or column after displacement
		public int fetchNextStateRowOrColumn(int noOfSteps , int direction, int currVal)
		{
			int maxVal = (direction == DOWN) ? utilities.length-1 : utilities[0].length-1;
			if( direction == UP  || direction == LEFT)
			{
				if( currVal  - noOfSteps <= 0)
					return 0;
				else
					return (currVal - noOfSteps);
			}
			else
			{
				if( currVal  + noOfSteps >= maxVal)
					return maxVal;
				else
					return (currVal + noOfSteps);
			}
		}

        // returns true if the given action from the current row or column results in out of bound condidtion
        // else return true
		public boolean isOutOfBound(int actionVal, int row, int col)
		{
			if( (actionVal == UP  &&  row == 0) ||  ( actionVal == LEFT  && col == 0) )
			{
				return true;
			}
			else if( (actionVal == DOWN &&  row == utilities.length-1)  || (actionVal == RIGHT  && col == utilities[0].length-1) )
			{
				return true;
			}
			return false;	
		}

        // Calculates utility value for all the states in the policy for MDP approach
		public void calculateUtilityValue(Problem prob, int row, int col, Policy pol)
		{
			double maxActionVal = Double.MIN_VALUE;
			int maxAction;
			int chosenAction=0;
			for(int i=0; i < ACTION_COUNT; i++)
			{
				double newSampleVal = 0;

				
				if(isOutOfBound(i, row, col))
				{
					continue;
				}


				for(int j=0; j < prob.moveProbs.length ; j++ )
				{

					int nextStateVal=0;

					if( i == UP || i == DOWN)
					{
						nextStateVal = fetchNextStateRowOrColumn(j+1, i, row);
						newSampleVal += prob.moveProbs[j] * utilities[nextStateVal][col];


					}
					else
					{
						nextStateVal = fetchNextStateRowOrColumn(j+1, i, col);
						newSampleVal += prob.moveProbs[j] * utilities[row][nextStateVal];

					}


				}
				
				if(newSampleVal > maxActionVal)
				{
					maxActionVal = newSampleVal;
					chosenAction = i;
				}
			}
			utilities[row][col] = rewards[row][col] + DISCOUNT_FACTOR * maxActionVal;
			if(prob.map.get(row).get(col).equals("G")  || prob.map.get(row).get(col).equals("P"))
			{
				pol.bestActions[row][col]  = prob.map.get(row).get(col);
			}
			else
			{
				pol.bestActions[row][col]  = DIRECTION[chosenAction];
			}
		}
	}

	// QLearner:  Same problem as MDP, but the agent doesn't know what the
	// world looks like, or what its actions do.  It can learn the utilities of
	// taking actions in particular states through experimentation, but it
	// has no way of realizing what the general action model is 
	// (like "Right" increasing the column number in general).
	static class QLearner {

		// Use these to index into the first index of utilities[][][]
		public static final int UP = 0;
		public static final int RIGHT = 1;
		public static final int DOWN = 2;
		public static final int LEFT = 3;
		public static final int ACTIONS = 4;

		public double utilities[][][];  // utilities of actions
		public double rewards[][];

		public QLearner(Problem prob) {
			utilities = new double[ACTIONS][prob.map.size()][prob.map.get(0).size()];
			// Rewards are for convenience of lookup; the learner doesn't
			// actually "know" they're there until encountering them
			rewards = new double[prob.map.size()][prob.map.get(0).size()];
			for (int r = 0; r < rewards.length; r++) {
				for (int c = 0; c < rewards[0].length; c++) {
					String locType = prob.map.get(r).get(c);
					if (locType.equals("G")) {
						rewards[r][c] = GOLD_REWARD;
					} else if (locType.equals("P")) {
						rewards[r][c] = PIT_REWARD;
					} else {
						rewards[r][c] = 0.0; // not strictly necessary to init
					}
				}
			}
			// Java: default init utilities to 0
		}

        // Return policy obtained after applying Q-Learning algorithm
		public Policy solve(Problem prob, int iterations) {
			Policy policy = new Policy(prob);
			int row;
			int col;
			for (int i=0; i<= ITERATIONS ; i++)
			{

				row = rng.nextInt(prob.map.size());
				col = rng.nextInt(prob.map.get(0).size());							
				fetchUtilityValue(prob, row, col, policy);

			}

			for (int r = 0; r < rewards.length; r++) 
			{
				for (int c = 0; c < rewards[0].length; c++) 
				{
					if(policy.bestActions[r][c] == null)
					{
						int direction =0;
						double maxQVal = Integer.MIN_VALUE;
						for(int i=0; i< ACTION_COUNT ; i++)
						{ 
							if(utilities[i][r][c] > maxQVal)
							{
								maxQVal =  utilities[i][r][c];
								direction = i;
							}
						}
						policy.bestActions[r][c] = DIRECTION[direction];
					}
					
				}
			}
			return policy;
		}

        // returns true if the given action from the current row or column results in out of bound condidtion
        // else return true
		public boolean isOutOfBound(int actionVal, int row, int col)
		{
			if( ( actionVal == UP  &&  row == 0) ||  ( actionVal == LEFT  && col == 0) )
			{
				return true;
			}
			else if( ( actionVal == DOWN &&  row == rewards.length-1)  || ( actionVal == RIGHT  && col == rewards[0].length-1) )
			{
				return true;
			}
			return false;	
		}

        // Returns the next row or column index of the next state based on the given direction
		public int fetchNextStateRowOrColumn(int noOfSteps , int direction, int currVal)
		{
			int maxVal = (direction == DOWN) ? rewards.length-1 : rewards[0].length-1;
			if( direction == UP  || direction == LEFT)
			{
				if( currVal  - noOfSteps < 0)
					return 0;
				else
					return (currVal - noOfSteps);
			}
			else
			{
				if( currVal  + noOfSteps > maxVal)
					return maxVal;
				else
					return (currVal + noOfSteps);
			}
		}

        // returns the displacement value obtained randomly
		public int fetchDisplacementValue(Problem prob)
		{
			double moveSample = rng.nextDouble();
			int displacement = 1;
			double totalProb = 0;

			for (int p = 0; p <= prob.moveProbs.length; p++) {
				totalProb += prob.moveProbs[p];
				if (moveSample <= totalProb) {
					displacement = p+1;
					break;
				}
			}
			return displacement;
		}

        // Calculates utility value for all the states in the policy for Q-Leraning algorithm
		public void fetchUtilityValue(Problem prob, int row, int col, Policy pol)
		{
			double maxActionVal = Double.MIN_VALUE;
			int chosenAction = -1;
			int chosenRow = -1;
			int chosenCol = -1;
			
			while(true)	
			{
				
				if(prob.map.get(row).get(col).equals("P"))
				{

					
					for(int i=0 ; i< ACTION_COUNT; i++)
					{
						utilities[i][row][col] = PIT_REWARD;
					}
					pol.bestActions[row][col] = PIT;
					
					break;
				}
				else if(prob.map.get(row).get(col).equals("G"))
				{
					for(int i=0 ; i< ACTION_COUNT; i++)
					{
					
						utilities[i][row][col] = GOLD_REWARD;
					}
					pol.bestActions[row][col] = GOLD;
					break;
				}
				
				if(rng.nextDouble() < EXPLORE_PROB)
				{
					
					chosenAction = rng.nextInt( ACTION_COUNT);
					while(isOutOfBound(chosenAction, row, col))
					{
						
						chosenAction = rng.nextInt(4);
					}
					int displacement = fetchDisplacementValue(prob);
					if(chosenAction == UP || chosenAction == DOWN)
						chosenRow = fetchNextStateRowOrColumn( displacement, chosenAction, row);
					else
						chosenCol = fetchNextStateRowOrColumn( displacement, chosenAction, col);
					maxActionVal = calculateQValue(prob, chosenAction, row, col, chosenRow, chosenCol);
					utilities[chosenAction][row][col] = maxActionVal;
					
				}
				else
				{
					double maxUtility = Integer.MIN_VALUE;
					for(int i=0; i < ACTION_COUNT; i++)
					{
						if(isOutOfBound(i, row, col))
						{
							continue;
						}
						if(maxUtility < utilities[i][row][col])
						{
							maxUtility = utilities[i][row][col];
							chosenAction = i;
						}
					}


					int displacement = fetchDisplacementValue(prob);

					if(chosenAction == UP || chosenAction == DOWN)
						chosenRow = fetchNextStateRowOrColumn( displacement, chosenAction, row);
					else
						chosenCol = fetchNextStateRowOrColumn( displacement, chosenAction, col);
					maxActionVal = calculateQValue(prob, chosenAction, row, col, chosenRow, chosenCol);

					utilities[chosenAction][row][col] = maxActionVal;

				}
				if(chosenAction == UP || chosenAction == DOWN) 
					row = chosenRow;
				else
					col = chosenCol;


			}


		}

        // Returns Max Q-value for a given state
		public double fetchMaxQValue(int row, int col)
		{
			double maxQVal = Integer.MIN_VALUE;
			for(int i=0; i < ACTION_COUNT; i++)
			{
				maxQVal = Math.max(maxQVal, utilities[i][row][col]);
			}
			return maxQVal;

		}

        // Calculates Q-value for the current state
		public double calculateQValue(Problem prob, int chosenAction, int row, int col, int newRow, int newCol) 
		{
			
			double newSampleVal = 0;
			if( chosenAction  == UP || chosenAction == DOWN)
			{

				newSampleVal =  fetchMaxQValue(newRow, col);
			}
			else
			{

				newSampleVal = fetchMaxQValue(row, newCol) ;
			}
			
			double QValue =  utilities[chosenAction][row][col] + LEARNING_RATE * (rewards[row][col] +  (DISCOUNT_FACTOR * newSampleVal - utilities[chosenAction][row][col]));
			return QValue;
		}
	}
}
