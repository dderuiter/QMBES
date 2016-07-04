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
 * Model abstract class for a term.
 * A term is comprised of literal(s). A Boolean expression is comprised of term(s).
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/26/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.model.group.term;

import com.deruiter.model.group.term.literal.Literal;
import com.deruiter.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

public abstract class Term
{
    // Instance variables
	private List<Literal> literals;
	private boolean wasUsed;
	private String label;

	/**
	 * Default constructor for a term.
	 */
	Term()
	{
		literals = new ArrayList<Literal>();
		wasUsed = false;
	}

	/**
	 * Sets the ID.
	 *
	 * @param id
	 * 			the ID (corresponds to row number(s) in minterm table).
	 */
	public abstract void setId(String id);

	/**
	 * Retrieves the ID.
	 *
	 * @return the ID (corresponds to row number(s) in minterm table).
	 */
	public abstract String getId();

	/**
	 * Retrieves the term in regular form.
	 *
	 * @return regular form of the term. (i.e. a'bc)
	 */
	public String getRegularForm()
	{
		String regularForm = Constants.EMPTY_STRING;
		for(int i = 0; i < literals.size(); i++)
		{
			regularForm += literals.get(i).getLetter();
			if(literals.get(i).isComplement())
			{
				regularForm += "'";
			}
		}
		return regularForm;
	}

	/**
	 * Retrieves the term in bit form.
	 *
	 * @return the term in bit form (i.e. 011)
	 */
	public String getBitForm()
	{
		String bitForm = Constants.EMPTY_STRING;
		for(int i = 0; i < literals.size(); i++)
		{
			bitForm += literals.get(i).getBit();
		}
		return bitForm;
	}

	/**
	 * Adds a literal.
	 *
	 * @param literal
	 * 			the literal to add to this term.
	 */
	public void addLiteral(Literal literal)
	{
		literals.add(literal);
	}

	/**
	 * Retrieves a literal.
	 *
	 * @param index
	 * 			the index of the literal in the term to retrieve.
	 * @return the literal at the specified index position in this term.
	 */
	private Literal getLiteral(int index)
	{
		return literals.get(index);
	}

	/**
	 * Retrieves all literals that comprise this term.
	 *
	 * @return all literals that comprise this term.
	 */
	public List<Literal> getLiterals()
	{
		return literals;
	}

	/**
	 * Retrieves the number of 1's in this term.
	 *
	 * @return the number of 1's in this term.
	 */
	public int getOneCount()
	{
		return getBitForm().replaceAll("0", Constants.EMPTY_STRING).replaceAll("-", Constants.EMPTY_STRING).length();
	}

	/**
	 * Retrieves the binary number representation of this term.
	 *
	 * @return the binary number representation of this term.
	 */
	public int getBinaryNumber()
	{
		int binaryNum = 0;
		int twoPower = 0;
		String bits = getBitForm();

		// Loop through all bits in the binary number
		for(int i = bits.length(); i > 0; i--)
		{
			String parsedBit = bits.substring(i - 1, i);

			// Check if parsed bit is not 1 or 0
			if(parsedBit.equals("-"))
			{
				continue; // Skip since not binary digit
			}

			int curBit = Integer.parseInt(parsedBit);

			// Check if 1 found
			if(curBit == 1)
			{
				binaryNum += (int)Math.pow(2, twoPower);
			}

			twoPower++;
		}

		return binaryNum;
	}

	/**
	 * Retrieves the length (number of literals) in this term.
	 *
	 * @return the length (number of literals) in this term.
	 */
	public int getLength()
	{
		return literals.size();
	}

	/**
	 * Determines whether this term is combinable with another term.
	 *
	 * @param term
	 * 			the term to see if this term is combinable with.
	 * @return whether this term is combinable with the specified term.
	 */
	public boolean isCombinable(Term term)
	{
		if((this.getOneCount() + 1) != term.getOneCount())
		{
			return false;
		}

		boolean differentLiteralFound = false;
		for(int i = 0; i < this.getBitForm().length(); i++)
		{
			String bit1 = this.getLiteral(i).getBit();
			String bit2 = term.getLiteral(i).getBit();

			// Different bit at same position in each term found
			if(!bit1.equals(bit2))
			{
				if(differentLiteralFound == true) // The 2nd different bit
				{
					return false; // More than 1 bit is different so cannot combine terms
				}
				differentLiteralFound = true; // 1 bit is different
			}
		}
		return true;
	}

