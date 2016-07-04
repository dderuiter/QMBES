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
 * Utility class for printing stuff to the console for debugging purposes.
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/26/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.utilities;

import com.deruiter.model.group.Group;
import com.deruiter.model.group.term.Term;
import com.deruiter.model.report.PrimeImplicantChart;

import java.util.List;

public class Print
{
	/**
	 * Prints a group as a table to the console.
	 *
	 * @param group
	 * 			the group to print out to the console as a table.
	 */
	public static void printGroup(Group group)
	{
		System.out.printf("%-15s%15s%20s\n", "ID", "Term", "Bits");
		System.out.println("==================================================");
		System.out.println(group + "\n");
	}

	/**
	 * Prints a set of primes to the console as a table.
	 *
	 * @param primes
	 * 			the primes to print out to the console as a table.
	 */
	public static void printPrimes(List<Term> primes)
	{
		// Print out prime table header
		System.out.println("Primes");
		System.out.println("==============================");
		for(int i = 0; i < primes.size(); i++)
		{
			Term prime = primes.get(i);
			System.out.printf("%-15s%15s\n", prime.getId(), prime.getRegularForm());
		}
	}

	/**
	 * Prints a prime implicant chart grid to the console.
	 *
	 * @param chart
	 * 			the prime implicant chart to print out to the console.
	 */
	public static void printGrid(PrimeImplicantChart chart)
	{
		System.out.println(chart);
	}
}
