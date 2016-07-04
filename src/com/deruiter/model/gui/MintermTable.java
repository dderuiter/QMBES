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
 * Model class for a minterm table.
 * A minterm table contains all table rows that make up a minterm table.
 * (Used for displaying information to GUI).
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/26/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.model.gui;

import java.util.ArrayList;
import java.util.List;

public class MintermTable
{
    // Instance variable
	private List<MintermTableRow> rows;

	/**
	 * Default constructor for a minterm table.
	 */
	public MintermTable()
	{
		rows = new ArrayList<MintermTableRow>();
	}

	/**
	 * Adds a row to the minterm table.
	 *
	 * @param row
	 * 			- the row to add to this minterm table.
	 */
	public void addRow(MintermTableRow row)
	{
		rows.add(row);
	}

	/**
	 * Retrieves all rows in the minterm table.
	 *
	 * @return all rows in this minterm table.
	 */
	public List<MintermTableRow> getRows()
	{
		return rows;
	}
}