	/**
	 * Determines whether two terms are equal (must have the same bit forms).
	 *
	 * @param obj
	 * 			the term to see if this term is equal to.
	 * @return whether two terms are equal.
	 */
	@Override
	public boolean equals(Object obj)
	{
		// Check if object compared with itself
		if(this == obj)
		{
			return true;
		}

		// Check if object is instance of Term
		if(!(obj instanceof Term))
		{
			return false;
		}

		// Check if bit forms and id's are equal
		Term t = (Term)obj; // Cast object to Term
		if(this.getBitForm().equals(t.getBitForm()) && this.getId().equals(t.getId()))
		{
			return true;
		}

		return false;
	}

	/**
	 * Marks the term as used (not prime).
	 */
	public void markAsUsed()
	{
		wasUsed = true;
	}

	/**
	 * Determines if the term was used to form a combined (simplified) term.
	 *
	 * @return whether the term is a prime.
	 */
	public boolean wasUsed()
	{
		return wasUsed;
	}

	/**
	 * Determines if the term contains a specified row number (ID) corresponding to
	 * an original term.
	 *
	 * @param targetId
	 * 			the ID for which to check if this term's ID contains.
	 * @return whether this term contains the specified ID.
	 */
	public boolean containsId(int targetId)
	{
		String completeId = getId();
		String curId = Constants.EMPTY_STRING;

		// Loop through all IDs in complete ID
		// (since an entire ID is made up of ID's if combined with another term)
		for(int i = 0; i < completeId.length(); i++)
		{
			// Check if ID separator reached
			if(completeId.charAt(i) == ',')
			{
				if(Integer.valueOf(curId) == targetId)
				{
					return true;
				}
				curId = Constants.EMPTY_STRING;
			}
			else
			{
				curId += completeId.charAt(i);
			}
		}

		// Check if beginning ID equals target ID if no commas or
		// if last term equals target ID if there are commas
		if(Integer.valueOf(curId) == targetId)
		{
			return true;
		}

		return false;
	}

	/**
	 * Sets the label (used in Petrick's method) for this term.
	 *
	 * @param label
	 * 			the label for this term.
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Retrieves the label (used in Petrick's method) for this term.
	 *
	 * @return the label for this term.
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Determines whether the list contains a target literal.
	 *
	 * @param targetLiteral
	 * 			the literal for which to search the list.
	 * @param literals
	 * 			all the literals for the term.
	 * @return whether the literal is already in the list.
	 */
	public static boolean containsLiteral(Literal targetLiteral, List<Literal> literals)
	{
		Literal curLiteral;
		for(int i = 0; i < literals.size(); i++)
		{
			curLiteral = literals.get(i);
			if(curLiteral.getLetter().equals(targetLiteral.getLetter()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Combines two terms together.
	 *
	 * @param term1
	 * 			the first term for combining.
	 * @param term2
	 * 			the second term for combining.
	 * @return the combination of the two specified terms.
	 */
	public static Term combine(Term term1, Term term2)
	{
		Term newTerm = new CombinedTerm();

		for(int i = 0; i < term1.getLength(); i++)
		{
			Literal curLiteral = new Literal(term1.getLiteral(i).getLetter(), term1.getLiteral(i).getBit());

			String term1CurBit = term1.getLiteral(i).getBit();
			String term2CurBit = term2.getLiteral(i).getBit();

			// Different bit at same position in each term found
			if(!term1CurBit.equals(term2CurBit))
			{
				curLiteral.eliminate();
			}
			newTerm.addLiteral(curLiteral);
		}

		String id = String.valueOf(term1.getId()) + "," + String.valueOf(term2.getId());
		((CombinedTerm)newTerm).setId(id);

		return newTerm;
	}

	/**
	 * Retrieves the term's ID and regular form separated by a space.
	 *
	 * @return the term's ID and regular form separated by a space.
	 */
	@Override
	public String toString()
	{
		return getId() + " " + getRegularForm();
	}
}
