//final working
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.security.auth.x500.X500Principal;

// An assignment on decision trees, using the "Adult" dataset from
// the UCI Machine Learning Repository.  The dataset predicts
// whether someone makes over $50K a year from their census data.
//
// Input data is a comma-separated values (CSV) file where the
// target classification is given a label of "Target."
// The other headers in the file are the feature names.
//
// Features are assumed to be strings, with comparison for equality
// against one of the values as a decision, unless the value can
// be parsed as a double, in which case the decisions are < comparisons
// against the values seen in the data.

public class DecisionTree {

	public HashMap<String, Integer> featureConsidered;
	public Feature feature;   // if true, follow the yes branch
	public boolean decision;  // for leaves
	public DecisionTree yesBranch;
	public DecisionTree noBranch;
	public static int YES = 1;
	public static int NO = 0;

	public static double CHI_THRESH = 3.84;  // chi-square test critical value
	public static double EPSILON = 0.00000001; // for determining whether vals roughly equal
	public static boolean PRUNE = true;  // prune with chi-square test or not

	public static void main(String[] args) {
		try
		{
			//Scanner scanner = new Scanner(System.in);
			Scanner scanner = new Scanner(new File("/Users/ruchitha/Downloads/adult.data.csv"));
			// Keep header line around for interpreting decision trees
			System.out.println(scanner.hasNextLine());
			String header = scanner.nextLine();
			Feature.featureNames = header.split(",");
			System.err.println("Reading training examples...");
			ArrayList<Example> trainExamples = readExamples(scanner);
			// We'll assume a delimiter of "---" separates train and test as before
			DecisionTree tree = new DecisionTree(trainExamples);
			System.out.println(tree);
			System.out.println("Training data results: ");
			System.out.println(tree.classify(trainExamples));
			System.err.println("Reading test examples...");
			ArrayList<Example> testExamples = readExamples(scanner);
			Results results = tree.classify(testExamples);
			System.out.println("Test data results: ");
			System.out.print(results);
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}

	}

