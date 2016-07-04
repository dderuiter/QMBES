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
 * Model exception class for a distributive property exceptions.
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/26/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.model.exception;

public class DistributivePropertyException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * Retrieves the error message for the exception.
	 *
	 * @return the error message.
	 */
	@Override
	public String getMessage()
	{
		return "Maximum number of terms for distributive property exceeded.";
	}

}
