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
 * Model class for a group.
 * A group contains term(s) and represents a set of term(s) at each step of minimization.
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/26/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.model.group;

import com.deruiter.model.group.term.Term;
import com.deruiter.utilities.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Group
{
    // Instance variable
	private List<Term> terms;

    // Class variable
	private static List<Term> primes = new ArrayList<Term>(0);

	/**
	 * Constructor for a group.
	 */
	public Group()
	{
		terms = new ArrayList<Term>(0);
	}

	/**
	 * Adds a term to the group.
	 *
	 * @param term
	 * 			the term to add to this group.
	 */
	public void addTerm(Term term)
	{
		terms.add(term);
	}

	/**
	 * Retrieves a term from the group at specified index.
	 *
	 * @param targetIndex
	 * 			the index in the group of the term to retrieve.
	 * @return the term from the group at the specified index.
	 */
	public Term getTerm(int targetIndex)
	{
		return terms.get(targetIndex);
	}

	/**
	 * Retrieves all terms from a group.
	 *
	 * @return all terms from a group.
	 */
	public List<Term> getTerms()
	{
		return terms;
	}

	/**
	 * Retrieves the total number of terms in the group.
	 *
	 * @return the total number of terms in the group.
	 */
	public int getSize()
	{
		return terms.size();
	}

	/**
	 * Retrieves the sum-of-products in bit form.
	 *
	 * @return the sum-of-products in bit form.
	 */
	public String getSumOfProductsBitForm()
	{
		String listOfTerms = Constants.EMPTY_STRING;
		Iterator<Term> itr = terms.iterator();

		while(itr.hasNext())
		{
			listOfTerms += itr.next().getBitForm();
			if(itr.hasNext())
			{
				listOfTerms += " + ";
			}
		}
		return listOfTerms;
	}

	/**
	 * Retrieves the sum-of-products in regular form.
	 *
	 * @return the sum-of-products in regular form.
	 */
	public String getSumOfProductsRegularForm()
	{
		String listOfTerms = Constants.EMPTY_STRING;
		Iterator<Term> itr = terms.iterator();

		while(itr.hasNext())
		{
			listOfTerms += itr.next().getRegularForm();
			if(itr.hasNext())
			{
				listOfTerms += " + ";
			}
		}
		return listOfTerms;
	}

	/**
	 * Sorts the terms in a group according to the number of 1's in corresponding bit strings.
	 * Top (Largest # of 1's) to Bottom (Smallest # of 1's)
	 * (Sorting Algorithm: Selection Sort)
	 */
	public void sort()
	{
		 int i, j, first;
		 Term temp;
	     for ( i = terms.size() - 1; i > 0; i-- )
	     {
	          first = 0; // Initialize to subscript of first element
	          for(j = 1; j <= i; j ++) // Locate smallest element between positions 1 and i
	          {
	               if( terms.get(j).getBinaryNumber() > terms.get(first).getBinaryNumber() )
	                 first = j;
	          }
	          temp = terms.get(first); // Swap smallest found with element in position i
	          terms.set(first, terms.get(i));
	          terms.set(i, temp);
	      }
	}

	/**
	 * Assigns the every term in the group an ID which corresponds to their position in the group.
	 */
	public void assignTermIDs()
	{
		for(int i = 0; i < terms.size(); i++)
		{
			terms.get(i).setId(String.valueOf(i));
		}
	}

	/**
	 * Retrieves a String representation of the Group.
	 *
	 * @return a String representation of the Group.
	 */
	@Override
	public String toString()
	{
		String listOfTerms = Constants.EMPTY_STRING;
		Iterator<Term> itr = terms.iterator();

		while(itr.hasNext())
		{
			Term term = itr.next();
			listOfTerms += String.format("%-15s%15s%20s",
					term.getId(), term.getRegularForm(), term.getBitForm());
			if(itr.hasNext())
			{
				listOfTerms += "\n";
			}
		}
		return listOfTerms;
	}

	/**
	 * Determines if the group contains a specific term.
	 *
	 * @param targetTerm
	 * 			the term to look for in the group.
	 * @return whether the group contains the term.
	 */
	public boolean contains(Term targetTerm)
	{
		Iterator<Term> itr = terms.iterator();
		while(itr.hasNext())
		{
			if(itr.next().equals(targetTerm))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Retrieves all the primes in the group (those terms not used to form simplified terms).
	 *
	 * @return  all the primes in the group.
	 */
	public static List<Term> getPrimes()
	{
		return primes;
	}

	/**
	 * Minimizes a group.
	 *
	 * @param originalGroup
	 * 			the original group to minimize.
	 * @return the minimized group.
	 */
	public static Group minimize(Group originalGroup)
	{
		Group minimizedGroup = new Group();

		// Iterate through all terms in original group except for last
		// (last term has already been compared to all previous terms by the time it is reached)
		for(int i = 0; i < originalGroup.getSize() - 1; i++)
		{
			Term curTerm = originalGroup.getTerm(i);

			// Iterate through remaining terms in group after current term
			for(int j = i + 1; j < originalGroup.getSize(); j++)
			{
				Term nextTerm = originalGroup.getTerm(j);
				if(curTerm.isCombinable(nextTerm))
				{
					Term combinedTerm = Term.combine(curTerm, nextTerm);

					// Check if group already contains term
					if(!minimizedGroup.contains(combinedTerm))
					{
						// Term not in group already so add to group
						minimizedGroup.addTerm(combinedTerm);
						primes.add(combinedTerm);
					}

					// Mark terms as used in minimization so they won't be added as primes later
					curTerm.markAsUsed();
					nextTerm.markAsUsed();

					// Remove terms that were used to form a new prime
					primes.remove(curTerm);
					primes.remove(nextTerm);
				}
			}
		}

		// Iterate through all terms in original group (looking for additional primes)
		for(int i = 0; i < originalGroup.getSize(); i++)
		{
			Term curTerm = originalGroup.getTerm(i);

			// Check if term was not used in minimization
			if(!curTerm.wasUsed())
			{
				// Check that prime list does not already contain current term
				if(!primes.contains(curTerm))
				{
					primes.add(curTerm);
				}
			}
		}

		return minimizedGroup;
	}

	/**
	 * Retrieves a list of reordered primes.
	 * Ordering is based on when they were added to the group.
	 * (Re-ordering done for testing purposes, not needed but also does not really harm anything!)
	 *
	 * @return a list of reordered primes.
	 */
	public static List<Term> getReorderedPrimes()
	{
		List<Term> reorderedPrimes = new ArrayList<Term>();

		// Loop through all primes and find add new prime at correctly ordered position
		for(int i = 0; i < primes.size(); i++)
		{
			boolean added = false;

			// Loop through all already reordered primes
			for(int j = 0; j < reorderedPrimes.size(); j++)
			{
				// Check if new prime ID length longer than current prime ID length
				if(primes.get(i).getId().split(",").length > reorderedPrimes.get(j).getId().split(",").length)
				{
					reorderedPrimes.add(j, primes.get(i)); // Found position
					added = true;
					break;
				}
			}

			// Check if current prime already added in its sorted position
			if(!added)
			{
				reorderedPrimes.add(primes.get(i)); // Add to end of list
			}
		}

		return reorderedPrimes;
	}
}