	public static ArrayList<Example> readExamples(Scanner scanner) {
		ArrayList<Example> examples = new ArrayList<Example>();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("---")) {
				break;
			}
			// Skip missing data lines
			if (!line.contains("?")) {
				Example newExample = new Example(line);
				examples.add(newExample);
			}
		}
		return examples;
	}

	public static class Example {
		// Not all features will use both arrays.  The Feature.isNumerical static
		// array will determine whether the numericals array can be used.  If not,
		// the strings array will be used.  The indices correspond to the columns
		// of the input, and thus the different features.  "target" is special
		// as it gives the desired classification of the example.
		public String[] strings;     // Use only if isNumerical[i] is false
		public double[] numericals;  // Use only if isNumerical[i] is true
		boolean target;

		// Construct an example from a CSV input line
		public Example(String dataline) {
			// Assume a basic CSV with no double-quotes to handle real commas
			strings = dataline.split(",");
			// We'll maintain a separate array with everything that we can
			// put into numerical form, in numerical form.
			// No real need to distinguish doubles from ints.
			numericals = new double[strings.length];
			if (Feature.isNumerical == null) {
				// First data line; we're determining types
				Feature.isNumerical = new boolean[strings.length];
				for (int i = 0; i < strings.length; i++) {
					if (Feature.featureNames[i].equals("Target")) {
						target = strings[i].equals("1");
					} else {
						try {
							numericals[i] = Double.parseDouble(strings[i]);
							Feature.isNumerical[i] = true;
						} catch (NumberFormatException e) {
							Feature.isNumerical[i] = false;
							// string stays where it is, in strings
						}
					}
				}
			} else {
				for (int i = 0; i < strings.length; i++) {
					if (i >= Feature.isNumerical.length) {
						System.err.println("Too long line: " + dataline);
					} else if (Feature.featureNames[i].equals("Target")) {
						target = strings[i].equals("1");
					} else if (Feature.isNumerical[i]) {
						try {
							numericals[i] = Double.parseDouble(strings[i]);
						} catch (NumberFormatException e) {
							Feature.isNumerical[i] = false;
							// string stays where it is
						}
					}
				}
			}
		}

		// Possibly of help in debugging:  a way to print examples
		public String toString() {
			String out = "";
			for (int i = 0; i < Feature.featureNames.length; i++) {
				out += Feature.featureNames[i] + "=" + strings[i] + ";";
			}
			return out;
		}
	}

	public static class Feature {
		// Which feature are we talking about?  Can index into Feature.featureNames
		// to get name of the feature, or into strings and numericals arrays of example
		// to get feature value
		public int featureNum;
		// WLOG assume numerical features are "less than"
		// and String features are "equal to"
		public String svalue;  // the string value to compare a string feature against
		public double dvalue;  // the numerical threshold to compare a numerical feature against
		public static String[] featureNames;  // extracted from the header
		public static boolean[] isNumerical = null;  // need to read a line to see the size

		public Feature(int featureNum, String value) {
			this.featureNum = featureNum;
			this.svalue = value;
		}

		public Feature(int featureNum, double value) {
			this.featureNum = featureNum;
			this.dvalue = value;
		}

		// Ask whether the answer is "yes" or "no" to the question implied by this feature
		// when applied to a particular example
		public boolean apply(Example e) {
			if (Feature.isNumerical[featureNum]) {
				return (e.numericals[featureNum] < dvalue);
			} else {
				return (e.strings[featureNum].equals(svalue));
			}
		}

		// It's suggested that when you generate a collection of potential features, you
		// use a HashSet to avoid duplication of features.  The equality and hashCode operators
		// that follow can help you with this.
		public boolean equals(Object o) {
			if (!(o instanceof Feature)) {
				return false;
			}
			Feature otherFeature = (Feature) o;
			if (featureNum != otherFeature.featureNum) {
				return false;
			} else if (Feature.isNumerical[featureNum]) {
				if (Math.abs(dvalue - otherFeature.dvalue) < EPSILON) {
					return true;
				}
				return false;
			} else {
				if (svalue.equals(otherFeature.svalue)) {
					return true;
				}
				return false;
			}
		}

		public int hashCode() {
			return (featureNum + (svalue == null ? 0 : svalue.hashCode()) + (int) (dvalue * 10000));
		}

		// Print feature's check; called when printing decision trees
		public String toString() {
			if (Feature.isNumerical[featureNum]) {
				return Feature.featureNames[featureNum] + " < " + dvalue;
			} else {
				return Feature.featureNames[featureNum] + " = " + svalue;
			}
		}


	}


	// Calculating the entropy values for the selected feature value when value is of type  Double
	public double calculateEntropy(ArrayList<Example> examples, int selectedfeature, double featureVal, int targetCol)
	{
		double ttlCount = examples.size();
		double entropy = 0; 
		double featureTrueCnt = examples.stream().filter(x-> x.numericals[selectedfeature]<featureVal && x.target ).count();
		double featureFalseCnt = examples.stream().filter(x-> x.numericals[selectedfeature]<featureVal && !x.target ).count();
		double weight1 = featureFalseCnt+featureTrueCnt;
		double notFeatureTrueCnt = examples.stream().filter(x-> x.numericals[selectedfeature]>=featureVal && x.target ).count();
		double notFeatureFalseCnt = examples.stream().filter(x-> x.numericals[selectedfeature]>=featureVal && !x.target ).count();
		double weight2 = notFeatureTrueCnt+notFeatureFalseCnt;
		double prob1 = (featureTrueCnt ==0) ? 0 : featureTrueCnt/weight1;
		double prob2 = (featureFalseCnt ==0) ? 0 : featureFalseCnt/weight1;
		double prob3 = ( notFeatureTrueCnt ==0) ? 0 : notFeatureTrueCnt/weight2;
		double prob4 = ( notFeatureFalseCnt ==0) ? 0 :notFeatureFalseCnt/weight2;
		double logVal1 = (prob1==0 )?0:( Math.log(prob1)/Math.log(2));
		double logVal2 = (prob2==0 )?0:( Math.log(prob2)/Math.log(2));
		double logVal3 = (prob3==0 )?0:( Math.log(prob3)/Math.log(2));
		double logVal4 = (prob4==0 )?0:( Math.log(prob4)/Math.log(2));
		double res1 =(weight1/ ttlCount)* (-(prob1 * logVal1) - (prob2 * logVal2)); 
		double res2 = (weight2/ ttlCount)* (-(prob3 * logVal3) - (prob4 *logVal4)); 
		entropy = res1 + res2;
		return entropy;
	}
	
	// Calculating the entropy values for the selected feature value when value is of type string 
	public double calculateEntropy(ArrayList<Example> examples, int selectedfeature, String featureVal, int targetCol)
	{
		double ttlCount = examples.size();
		double entropy =0;

		double featureTrueCnt = examples.stream().filter(x-> x.strings[selectedfeature].equals(featureVal) && x.target ).count();
		double featureFalseCnt = examples.stream().filter(x-> x.strings[selectedfeature].equals(featureVal) && !x.target).count();
		double weight1 = featureTrueCnt + featureFalseCnt;
		double prob1 = (featureTrueCnt ==0) ? 0 : featureTrueCnt/weight1;
		double prob2 = (featureFalseCnt ==0) ? 0 : featureFalseCnt/weight1;

		double notFeatureTrueCnt = examples.stream().filter(x-> !x.strings[selectedfeature].equals(featureVal) && x.target ).count();
		double notFeatureFalseCnt = examples.stream().filter(x-> !x.strings[selectedfeature].equals(featureVal) && !x.target).count();
		double weight2 = notFeatureTrueCnt + notFeatureFalseCnt;
		double prob3 = ( notFeatureTrueCnt ==0) ? 0 : notFeatureTrueCnt/weight2;
		double prob4 = ( notFeatureFalseCnt ==0) ? 0 :notFeatureFalseCnt/weight2;

		double logVal1 = (prob1==0 )?0:( Math.log(prob1)/Math.log(2));
		double logVal2 = (prob2==0 )?0:( Math.log(prob2)/Math.log(2));
		double logVal3 = (prob3==0 )?0:( Math.log(prob3)/Math.log(2));
		double logVal4 = (prob4==0 )?0:( Math.log(prob4)/Math.log(2));
		double res1 =(weight1/ ttlCount)* (-(prob1 * logVal1) - (prob2 * logVal2)); 
		double res2 = (weight2/ ttlCount)* (-(prob3 * logVal3) - (prob4 *logVal4)); 
		entropy = res1 + res2;
		return entropy;

	}

	// Fetch the feature to be selected while creating decision tree nodes
	public Feature fetchSignificantFeature(ArrayList<Example> examples, int targetCol, HashSet<String> featuresUnused)
	{
		double minEntropy = Double.MAX_VALUE;

		Feature resFeature = null;
		for(int k=0; k<Feature.featureNames.length ; k++)
		{
		
			if(k != targetCol && featuresUnused.contains(Feature.featureNames[k]))
			{
				int val = k;
				if(Feature.isNumerical[k])
				{

					List<Double> distinctVal =examples.stream().map(x->x.numericals[val]).distinct().collect(Collectors.toList());
					for(Double num : distinctVal)
					{
						double entropy = calculateEntropy(examples, k, num, targetCol);
						if(entropy < minEntropy)
						{
							minEntropy = entropy;
							resFeature = new Feature(k, num);

						}
					}


				}
				else
				{
					List<String> distinctVal =examples.stream().map(x-> x.strings[val]).distinct().collect(Collectors.toList());

					for(String str : distinctVal)
					{
						double entropy = calculateEntropy(examples, k, str, targetCol);
						if(entropy < minEntropy)
						{
							minEntropy = entropy;
							resFeature = new Feature(k, str);
						}
					}

				}


			}
		}

		return resFeature;
	}

	// make decision based on the target values in the example set while creating leaf nodes
	public int checkForDecision(ArrayList<Example> examples, int targetCol)
	{
		List<String> res= examples.stream().map(x->x.strings[targetCol]).distinct().collect(Collectors.toList());

		if(res.size() == 1 && res.get(0).equals("1"))
		{
			return 1;
		}
		else if(res.size() == 1 && res.get(0).equals("0"))
		{
			return 0;
		}
		else
		{
			return 2;
		}
	}

	// This constructor should create the whole decision tree recursively.
	DecisionTree(ArrayList<Example> examples) {
		HashSet<String> featuresUnused =  new HashSet<>();
		int targetCol=0;
		if(examples.isEmpty())
		{
			feature = null;
			yesBranch = null;
			noBranch = null;
			decision = calculatePluralityValue(examples, targetCol);
			return;
		}
		for(int i =0; i<Feature.featureNames.length; i++)
		{
			if(Feature.featureNames[i].equals("Target"))
			{
				targetCol=i;
			}
		}

		for(String str : Feature.featureNames)
		{
			featuresUnused.add(str);
		}

		if(featuresUnused.size() == 2)
		{
			feature = null;
			yesBranch = null;
			noBranch = null;
			decision = calculatePluralityValue(examples, targetCol);
			return;
		}
		if(featuresUnused.size() == 1 && examples.size() >0 )
		{
			feature = null;
			yesBranch = null;
			noBranch = null;
			decision = calculatePluralityValue(examples, targetCol);
			return;
		}

		if(checkForDecision(examples, targetCol) ==  YES)
		{
			feature = null;
			yesBranch = null;
			noBranch = null;
			decision = true;
			return;

		}
		else if(checkForDecision(examples,targetCol) == NO)
		{
			feature = null;
			yesBranch = null;
			noBranch = null;
			decision = false;
			return;

		}
		else
		{
			feature= fetchSignificantFeature(examples, targetCol, featuresUnused);
			
			if(feature == null){
				feature = null;
				yesBranch = null;
				noBranch = null;
				decision = calculatePluralityValue(examples, targetCol);
				return;
			}
			double chiSquareVal = fetchChiSquareValue(examples, targetCol, feature.featureNum);
			if(PRUNE)
			{
				if(chiSquareVal >= CHI_THRESH)
				{
//					ArrayList<Example> yesExmpl;
//					ArrayList<Example> noExmpl;
//					if(Feature.isNumerical[feature.featureNum])
//					{
//						yesExmpl = (ArrayList<Example>) examples.stream().filter(x->x.numericals[feature.featureNum] < feature.dvalue).collect(Collectors.toList());
//						noExmpl = (ArrayList<Example>) examples.stream().filter(x->x.numericals[feature.featureNum] >= feature.dvalue).collect(Collectors.toList());
//					}
//					else
//					{
//
//						yesExmpl = (ArrayList<Example>) examples.stream().filter(x->x.strings[feature.featureNum].equals(feature.svalue)).collect(Collectors.toList());
//						noExmpl = (ArrayList<Example>) examples.stream().filter(x->!x.strings[feature.featureNum].equals(feature.svalue)).collect(Collectors.toList());
//
//					}
//
//					noBranch = new DecisionTree(noExmpl, targetCol, featuresUnused, examples);
//					featuresUnused.remove(Feature.featureNames[feature.featureNum]);
//					yesBranch = new DecisionTree(yesExmpl, targetCol, featuresUnused, examples);
					CreateBranchesForDecisionTree(this, examples, featuresUnused, targetCol, feature.featureNum);

				}
				else
				{
					decision = calculatePluralityValue(examples, targetCol);
					feature = null;
					yesBranch = null;
					noBranch = null;
					return;
				}
			}
			else
			{
				CreateBranchesForDecisionTree(this, examples, featuresUnused, targetCol, feature.featureNum);

			}
			
		}
	}

	// Parameterized constructor used to build the decision tree
	DecisionTree(ArrayList<Example> examples, int targetCol, HashSet<String> featuresUnused, ArrayList<Example> parentExample)
	{

		if(examples.isEmpty())
		{
			feature = null;
			yesBranch = null;
			noBranch = null;
			decision = calculatePluralityValue(parentExample, targetCol);
			return;
		}

		if(featuresUnused.size() == 2)
		{

			feature = null;
			yesBranch = null;
			noBranch = null;
			decision = calculatePluralityValue(examples, targetCol);
			return;
		}

		if(featuresUnused.size() == 1 && examples.size() >0 )
		{
			feature = null;
			yesBranch = null;
			noBranch = null;
			decision = calculatePluralityValue(examples, targetCol);
			return;
		}
		if(checkForDecision(examples, targetCol) ==  YES)
		{

			feature = null;
			yesBranch = null;
			noBranch = null;
			decision = true;
			return;

		}
		else if(checkForDecision(examples,targetCol) == NO)
		{

			feature = null;
			yesBranch = null;
			noBranch = null;
			decision = false;
			return;
		}
		else
		{
			feature= fetchSignificantFeature(examples, targetCol, featuresUnused);
			if(feature == null){
				feature = null;
				yesBranch = null;
				noBranch = null;
				decision = calculatePluralityValue(examples, targetCol);
				return;
			}


			double chiSquareVal = fetchChiSquareValue(examples, targetCol, feature.featureNum);
			
			if(PRUNE)
			{
				if(chiSquareVal >= CHI_THRESH)
				{
					CreateBranchesForDecisionTree(this, examples, featuresUnused, targetCol, feature.featureNum);
				}
				else
				{
					decision = calculatePluralityValue(examples, targetCol);
					feature = null;
					yesBranch = null;
					noBranch = null;
					return;
				}
			}
			else
			{
				CreateBranchesForDecisionTree(this, examples, featuresUnused, targetCol, feature.featureNum);
			
			}
		}
	}
	
	public DecisionTree CreateBranchesForDecisionTree(DecisionTree dt, ArrayList<Example> examples, HashSet<String> featuresUnused, int targetCol, int featureNum)
	{
		ArrayList<Example> yesExmpl;
		ArrayList<Example> noExmpl;
		if(Feature.isNumerical[feature.featureNum])
		{
			yesExmpl = (ArrayList<Example>) examples.stream().filter(x->x.numericals[feature.featureNum] < feature.dvalue).collect(Collectors.toList());
			noExmpl = (ArrayList<Example>) examples.stream().filter(x->x.numericals[feature.featureNum] >= feature.dvalue).collect(Collectors.toList());
		}
		else
		{
			yesExmpl = (ArrayList<Example>) examples.stream().filter(x->x.strings[feature.featureNum].equals(feature.svalue)).collect(Collectors.toList());
			noExmpl = (ArrayList<Example>) examples.stream().filter(x->!x.strings[feature.featureNum].equals(feature.svalue)).collect(Collectors.toList());

		}
		
		int distinctValCnt;
		if(Feature.isNumerical[featureNum])
		{
			distinctValCnt = (int)examples.stream().map(x->x.numericals[featureNum]).distinct().count();
//			System.out.println(distinctValCnt);
		}
		else
		{
			distinctValCnt = (int)examples.stream().map(x->x.strings[featureNum]).distinct().count();
//			System.out.println(distinctValCnt);
		}
		
//		for(int i=0; i< Feature.featureNames.length; i++)
//		{
//			distinctValCnt = (int)examples.stream().map(x->x.strings[featureNum]).distinct().count();
//			System.out.println("distinct feature value count for "+ feature.featureNames[featureNum] + distinctValCnt);
//		}
		
		
		
		@SuppressWarnings("unchecked")
		HashSet<String> noBranchUnusedList = (HashSet<String>) featuresUnused.clone();
		if(distinctValCnt == 1)
		{
			noBranchUnusedList.remove(Feature.featureNames[featureNum]);
		}
		
		System.out.println(noBranchUnusedList);
		dt.noBranch = new DecisionTree(noExmpl, targetCol, noBranchUnusedList, examples);
		featuresUnused.remove(Feature.featureNames[feature.featureNum]);
		
		
		dt.yesBranch = new DecisionTree(yesExmpl, targetCol, featuresUnused, examples);
		return dt;
	}


	// calculating plurality value for the example set

	public boolean calculatePluralityValue(ArrayList<Example> examples, int targetCol)
	{
		long trueCnt = examples.stream().filter(x->x.strings[targetCol].equals("1")).count();
		long falseCnt = examples.stream().filter(x->x.strings[targetCol].equals("0")).count();
		return (trueCnt >= falseCnt);
	}

	// calculate the chi-square value for the string features
	public double calculateChiSquareValueForString(ArrayList<Example> examples, int targetCol, List<String> distinctFeatureVal, int selectedFeature)
	{
		double[] proTarget = new double[2];
		double ttlYesCount = (double) examples.stream().filter(x->x.target).count();
		double ttlNoCount = (double) examples.stream().filter(x->!x.target).count();
		double ttlExmplCnt = (double) examples.size();
		proTarget[1] = ttlYesCount/ ttlExmplCnt;
		proTarget[0] = ttlNoCount/ ttlExmplCnt;
		double[] proFeatures =  new double[distinctFeatureVal.size()];
		double chiSquareVal = 0;

		for(int k=0; k<distinctFeatureVal.size(); k++ )
		{
			int val =k;
			double featureCnt = (double)examples.stream().filter(x-> x.strings[selectedFeature].equals(distinctFeatureVal.get(val))).count();
			proFeatures[k] = featureCnt/ttlExmplCnt;
		}
		for(int i=0; i< distinctFeatureVal.size(); i++)
		{

			for(int j=0; j<2; j++)
			{   
				int val =i;
				double observedVal;
				if(j==0)
				{
					observedVal = examples.stream().filter(x->x.strings[selectedFeature].equals(distinctFeatureVal.get(val)) && !x.target).count();
				}
				else
				{
					observedVal = examples.stream().filter(x->x.strings[selectedFeature].equals(distinctFeatureVal.get(val)) && x.target).count();
				}
				double expectedVal = proTarget[j] * proFeatures[i] * ttlExmplCnt;
				if(expectedVal == 0)
				{
					chiSquareVal += 0;
				}
				else
				{
					chiSquareVal += ((observedVal - expectedVal) * (observedVal - expectedVal)) / expectedVal;
				}

			}

		}	
		
		return chiSquareVal;
	}


	
	// calculating the chi-square value for the double values
	public double calculateChiSquareValueForDouble(ArrayList<Example> examples, int targetCol, List<Double> distinctFeatureVal, int selectedFeature)
	{
		double[] proTarget = new double[2];
		double ttlYesCount = (double) examples.stream().filter(x->x.target).count();
		double ttlNoCount = (double) examples.stream().filter(x->!x.target).count();
		double ttlExmplCnt = (double) examples.size();
		proTarget[1] = ttlYesCount/ ttlExmplCnt;
		proTarget[0] = ttlNoCount/ ttlExmplCnt;
		double[] proFeatures =  new double[distinctFeatureVal.size()];
		double chiSquareVal = 0;
		for(int k=0; k<distinctFeatureVal.size(); k++ )
		{
			int val =k;
			double featureCnt = (double)examples.stream().filter(x-> x.numericals[selectedFeature] == (distinctFeatureVal.get(val))).count();			
			proFeatures[k] = featureCnt/ttlExmplCnt;
		}
		for(int i=0; i< distinctFeatureVal.size(); i++)
		{
			for(int j=0; j<2; j++)
			{   
				int val =i;
				double observedVal;
				if(j==0)
				{
					observedVal = examples.stream().filter(x->x.numericals[selectedFeature] == (distinctFeatureVal.get(val)) && !x.target).count();
				}
				else
				{
					observedVal = examples.stream().filter(x->x.numericals[selectedFeature] == (distinctFeatureVal.get(val)) && x.target).count();
				}
				double expectedVal = proTarget[j] * proFeatures[i] * ttlExmplCnt;
				if(expectedVal == 0)
				{
					chiSquareVal += 0;
				}
				else
				{
					chiSquareVal += ((observedVal - expectedVal) * (observedVal - expectedVal)) / expectedVal;
				}
			}

		}	
		
		return chiSquareVal;
	}
	// Fetching Chi-square value for a selected feature in the example set
	public double fetchChiSquareValue(ArrayList<Example> examples, int targetCol, int selectedFeature)
	{
		Double chiSquareVal;
		if(Feature.isNumerical[selectedFeature])
		{
			List<Double> distinctFeatureVal = examples.stream().map(x->x.numericals[selectedFeature]).distinct().collect(Collectors.toList());
			chiSquareVal = calculateChiSquareValueForDouble(examples, targetCol, distinctFeatureVal, selectedFeature);
		}
		else
		{
			List<String> distinctFeatureVal = examples.stream().map(x->x.strings[selectedFeature]).distinct().collect(Collectors.toList());
			chiSquareVal = calculateChiSquareValueForString(examples, targetCol, distinctFeatureVal, selectedFeature);	
		}
		return chiSquareVal;
	}


	public static class Results {
		public int true_positive;  // correctly classified "yes"
		public int true_negative;  // correctly classified "no"
		public int false_positive; // incorrectly classified "yes," should be "no"
		public int false_negative; // incorrectly classified "no", should be "yes"

		public Results() {
			true_positive = 0;
			true_negative = 0;
			false_positive = 0;
			false_negative = 0;
		}

		public String toString() {
			String out = "Precision: ";
			out += String.format("%.4f", true_positive/(double)(true_positive + false_positive));
			out += "\nRecall: " + String.format("%.4f",true_positive/(double)(true_positive + false_negative));
			out += "\n";
			out += "Accuracy: ";
			out += String.format("%.4f", (true_positive + true_negative)/(double)(true_positive + true_negative + false_positive + false_negative));
			out += "\n";
			return out;
		}
	}
	// Used to classify the examples 
	public Results classify(ArrayList<Example> examples) {
		Results results = new Results();
		for(Example ex : examples)
		{
			Boolean decisionRes = getDecision(ex, this);
			if(ex.target && decisionRes)  results.true_positive++;
			else if(ex.target && !decisionRes) results.false_negative++;
			else if(!ex.target && decisionRes) results.false_positive++;
			else if(!ex.target && !decisionRes) results.true_negative++;
		}

		return results;
	}

	// traversing the decision tree to fetch the decision for the given example
	public boolean getDecision(Example ex, DecisionTree dt)
	{
		if(dt.feature == null)
		{
			return dt.decision;
		}
		else if(dt.feature.apply(ex))
		{
			return getDecision(ex, dt.yesBranch);
		}
		else
		{
			return getDecision(ex, dt.noBranch);
		}	
	}

	public String toString() {
		return toString(0);
	}

	// Print the decision tree as a set of nested if/else statements.
	// This is a little easier than trying to print with the root at the top.
	public String toString(int depth) {
		String out = "";
		for (int i = 0; i < depth; i++) {
			out += "    ";
		}
		if (feature == null) {
			out += (decision ? "YES" : "NO");
			out += "\n";
			return out;
		}
		out += "if " + feature + "\n";
		out += yesBranch.toString(depth+1);
		for (int i = 0; i < depth; i++) {
			out += "    ";
		}
		out += "else\n";
		out += noBranch.toString(depth+1);
		return out;
	}

}
