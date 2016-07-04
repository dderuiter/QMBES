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
 * Model class for a prime implicant chart.
 * A prime implicant chart lists all prime terms and the original columns (or minterms) that are covered by the
 * corresponding prime. A minterm that is covered by a prime is marked with a circle.
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/26/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.model.report;

import com.deruiter.model.group.term.Term;
import com.deruiter.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

public class PrimeImplicantChart
{
    // Instance Variables
	private List<Integer> ids;
	private List<Term> primes;
	private List<Term> essentialPrimes;
	private char[][] primeGrid;

    // Class Variables
	public static final char gridPosFilledSymbol = 'o';

	/**
	 * Constructor for a prime implicant chart.
	 *
	 * @param originalTerms
	 * 			the original terms parsed from the original Boolean expression.
	 * @param primes
	 * 			the primes found by the Quine McCluskey algorithm.
     *
	 */
	public PrimeImplicantChart(List<Term> originalTerms, List<Term> primes)
	{
		this.ids = new ArrayList<Integer>();
		this.primes = primes;
		this.essentialPrimes = new ArrayList<Term>(0);

		primeGrid = new char[primes.size()][originalTerms.size()];
		createGrid();

        // Set initial id's
		for(int i = 0; i < originalTerms.size(); i++)
		{
			ids.add(i);
		}
	}

	/**
	 * Constructor for a prime implicant chart.
	 *
	 * @param optionalPrimes
	 * 			- the optional primes found by the Quine McCluskey algorithm.
	 * @param essentialPrimes
	 * 			- the essential primes found by the Quine McCluskey algorithm.
	 * @param primeGrid
	 * 			- the prime grid formed by the Quine McCluskey algorithm.
	 * @param ids
	 * 			- a set of all unique ID's for the terms which will be included in this
	 * 			  prime implicant chart.
	 */
	public PrimeImplicantChart(List<Term> optionalPrimes, List<Term> essentialPrimes, char[][] primeGrid,
			List<Integer> ids)
	{
		this.primes = optionalPrimes;
		this.essentialPrimes = essentialPrimes;
		this.primeGrid = primeGrid;
		this.ids = ids;
	}

	/**
	 * Creates a prime implicant chart grid.
	 * Grid is a 2D array and contains an special symbol in a cell if the term corresponding
	 * to the current row covers the current column (which represents an original term)
	 */
	public void createGrid()
	{
        // Local Variables
		Term curPrime = null; // Initialize (just need to initialize to something)
		int numTermsCoveringColumn = 0;
		List<String> column = new ArrayList<String>(0);

		// Iterate through columns
		for(int col = 0; col < primeGrid[0].length; col++)
		{
			// Iterate through rows
			for(int row = 0; row < primeGrid.length; row++)
			{
				curPrime = primes.get(row);
				if(curPrime.containsId(col))
				{
					primeGrid[row][col] = gridPosFilledSymbol;

					// Found term covering current column so increment counter
					numTermsCoveringColumn++;
				}
				else
				{
					primeGrid[row][col] = ' ';
				}
				column.add(String.valueOf(primeGrid[row][col]));
			}

			// Check if essential prime found
			if(numTermsCoveringColumn == 1)
			{
				// Extract essential prime from current column
				Term essentialPrime = primes.get(column.indexOf(Constants.EMPTY_STRING + gridPosFilledSymbol));

				// Verify essential prime not already stored
				if(!essentialPrimes.contains(essentialPrime))
				{
					essentialPrimes.add(essentialPrime);
				}
			}

			numTermsCoveringColumn = 0; // Reset for next column
			column.clear();
		}
	}

	/**
	 * Retrieves a set of all unique ID's for the terms in the chart.
	 *
	 * @return a set of all unique ID's for the terms in the chart.
	 */
	public List<Integer> getIDs()
	{
		return ids;
	}

	/**
	 * Retrieves all the primes in chart.
	 *
	 * @return all the primes in chart.
	 */
	public List<Term> getPrimes()
	{
		return primes;
	}

	/**
	 * Retrieves all the essential primes in chart.
	 *
	 * @return all the essential primes in chart.
	 */
	public List<Term> getEssentialPrimes()
	{
		return essentialPrimes;
	}

	/**
	 * Retrieves the prime grid.
	 *
	 * @return the prime grid.
	 */
	public char[][] getPrimeGrid()
	{
		return primeGrid;
	}

	/**
	 * Retrieves a String representation of the prime implicant chart.
	 *
	 * @return a String representation of the prime implicant chart.
	 */
	@Override
	public String toString()
	{
		// Determine max ID field size
		int maxIdFieldSize = -1;
		for(Integer id : ids)
		{
			if(id.toString().length() > maxIdFieldSize)
			{
				maxIdFieldSize = id.toString().length();
			}
		}

		String chart = String.format("%-40s", Constants.EMPTY_STRING);
		for(int i = 0; i < ids.size(); i++)
		{
			chart += String.format("%" + maxIdFieldSize + "d ", ids.get(i));
		}
		chart += "\n";

		// Iterate through rows
		for(int row = 0; row < primeGrid.length; row++)
		{
			chart += String.format("%-10s", primes.get(row).getRegularForm());
			chart += String.format("%25s", primes.get(row).getId());
			chart += String.format("%5s", Constants.EMPTY_STRING);

			// Iterate through columns
			for(int col = 0; col < primeGrid[0].length; col++)
			{
				chart += String.format("%" + maxIdFieldSize + "s ", primeGrid[row][col]);
			}
			chart += "\n";
		}

		return chart;
	}
}
