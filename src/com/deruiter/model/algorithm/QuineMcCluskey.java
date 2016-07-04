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
 * Model class for implementing the Quine-McCluskey algorithm.
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/26/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.model.algorithm;

import com.deruiter.model.exception.DistributivePropertyException;
import com.deruiter.model.group.Group;
import com.deruiter.model.group.term.Term;
import com.deruiter.model.input.Parser;
import com.deruiter.model.report.PrimeImplicantChart;
import com.deruiter.utilities.Print;
import com.deruiter.utilities.Constants;
import javafx.beans.property.DoubleProperty;

import java.util.*;

public class QuineMcCluskey
{
	// Class variables
	public static List<Integer> steps = new ArrayList<>();
	public static List<Group> groups = new ArrayList<>();
	public static PrimeImplicantChart primeChart;
	public static PrimeImplicantChart reducedPrimeChart;
	public static boolean inDebugMode = false;

	/**
	 * Runs the Quine McCluskey algorithm and Petrick's method on a specified Boolean expression.
	 *
	 * @param funcToMinimize
	 * 			the Boolean expression to attempt to simplify.
	 * @param prop_WorkDone
	 * 			the current progress of the algorithm.
	 * @return the solutions representing maximally simplified Boolean expressions.
	 * @throws DistributivePropertyException
	 */
	public static List<String> runAlgorithm(String funcToMinimize, DoubleProperty prop_WorkDone)
			throws DistributivePropertyException
	{
		resetForNewRun();

		if(funcToMinimize.length() == 0)
		{
			if(inDebugMode) System.out.println("Boolean expression is empty.");
			return new ArrayList<>(0); // Exit early and return empty list
		}

		Parser parser = new Parser(funcToMinimize);
		Group group = parser.createGroup();

		// Store original terms for creation of prime implication chart
		List<Term> originalTerms = group.getTerms();

		// Minimize terms until no longer possible
		int step = 1;
		do
		{
			// Print out step #
			if(inDebugMode) System.out.println("Step " + String.valueOf(step));

			// Print out group
			if(inDebugMode) Print.printGroup(group);

			groups.add(group);
			steps.add(step);
			step++;
		} while((group = Group.minimize(group)).getSize() > 0);

		// Print out prime terms
		List<Term> primes = Group.getReorderedPrimes();
		if(inDebugMode) Print.printPrimes(primes);

		if(inDebugMode) System.out.println();

		// Create Prime Implication Chart
		primeChart = new PrimeImplicantChart(originalTerms, primes);

		// Print Prime Implication Chart
		if(inDebugMode) System.out.println();
		if(inDebugMode) System.out.println("Prime Implication Chart:");
		if(inDebugMode) System.out.println("========================");
		if(inDebugMode) Print.printGrid(primeChart);

		// Store essential primes
		char[][] primeGrid = primeChart.getPrimeGrid();
		List<Term> essentialPrimes = primeChart.getEssentialPrimes();

		String initialSolution = Constants.EMPTY_STRING;

		// Loops through all essential primes and store them in final solution
		for(int i = 0; i < essentialPrimes.size(); i++)
		{
			initialSolution += essentialPrimes.get(i).getRegularForm() + " + ";
		}

		// Check if any essential primes exist
		// (ensures that for loop ran before attempting to remove last " + " from the String
		if(essentialPrimes.size() > 0)
		{
			initialSolution = initialSolution.substring(0, initialSolution.length() - 3);
			initialSolution.replace("-", Constants.EMPTY_STRING);
		}

		// Run Petrick's Method
		PetricksMethod petricksMethod = new PetricksMethod(primeGrid, primes, essentialPrimes, prop_WorkDone);

		List<String> finalSolutions = petricksMethod.runMethod(initialSolution);
		reducedPrimeChart = petricksMethod.getReducedPrimeImplicantChart();

		if(inDebugMode) System.out.println();
		if(inDebugMode) System.out.println("Petrick's Method - Reduced Prime Implication Chart:");
		if(inDebugMode) System.out.println("===================================================");

		// Check if prime implicant chart could be simplified
		if(reducedPrimeChart != null)
		{
			// Print the reduced prime implication chart
			if(inDebugMode) Print.printGrid(reducedPrimeChart);
		}
		else
		{
			if(inDebugMode) System.out.println("Chart could not be reduced.");
		}

		// Print out all minimal solutions
		if(inDebugMode) System.out.println();
		for(int i = 0; i < finalSolutions.size(); i++)
		{
			if(inDebugMode) System.out.println("Minimal Solution: " + finalSolutions.get(i));
		}

		return finalSolutions;
	}

