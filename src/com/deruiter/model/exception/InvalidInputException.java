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
 * Model exception class for invalid user input Boolean expression exceptions.
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/26/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.model.exception;

public class InvalidInputException extends Exception
{
    // Class variable
	private static final long serialVersionUID = 1L;

    // Instance variable
	private String errMessage;

	/**
	 * Constructor for an invalid input exception.
	 *
	 * @param errMessage
	 * 			the error message to display for the exception detailing what is wrong with the Boolean expression.
	 */
	public InvalidInputException(String errMessage)
	{
		this.errMessage = errMessage;
	}

	/**
	 * Retrieves the detailed error message.
	 *
	 * @return the detailed error message.
	 */
	@Override
	public String getMessage()
	{
		return errMessage;
	}
}
