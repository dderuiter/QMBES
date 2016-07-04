/**********************************************************************************************************************
 *
 * WARNING:
 * Copyright Ⓒ 2016 by D.DeRuiter
 * Do not use, modify, or distribute in any way without express written consent.
 *
 * PROJECT:
 * QMBES (Quine McCluskey Boolean Expression Simplifier)
 *
 * DESCRIPTION:
 * Model class for implementing Petrick's method.
 * Petrick's method is a technique for finding all minimum sum-of-products solutions from a prime implicant chart.
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/30/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.model.algorithm;

import com.deruiter.model.exception.DistributivePropertyException;
import com.deruiter.model.group.term.Term;
import com.deruiter.model.report.PrimeImplicantChart;
import com.deruiter.utilities.Constants;
import javafx.beans.property.DoubleProperty;

import java.util.ArrayList;
import java.util.List;

public class PetricksMethod
{
    // Instance variables
	private char[][] primeGrid;
	private List<Term> primes;
	private List<Term> essentialPrimes;
	private List<Term> optionalPrimes;
	private PrimeImplicantChart reducedPrimeImplicantChart;
	private DoubleProperty prop_WorkDone;

	/**
	 * Constructor for Petrick's method.
	 *
	 * @param primeGrid
     *          the prime grid to use for running Petrick's method.
	 * @param primes
	 * 			the primes found by the Quine McCluskey algorithm.
	 * @param essentialPrimes
	 * 			the essential primes found by the Quine McCluskey algorithm.
	 * @param prop_WorkDone
     *          the total work done for the algorithm.
	 */
	public PetricksMethod(char[][] primeGrid, List<Term> primes, List<Term> essentialPrimes,
                          DoubleProperty prop_WorkDone)
	{
		this.primeGrid = primeGrid;
		this.primes = primes;
		this.essentialPrimes = essentialPrimes;
		this.prop_WorkDone = prop_WorkDone;

		optionalPrimes = new ArrayList<Term>(0);
		optionalPrimes.addAll(primes);
		optionalPrimes.removeAll(essentialPrimes);
	}

	/**
	 * Runs Petrick's method.
	 *
	 * @param initialSolution
	 * 		    the initial solution (contains essential primes if any exist).
	 * @return a list containing the final minimized Boolean expression(s).
	 * @throws DistributivePropertyException
	 */
	public List<String> runMethod(String initialSolution) throws DistributivePropertyException
	{
        // Local variables
		List<String> finalSolutions;
		List<Integer> essentialPrimeColumns = determineEssentialPrimeColumns();
		List<Integer> optionalPrimeRows = determineOptionalPrimeRows();

		// Check if optional prime rows exist and if so run the rest of Petrick's method
		if(optionalPrimeRows.size() > 0)
		{
			char[][] reducedPrimeGrid = formReducedPrimeGrid(essentialPrimeColumns, optionalPrimeRows);
			if(reducedPrimeGrid != null)
			{
				String logicFunction = formLogicFunction(reducedPrimeGrid);
				String distributedFun = applyDistributiveProperty(logicFunction);
				distributedFun = distributedFun.substring(1, distributedFun.length() - 1);
				String[] simplifiedProducts = distributedFun.split("\\+");

				finalSolutions = findFinalSolutions(initialSolution, simplifiedProducts);

				return finalSolutions;
			}
		}

		// No point in running Petrick's method because either all primes are essential or
        // essential primes cover all columns
		finalSolutions = new ArrayList<String>();

        // Only need essential primes in final solution
		finalSolutions.add(initialSolution);

		return finalSolutions;
	}

	/**
	 * Determines the columns (minterms) covered by essential prime implicants.
	 *
	 * @return a list of essential prime column numbers.
	 */
	private List<Integer> determineEssentialPrimeColumns()
	{
        // Local variable
		List<Integer> essentialPrimeColumns = new ArrayList<Integer>(0);

		// Find essential prime columns
		for(int i = 0; i < essentialPrimes.size(); i++)
		{
			String[] ids = essentialPrimes.get(i).getId().split(",");

            // Loop through all ID's in current essential prime
			for(int idIndex = 0; idIndex < ids.length; idIndex++)
			{
				// Verify essential id not already stored
				if(!essentialPrimeColumns.contains(Integer.valueOf(ids[idIndex])))
				{
					essentialPrimeColumns.add(Integer.valueOf(ids[idIndex]));
				}
			}
		}

		return essentialPrimeColumns;
	}

	/**
	 * Determines the row numbers for optional primes.
	 *
	 * @return a list of optional prime row numbers.
	 */
	private List<Integer> determineOptionalPrimeRows()
	{
        // Local variable
		List<Integer> optionalPrimeRows = new ArrayList<Integer>(0);

		// Iterate through all rows (primes)
		for(int rowIndex = 0; rowIndex < primes.size(); rowIndex++)
		{
			// Check if essential prime row
			if(essentialPrimes.contains(primes.get(rowIndex)))
			{
                // Skip row
				continue;
			}
			else
			{
				// Optional prime row so add row number
				optionalPrimeRows.add(rowIndex);
			}
		}

		return optionalPrimeRows;
	}

	/**
	 * Prints the prime implication chart without columns covered by essential primes.
	 *
	 * @param essentialPrimeColumns
	 * 		    the essential prime column numbers.
	 * @param optionalPrimeRows
	 * 		    the optional prime row numbers.
	 * @return the reduced prime implicant grid.
	 */
	private char[][] formReducedPrimeGrid(List<Integer> essentialPrimeColumns, List<Integer> optionalPrimeRows)
	{
        // Local variables
		List<Integer> ids = new ArrayList<>(0);
        List<Term> optionalPrimes = new ArrayList<>(0);
		char reducedPrimeGrid[][] =
				new char[optionalPrimeRows.size()][primeGrid[0].length - essentialPrimeColumns.size()];
		int newGridCurCol = 0;
		int newGridCurRow = 0;

		// Iterate through columns (skipping columns covered by essential primes)
		for(int col = 0; col < primeGrid[0].length; col++)
		{
			// Check if column is covered by an essential prime
			if(essentialPrimeColumns.contains(col))
			{
                // Skip column
				continue;
			}

			// Iterate through rows
			for(int row = 0; row < primeGrid.length; row++)
			{
				// Check if current row is an optional prime row
				if(optionalPrimeRows.contains(row))
				{
					reducedPrimeGrid[newGridCurRow][newGridCurCol] = primeGrid[row][col];
					newGridCurRow++;
				}
			}
            // Add column not covered by an essential prime
			ids.add(col);

            // Reset
			newGridCurRow = 0;

            // Increment
			newGridCurCol++;
		}

        // Store optional primes
		for(int i = 0; i < primes.size(); i++)
		{
			if(optionalPrimeRows.contains(i))
			{
				optionalPrimes.add(primes.get(i));
			}
		}

		// Check if essential primes cover all columns
		if(reducedPrimeGrid[0].length == 0)
		{
            // This represents that only essential primes are needed
			return null;
		}
		else
		{
			// Create the reduced prime implication chart
			reducedPrimeImplicantChart =
					new PrimeImplicantChart(optionalPrimes, essentialPrimes, reducedPrimeGrid, ids);

			return reducedPrimeGrid;
		}
	}

	/**
	 * Forms the logic function.
	 *
	 * The rows of the reduced chart are labeled R1, R2, R3, etc. and form a logic function P that is true when all
     * columns are covered. P consists of a product of sum terms - one for each column with at least one "x".
	 *
	 * @param reducedPrimeGrid
	 * 			the reduced prime grid.
	 * @return the logic function.
	 */
	private String formLogicFunction(char[][] reducedPrimeGrid)
	{
		String logicFunction = Constants.EMPTY_STRING;

		// Iterate through columns (skipping columns covered by essential primes)
		for(int col = 0; col < reducedPrimeGrid[0].length; col++)
		{
			// Add opening parenthesis
			logicFunction += "(";

			// Iterate through rows
			for(int row = 0; row < reducedPrimeGrid.length; row++)
			{
				// Check if optional prime covers column
				if(reducedPrimeGrid[row][col] == PrimeImplicantChart.gridPosFilledSymbol)
				{
                    // Label row
					String label = "R" + row;
					optionalPrimes.get(row).setLabel(label);

                    // Add label and plus sign
					logicFunction += label + "+";
				}
			}

			// Remove trailing plus sign and add closing parenthesis
			logicFunction = logicFunction.substring(0, logicFunction.length() - 1) + ")";
		}

		return logicFunction;
	}

	/**
	 * Applies the distributive property to the equation.
	 *
	 * @param p
     *          the logic function.
	 * @return the distributed equation.
     * @throws DistributivePropertyException
	 */
	private String applyDistributiveProperty(String p) throws DistributivePropertyException
	{
		// Leave commented code if needed for performance optimization later
//		long start = System.currentTimeMillis();

        // Local variables
		float curSubExpressionNum = 1;
		float totalSubExpressionCount = p.length() - p.replace(")", Constants.EMPTY_STRING).length();
		float totalTerms = calculateTotalTerms(p);

		// Check if too many terms
		if(totalTerms < 0)
		{
			throw new DistributivePropertyException();
		}

		// Apply the distributive property to the logic function
		while(p.substring(p.indexOf(")") + 1).indexOf(")") != -1)
		{
			String sum1 = p.substring(p.indexOf("(") + 1, p.indexOf(")"));
			p = p.substring(p.indexOf(")") + 1); // Delete first sum from expression

			String sum2 = p.substring(p.indexOf("(") + 1, p.indexOf(")"));
			p = p.substring(p.indexOf(")") + 1); // Delete second sum from expression

			String[] sum1Addends = sum1.split("\\+");
			String[] sum2Addends = sum2.split("\\+");

			float curWorkFracCompleted = curSubExpressionNum / totalSubExpressionCount;
			float workFracToComplete =
					((curSubExpressionNum + 1.0f) / totalSubExpressionCount) - curWorkFracCompleted;

			p = multiplyTogether(sum1Addends, sum2Addends, curWorkFracCompleted, workFracToComplete) + p;
			prop_WorkDone.setValue(curSubExpressionNum++ / totalSubExpressionCount);
		}

		// Leave commented code if needed for performance optimization later
//		long end = System.currentTimeMillis();
//		System.out.println("Distribute Duration: " + (end - start) + " ms");

		return p;
	}

	/**
	 * Calculates the total number of terms in a distributed function.
	 *
	 * @param expression
	 * 			the expression to determine how many terms will result from applying the distributive property to this
     * 			expression.
	 * @return the total number of terms after distributing the terms in the expression.
	 */
    private float calculateTotalTerms(String expression)
	{
        // Local variables
    	float maxTermsAllowed = Float.MAX_VALUE;
		int curTermCount = 0;
		long totalTermCount = 1;

		// Loop through all characters in expression
		for(int i = 0; i < expression.length(); i++)
		{
			char curChar = expression.charAt(i);

			// Check if multiplying two positives resulted in a neg. value which means max. value exceeded
			// or
			// Check if total term count exceeds the max. allowed
			if(totalTermCount < 0 || totalTermCount > maxTermsAllowed)
			{
				return -1; // Flag problem as too complex to solve
			}
			// Check if beginning of sub-expression found
			else if(curChar == '(')
			{
				curTermCount = 0;
			}
			// Check if end of sub-expression found
			else if(curChar == ')')
			{
				curTermCount++; // Add one to make up for last term which does not have "+" following it
				totalTermCount *= curTermCount;
			}
			// Check if plus addition operator found because this means term found right before
			else if(curChar == '+')
			{
				curTermCount++;
			}
			else // Space character
			{
				continue;
			}
		}

		return totalTermCount;
	}

	/**
	 * Uses the distributive property to multiply two summation expressions.
	 *
	 * @param sum1Addends
	 *			the addens contained within the first set of parenthesis in the expression.
	 * @param sum2Addends
	 * 			the addens contained within the second set of parenthesis in the expression.
	 * @param curWorkFracCompleted
	 * 			the current overall fraction of work completed.
	 * @param workFractToComplete
	 * 			the fraction of work to be completed by this multiplication.
	 * @return the result of using the distributive property on two expressions
	 */
	private String multiplyTogether(String[] sum1Addends, String[] sum2Addends, float curWorkFracCompleted,
                                    float workFractToComplete)
	{
        // Local variables
		String result = Constants.EMPTY_STRING;
		float newPercentage = curWorkFracCompleted;
		float progressIncrementVal = workFractToComplete / (sum1Addends.length * sum2Addends.length);

		// Loop through all labels in first summation expression
		for(int i = 0; i < sum1Addends.length; i++)
		{
			// Loop through all labels in second summation expression
			for(int j = 0; j < sum2Addends.length; j++)
			{
				result += sum1Addends[i] + sum2Addends[j] + "+";
				newPercentage += progressIncrementVal;
				prop_WorkDone.setValue(newPercentage);
			}
		}

		String distributedExpression = result.substring(0, result.length() - 1);
		distributedExpression = simplifyDistributedExpression(distributedExpression);
		result = "(" + distributedExpression + ")";

		return result;
	}

	/**
	 * Simplifies a distributed expression using 4 rules.
	 *
	 * @param expression
	 * 			- the expression (after the distributive property has been applied to it)
	 * 			  to simplify.
	 * @return the simplified expression.
	 */
	private String simplifyDistributedExpression(String expression)
	{
		String[] addends = expression.split("\\+");

		// Loop through all unsimplified addends
		for(int i = 0; i < addends.length; i++)
		{
			// Use rule #1: XX = X Rule
			addends[i] = removeDuplicates(addends[i]);
		}

		// Loop through all addends and apply simplification rules #2, #3, and #4
		for(int i = 0; i < addends.length; i++)
		{
			// Loop through all addends except for addends already simplified
			for(int j = i + 1; j < addends.length; j++)
			{
				// Check if already marked for deletion
				if(addends[i].equals("DELETE"))
				{
					continue; // Skip to boost performance
				}
				// Check for rule #2: X + X = X
				else if(addends[i].equals(addends[j]))
				{
					addends[j] = "DELETE";
				}
				// Check for rule #3: XY + X = X
				else if(addends[i].contains(addends[j]))
				{
					addends[i] = "DELETE";
				}
				// Check for rule #4: X + XY = X
				else if(addends[j].contains(addends[i]))
				{
					addends[j] = "DELETE";
				}
			}
		}

		String simplifiedExpression = Constants.EMPTY_STRING;

		// Loop through addends and delete those marked for deletion
		for(int i = 0; i < addends.length; i++)
		{
			// Check if addend marked for elimination
			if(addends[i].equals("DELETE"))
			{
				continue;
			}
			else // Keep addend
			{
				simplifiedExpression += addends[i] + "+";
			}
		}

		// Remove trailing addition (i.e. "+") operator
		simplifiedExpression = simplifiedExpression.substring(0, simplifiedExpression.length() - 1);

		return simplifiedExpression;
	}

	/**
	 * Removes duplicate labels within an addend.
	 * (Uses rule #1: XX = X)
	 *
	 * @param product
	 * 			- the addend from which to remove duplicate labels.
	 * @return the product without duplicate labels.
	 */
	private String removeDuplicates(String product)
	{
		String[] labelNums = product.split("R");

		// Loop through labels and skip first because it is empty string
		for(int i = 1; i < labelNums.length; i++)
		{
			// Loop through labels minus the first (which is really the 2nd because of empty string)
			for(int j = i + 1; j < labelNums.length; j++)
			{
				// Check if labels are equal
				if(labelNums[i].equals(labelNums[j]))
				{
					labelNums[j] = "DUPLICATE"; // Mark duplicate
				}
			}
		}

		String newProduct = Constants.EMPTY_STRING;

		// Loop through all label numbers and each unique label number to solution
		for(int i = 1; i < labelNums.length; i++)
		{
			// Check if duplicate number
			if(labelNums[i].equals("DUPLICATE"))
			{
				continue; // Skip to next
			}
			else
			{
				newProduct += "R" + labelNums[i]; // Add unique number
			}
		}

		return newProduct;
	}

	/**
	 * Finds all solutions with minimum number of terms and literals.
	 *
	 * @param initialSolution
	 * 			- the initial solution (containing any essential primes).
	 * @param simplifiedProducts
	 * 			- the simplified products (addends) from the distributed logic function.
	 * @return all solutions with minimum number of terms and literals.
	 */
	private List<String> findFinalSolutions(String initialSolution, String[] simplifiedProducts)
	{
		List<String> shortestOptionalProducts = extractShortestProducts(simplifiedProducts);

		// Substitute labels back for actual boolean terms to compose final solution(s)
		List<String> possibleSolutions = new ArrayList<String>();
		for(int i = 0; i < shortestOptionalProducts.size(); i++)
		{
			String possibleSolution = Constants.EMPTY_STRING;

			// Check if final solution contains any essential primes
			if(!initialSolution.isEmpty())
			{
				// Add essential primes to final solution
				possibleSolution += initialSolution + " + ";
			}

			String optionalSolution = shortestOptionalProducts.get(i);
			int totalLabelCount = countOccurrences(optionalSolution, 'R');

			// Loop through all labels in each solution/product
			for(int curLabelIndex = 0; curLabelIndex < totalLabelCount; curLabelIndex++)
			{
				String label = Constants.EMPTY_STRING;
				int nextLabelStartIndex = optionalSolution.indexOf('R', 1);

				// Check if multiple labels in current solution
				if(nextLabelStartIndex != -1)
				{
					label = optionalSolution.substring(0, nextLabelStartIndex); // Skip 1st 'R' so 2nd 'R' can be used to find the end of label
					optionalSolution = optionalSolution.substring(nextLabelStartIndex); // Delete label from solution
				}
				else
				{
					label = optionalSolution; // Only 1 label in solution so entire solution is label
				}

				// Loop through all optional primes to find which corresponds to current label
				for(int curOpPrimeIndex = 0; curOpPrimeIndex < optionalPrimes.size(); curOpPrimeIndex++)
				{
					Term optionalPrime = optionalPrimes.get(curOpPrimeIndex);
					if(label.equals(optionalPrime.getLabel()))
					{
						possibleSolution += optionalPrime.getRegularForm() + " + ";
					}
				}
			}

			possibleSolution = possibleSolution.substring(0, possibleSolution.length() - 3);
			possibleSolution = possibleSolution.replace("-", Constants.EMPTY_STRING);
			possibleSolutions.add(possibleSolution);
		}

		List<String> finalSolutions = getSolutionsWithFewestLiterals(possibleSolutions);

		return finalSolutions;
	}

	/**
	 * Extracts the shortest products (addends) from the logic function.
	 *
	 * @param simplifiedProducts
	 * 			- the simplified products (addends) from the distributed logic function.
	 * @return the products with the least number of labels.
	 */
	private List<String> extractShortestProducts(String[] simplifiedProducts)
	{
		int minLength = Integer.MAX_VALUE;

		// Loop through products and determine length of shortest product
		// (i.e. one with least number of labels)
		for(int i = 0; i < simplifiedProducts.length; i++)
		{
			// Check if new minimum product length found
			if(simplifiedProducts[i].length() < minLength)
			{
				 // Store new minimum length
				minLength = simplifiedProducts[i].length();
			}
		}

		// Create list with reduced solution set
		// (only contains solutions which have the least # of labels).
		List<String> shortestProducts = new ArrayList<String>();
		for(int i = 0; i < simplifiedProducts.length; i++)
		{
			if(simplifiedProducts[i].length() > minLength)
			{
				continue;
			}
			else
			{
				shortestProducts.add(simplifiedProducts[i]);
			}
		}
//		System.out.println("Min. Label Sols: " + shortestProducts);

		return shortestProducts;
	}

	/**
	 * Retrieves the solutions with the fewest number of total literals.
	 *
	 * @param possibleSolutions
	 * 			- the possible solutions.
	 * @return the solutions with the fewest number of total literals.
	 */
	private List<String> getSolutionsWithFewestLiterals(List<String> possibleSolutions)
	{
        // Local variables
		List<String> finalSolutions = new ArrayList<String>();
		int minLength = Integer.MAX_VALUE;

		// Loop through all possible solutions and find the one with fewest total literals
		for(int i = 0; i < possibleSolutions.size(); i++)
		{
			if(possibleSolutions.get(i).length() < minLength)
			{
				 // Store new minimum length
				minLength = possibleSolutions.get(i).length();
			}
		}

		// Loop through all possible solutions and add those with fewest total literal count
		// to final solution list
		for(int i = 0; i < possibleSolutions.size(); i++)
		{
			if(possibleSolutions.get(i).length() == minLength)
			{
				// Store final solution
				finalSolutions.add(possibleSolutions.get(i));
			}
		}

		return finalSolutions;
	}

	/**
	 * Counts all occurrences of the target character in the specified string.
	 *
	 * @param searchStr
	 * 			the string to search
	 * @param targetChar
	 * 			the target character for which to count all occurrences.
	 * @return the number of occurrences of the target character in the string or zero if no matches are found.
	 */
	private int countOccurrences(String searchStr, char targetChar)
	{
        // Local variable
		int occurences = 0;

        // Loop through all characters in string
		for(int i = 0; i < searchStr.length(); i++ )
		{
            // Check if target character found
		    if(searchStr.charAt(i) == targetChar)
		    {
		        occurences++;
		    }
		}

		return occurences;
	}

	/**
	 * Retrieves the reduced prime implicant chart.
	 *
	 * @return the reduced prime implicant chart.
	 */
	public PrimeImplicantChart getReducedPrimeImplicantChart()
	{
		return reducedPrimeImplicantChart;
	}
}