	/**
	 * Resets static variables for a new run.
	 */
	private static void resetForNewRun()
	{
		steps.clear();
		groups.clear();
		Group.getPrimes().clear();
	}

	/**
	 * Retrieves the total number of steps it took for minimization.
	 *
	 * @return the total number of steps it took for minimization.
	 */
	public static List<Integer> getSteps()
	{
		return steps;
	}

	/**
	 * Retrieves the minterm groups formed by the algorithm.
	 *
	 * @return the minterm groups formed by the algorithm.
	 */
	public static List<Group> getGroups()
	{
		return groups;
	}

	/**
	 * Retrieves the prime implicant chart.
	 *
	 * @return the prime implicant chart.
	 */
	public static PrimeImplicantChart getPrimeImplicantChart()
	{
		return primeChart;
	}

	/**
	 * Retrieves the reduced prime implicant chart.
	 *
	 * @return the reduced prime implicant chart.
	 */
	public static PrimeImplicantChart getReducedPrimeImplicantChart()
	{
		return reducedPrimeChart;
	}

	/**
	 * Generates a random Boolean expression.
	 *
	 * @param numOfLiterals
	 * 			the number of literals for each term in the random Boolean expression.
	 * @param numOfTerms
	 * 			the number of terms that make up the random Boolean expression.
	 * @return a random Boolean expression.
	 */
	public static String getRandomExpression(int numOfLiterals, int numOfTerms)
	{
		// Local variables
		char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
						  'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
						  'u', 'v', 'w', 'x', 'y', 'z'};
		String randomExpression = Constants.EMPTY_STRING;

		// Check if maximum # of literals exceeded
		if(numOfLiterals > 26 || numOfLiterals == 0)
		{
			return Constants.EMPTY_STRING;
		}

		int maxTermCount = (int)Math.pow(2, numOfLiterals);
		if(inDebugMode) System.out.println("Possible Num. of Term Combinations: " + maxTermCount);
		String[] possibleTerms = new String[maxTermCount];

		// Generate all possible terms
		for(int termNum = 0; termNum < maxTermCount; termNum++)
		{
			String binaryRepresentation = Integer.toBinaryString(termNum);

			// Loop through binary number and add missing 0's
			while(binaryRepresentation.length() < numOfLiterals)
			{
				binaryRepresentation = "0" + binaryRepresentation;
			}

			String literalRepresentation = Constants.EMPTY_STRING;

			// Loop through binary representation change bits to literals
			for(int i = 0; i < binaryRepresentation.length(); i++)
			{
				literalRepresentation += letters[i];

				// Check if literal should be complement
				if(binaryRepresentation.charAt(i) == '0')
				{
					literalRepresentation += "'";
				}
			}

			possibleTerms[termNum] = literalRepresentation;
		}

		// Generate random # of terms (Leave code - might need in future for Debugging)
//		int numOfTerms = ThreadLocalRandom.current().nextInt(0, maxTermCount + 1);
//		int numOfTerms = ThreadLocalRandom.current().nextInt(0, 21); // Range: 0 to 20
		if(inDebugMode) System.out.println("Num. of Terms: " + numOfTerms);

		// Generate set of random numbers identifying which terms to use
		Random rng = new Random(); // Ideally just create one instance globally
		Set<Integer> generated = new LinkedHashSet<Integer>(); // Use LinkedHashSet to maintain insertion order
		while (generated.size() < numOfTerms)
		{
		    Integer next = rng.nextInt(maxTermCount);

		    // As we're adding to a set, this will automatically do a containment check
		    generated.add(next);
		}

		Iterator<Integer> itr = generated.iterator();

		// Loop through all terms in random boolean expression
		for(int termNum = 0; termNum < numOfTerms; termNum++)
		{
			randomExpression += possibleTerms[itr.next()];

			// Verify NOT on last term
			if(termNum != (numOfTerms - 1))
			{
				randomExpression += " + ";
			}
		}

		return randomExpression;
	}
}
