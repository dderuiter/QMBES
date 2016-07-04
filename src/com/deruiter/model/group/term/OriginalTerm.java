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
 * Model class for an original term.
 * An original term is one parsed initially from the Boolean expression and has not been minimized (or simplified) at
 * all.
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/26/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.model.group.term;

import com.deruiter.model.group.term.literal.Literal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OriginalTerm extends Term
{
	// Instance variables
	private String id;
	private String rawTerm;
	private List<Literal> allLiterals;

    /**
     * Constructor for an Original Term
     *
     * @param rawTerm
     *          the raw term.
     * @param allLiterals
     *          the literals that comprise the term.
     */
	public OriginalTerm(String rawTerm, List<Literal> allLiterals)
	{
		super();
		this.rawTerm = rawTerm;
		this.allLiterals = allLiterals;
		createTerm();
	}

	/**
	 * Sets the initial ID.
	 *
	 * @param id
	 * 			the row number (ID) for the term in the original minterm table.
	 */
	@Override
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * Gets the intial ID.
	 *
	 * @return the row number (ID) for the term in the original minterm table.
	 */
	@Override
	public String getId()
	{
		return id;
	}

	/**
	 * Creates an original term.
	 */
	private void createTerm()
	{
		int termLength = rawTerm.length();
		char curChar = '\u0000';

		for(int i = 0; i < termLength; i++)
		{
			curChar = rawTerm.charAt(i);

			// Check if next char is an apostrophe
			if( ((i + 1) != termLength) && rawTerm.charAt(i + 1) == '\'')
			{
				addLiteral(new Literal(Character.toString(curChar), "0"));
				i++; // Skip next apostrophe character
			}
			else
			{
				addLiteral(new Literal(Character.toString(curChar), "1"));
			}
		}

		// Add all missing literals to term
		addMissingLiterals();
	}

	/**
	 * Adds all missing literals to the term.
	 * (Missing literals are represented by a dash "-".)
	 */
	private void addMissingLiterals()
	{
		List<Literal> literalsAdded = new ArrayList<Literal>();

		// Add missing literals
		for(int i = 0; i < allLiterals.size(); i++)
		{
			Literal literal = allLiterals.get(i);
			if(Term.containsLiteral(literal, getLiterals()) == false)
			{
				Literal missingLiteral = new Literal(literal.getLetter());
				addLiteral(missingLiteral); // Add missing literal to term
				literalsAdded.add(missingLiteral); // Add missing literal to term
			}
		}

		// Sort all literals in term
		Collections.sort(getLiterals());

		// Replace new literals with dashes
		for(int i = 0; i < literalsAdded.size(); i++)
		{
			literalsAdded.get(i).eliminate();
		}
	}
}
