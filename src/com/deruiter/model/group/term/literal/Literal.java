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
 * Model class for a literal.
 * Literal(s) are the variable(s) that make up a term.
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/26/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.model.group.term.literal;

import com.deruiter.utilities.Constants;

public class Literal implements Comparable<Object>
{
    // Instance variables
	private String letter;
	private String bit;

	/**
	 * Default constructor for a literal.
	 */
	public Literal()
	{
		letter = Constants.EMPTY_STRING;
		bit = Constants.EMPTY_STRING;
	}

	/**
	 * Constructor for a literal.
	 *
	 * @param letter
	 * 			- the letter associated with this literal.
	 */
	public Literal(String letter)
	{
		this.letter = letter;
		bit = Constants.EMPTY_STRING;
	}

	/**
	 * Constructor for a literal.
	 *
	 * @param letter
	 * 			- the letter associated with this literal.
	 * @param bit
	 * 			- the bit (0 or 1) associated with this literal.
	 */
	public Literal(String letter, String bit)
	{
		this.letter = letter;
		this.bit = bit;
	}

	/**
	 * Determines if the literal is the complement.
	 *
	 * @return whether the literal is the complement.
	 */
	public boolean isComplement()
	{
		if(bit.equals("1") || bit.equals("-"))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * Eliminates a literal.
	 */
	public void eliminate()
	{
		letter = "-";
		bit = "-";
	}

	/**
	 * Retrieves the letter associated with the literal.
	 *
	 * @return the letter associated with the literal.
	 */
	public String getLetter()
	{
		return letter;
	}

	/**
	 * Retrieves the bit (0 or 1) associated with the literal.
	 * @return
	 */
	public String getBit()
	{
		return bit;
	}

	/**
	 * Compares this literal to another literal based upon its associated letter.
	 *
	 * @param o
	 * 			- the literal to which to compare this literal.
	 * @return the result of the comparison.
	 */
	@Override
	public int compareTo(Object o)
	{
		return (this.letter).compareTo(((Literal)o).getLetter());
	}
}
