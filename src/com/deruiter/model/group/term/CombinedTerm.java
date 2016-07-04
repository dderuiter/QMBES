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
 * Model class for a combined term.
 * Combined terms are a combination of two previous terms that could be minimized to a single term.
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/26/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.model.group.term;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CombinedTerm extends Term
{
	private List<String> associatedIds;

	/**
	 * Constructor for a combined term.
	 */
	CombinedTerm()
	{
		super();
		associatedIds = new ArrayList<String>(0);
	}

	/**
	 * Sets the ID by adding to the current ID.
	 *
	 * @param id
	 * 			- the row number(s) for the term in the original minterm table to add.
	 */
	@Override
	public void setId(String id)
	{
		String associatedId;
		int startIndex = 0;

		// Iterate through comma delimited id string and add individual id's to list
		for(int i = 0; i < id.length(); i++)
		{
			char curChar = id.charAt(i);

			// Parse and add next id to list if comma reached
			if(curChar == ',')
			{
				associatedId = id.substring(startIndex, i);
				startIndex = i + 1; // Move index to one space after last comma found
				associatedIds.add(associatedId);
			}
		}

		// Flush out remaining id
		associatedId = id.substring(startIndex);
		associatedIds.add(associatedId);

		// Sort the associated id's
		Collections.sort(associatedIds);
	}

	/**
	 * Retrieves the ID.
	 *
	 * @return the ID for the term which is comprised of all rows numbers (which represent
	 * 		   original minterms) that this minterm covers.
	 */
	@Override
	public String getId()
	{
		String commaDelimitedIds = "";
		Iterator<String> itr = associatedIds.iterator();

		while(itr.hasNext())
		{
			commaDelimitedIds += itr.next();
			if(itr.hasNext())
			{
				commaDelimitedIds += ",";
			}
		}

		return commaDelimitedIds;
	}
}
