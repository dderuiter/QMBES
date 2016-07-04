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
 * Model class for a minterm table row.
 * A minterm table row contains all data (ID, Regular Form, Bit Form, and Prime?) for the minterm.
 * (Used for displaying information to the GUI).
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/26/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.model.gui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class MintermTableRow
{
    // Instance variables
	private final SimpleStringProperty termId;
    private final SimpleStringProperty termInRegForm;
    private final SimpleStringProperty termInBitForm;
    private final SimpleBooleanProperty isPrime;

    /**
     * Constructor for a minterm table row.
     * Each row contains the details for a single term.
     *
     * @param termId
     * 			the term's ID.
     * @param termInRegForm
     * 			the term's regular form.
     * @param termInBitForm
     * 			the term's bit form.
     * @param isPrime
     * 			whether the term is a prime.
     */
    public MintermTableRow(String termId, String termInRegForm, String termInBitForm, boolean isPrime)
    {
    	this.termId = new SimpleStringProperty(termId);
    	this.termInRegForm = new SimpleStringProperty(termInRegForm);
    	this.termInBitForm = new SimpleStringProperty(termInBitForm);
    	this.isPrime = new SimpleBooleanProperty(isPrime);
    }

    /**
     * Retrieves the term's ID.
     *
     * @return the term's ID.
     */
    public String getTermId()
    {
    	return termId.getValue();
    }

    /**
     * Retrieves the term's regular form.
     *
     * @return the term's regular form.
     */
    public String getTermInRegForm()
    {
    	return termInRegForm.getValue();
    }

    /**
     * Retrieves the term's bit form.
     *
     * @return the term's bit form.
     */
    public String getTermInBitForm()
    {
    	return termInBitForm.getValue();
    }

    /**
     * Retrieves whether the term is a prime.
     *
     * @return whether the term is a prime.
     */
    public boolean getIsPrime()
    {
    	return isPrime.getValue();
    }
}
