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
 * Model class for a Boolean expression parser.
 * A parser determines forms the data to use in the Quine McCluskey algorithm from the specified Boolean expression.
 * Also, a parser sanitizes a Boolean expression.
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/26/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.model.input;

import com.deruiter.model.exception.InvalidInputException;
import com.deruiter.model.group.Group;
import com.deruiter.model.group.term.OriginalTerm;
import com.deruiter.model.group.term.Term;
import com.deruiter.model.group.term.literal.Literal;
import com.deruiter.utilities.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Parser
{
	private String expression;
	private List<Literal> allLiterals;

	/**
	 * Constructor for a parser.
	 *
	 * @param expression
	 * 			a Boolean expression to parse.
	 */
	public Parser(String expression)
	{
		this.expression = expression;
		allLiterals = new ArrayList<>();

		determineLetters();
	}

	/**
	 * Determines all the unique letters in an expression.
	 */
	private void determineLetters()
	{
		Literal curLiteral;
		String modifiedExpression = expression;

		modifiedExpression = modifiedExpression.replace("+", Constants.EMPTY_STRING);
		modifiedExpression = modifiedExpression.replace("'", Constants.EMPTY_STRING);
		modifiedExpression = modifiedExpression.replace(" ", Constants.EMPTY_STRING);

		for(int i = 0; i < modifiedExpression.length(); i++)
		{
			curLiteral = new Literal(Character.toString(modifiedExpression.charAt(i)));
			if(Term.containsLiteral(curLiteral, allLiterals))
			{
				continue;
			}
			else
			{
				allLiterals.add(curLiteral);
			}
		}
		Collections.sort(allLiterals);
	}

	/**
	 * Creates a group for the Quine McCluskey algorithm to minimize.
	 *
	 * @return a group for the Quine McCluskey algorithm to minimize.
	 */
	public Group createGroup()
	{
        // Local variables
		char curChar = '\u0000';
		String curTerm = Constants.EMPTY_STRING;
		Group group = new Group();

		for(int i = 0; i < expression.length(); i++)
		{
			curChar = expression.charAt(i);
			if(curChar == ' ')
			{
				continue;
			}
			else if(curChar == '+')
			{
				group.addTerm(new OriginalTerm(curTerm, allLiterals));
				curTerm = Constants.EMPTY_STRING;
			}
			else
			{
				curTerm += curChar;
			}
		}

		group.addTerm(new OriginalTerm(curTerm, allLiterals)); // Flush out last term in expression
		group.sort(); // Sort group according to bit strings
		group.assignTermIDs();

		return group;
	}

	/**
	 * Checks the user's input Boolean expression for invalid tokens or general errors.
	 *
	 * @param inputBoolExpression
	 * 			the Boolean expression input by the user.
	 * @return a valid Boolean expression.
	 * @throws InvalidInputException
	 */
	public static String checkInputForErrors(String inputBoolExpression) throws InvalidInputException
	{
		inputBoolExpression = inputBoolExpression.trim();

		// Check if empty Boolean expression
		if(inputBoolExpression.isEmpty())
		{
			throw new InvalidInputException("Illegal empty Boolean expression.");
		}

		// Loop through all characters in the Boolean expression
		for(int i = 0; i < inputBoolExpression.length(); i++)
		{
			char curChar = inputBoolExpression.charAt(i);

			// Check if current character is valid
			if(Character.isLowerCase(curChar) || curChar == ' ' || curChar == '+' || curChar == '\'')
			{
				continue; // Valid character found so proceed to next
			}
			// Check if current character is an upper case letter
			else if(Character.isUpperCase(curChar))
			{
				throw new InvalidInputException("Illegal character: Uppercase Letter");
			}
			// Check if current character is a digit
			else if(Character.isDigit(curChar))
			{
				throw new InvalidInputException("Illegal character: Digit");
			}
			// Check if current character is a parenthesis
			else if(curChar == '(' || curChar == ')')
			{
				throw new InvalidInputException("Illegal character: Parenthesis");
			}
			else // Character is invalid
			{
				throw new InvalidInputException("Illegal character in Boolean expression.");
			}
		}

		// Check if 1st character is NOT a lower case letter
		if(!Character.isLowerCase(inputBoolExpression.charAt(0)))
		{
			throw new InvalidInputException("Illegal first character in Boolean expression.");
		}

		// Check if last character is NOT a lower case letter or apostrophe
		if(!Character.isLowerCase(inputBoolExpression.charAt(inputBoolExpression.length() - 1)) &&
				inputBoolExpression.charAt(inputBoolExpression.length() - 1) != '\'')
		{
			throw new InvalidInputException("Illegal last character in Boolean expression.");
		}

		// Check if expression contains a double negation
		if(inputBoolExpression.contains("''"))
		{
			throw new InvalidInputException("Illegal double negation found in Boolean expression.");
		}

		String temp = inputBoolExpression.replace(" ", Constants.EMPTY_STRING);
		boolean plusFound = false;

		// Loop through all characters in white space removed Boolean expression
		for(int i = 0; i < temp.length(); i++)
		{
			char curChar = temp.charAt(i);

			// Check if addition operators with no term in between exist
			if(plusFound && curChar == '+')
			{
				throw new InvalidInputException("Illegal addition operators with no term in between.");
			}
			else
			{
				// Reset
				plusFound = false;
			}

			// Check if end of term reached
			if(curChar == '+')
			{
				plusFound = true;
			}
		}

		String[] terms = inputBoolExpression.replace(" ", Constants.EMPTY_STRING).split("\\+");

		// Loop through all terms in the Boolean expression
		for(int i = 0; i < terms.length; i++)
		{
			for(int j = i + 1; j < terms.length; j++)
			{
				for(int k = 0; k < terms[i].length(); k++)
				{
					String term = terms[i].substring(k + 1);
					String literal = terms[i].substring(k, k + 1);
					if(term.contains(literal) && !literal.equals("'"))
					{
						throw new InvalidInputException("Illegal duplicate literal found in term in Boolean Expression.");
					}
				}
				if(terms[i].equals(terms[j]))
				{
					throw new InvalidInputException("Illegal duplicate term found in Boolean Expression.");
				}
			}
		}

		return inputBoolExpression;
	}
}