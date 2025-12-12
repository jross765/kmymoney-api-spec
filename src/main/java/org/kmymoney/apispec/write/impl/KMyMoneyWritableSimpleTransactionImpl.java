package org.kmymoney.apispec.write.impl;

import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.impl.KMyMoneyWritableTransactionImpl;
import org.kmymoney.apispec.read.impl.KMyMoneySimpleTransactionImpl;
import org.kmymoney.apispec.write.KMyMoneyWritableSimpleTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;

/**
 * xyz.
 * 
 * @see KMyMoneySimpleTransactionImpl
 */
public class KMyMoneyWritableSimpleTransactionImpl extends KMyMoneyWritableTransactionImpl 
                                                   implements KMyMoneyWritableSimpleTransaction 
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyWritableSimpleTransactionImpl.class);

    // -----------------------------------------------------------

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public KMyMoneyWritableSimpleTransactionImpl(final KMyMoneyTransactionImpl trx) {
    	super(trx);
    }

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public KMyMoneyWritableSimpleTransactionImpl(final KMyMoneyWritableTransactionImpl trx) {
    	super(trx);
    }

    // ---------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     */
    public KMyMoneyWritableTransactionSplit getWritableFirstSplit() throws TransactionSplitNotFoundException {
    	if ( getSplits().size() == 0 )
    		throw new TransactionSplitNotFoundException();
	
    	return (KMyMoneyWritableTransactionSplit) getFirstSplit();
    }
    
    /**
     * {@inheritDoc}
     */
    public KMyMoneyWritableTransactionSplit getWritableSecondSplit()  throws TransactionSplitNotFoundException {
		if ( getSplits().size() <= 1 )
			throw new TransactionSplitNotFoundException();

    	return (KMyMoneyWritableTransactionSplit) getSplits().get(1);
    }

    // ---------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     */
	@Override
	public KMyMoneyTransactionSplit getFirstSplit() throws TransactionSplitNotFoundException
	{
    	if ( getSplits().size() == 0 )
    		throw new TransactionSplitNotFoundException();
	
    	return getSplits().get(0);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public KMyMoneyTransactionSplit getSecondSplit() throws TransactionSplitNotFoundException
	{
		if ( getSplits().size() <= 1 )
			throw new TransactionSplitNotFoundException();

		return getSplits().get(1);
	}

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("KMyMoneyWritableSimpleTransactionImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", balance=");
		buffer.append(getBalanceFormatted());

		buffer.append(", memo='");
		buffer.append(getMemo() + "'");

		buffer.append(", split1=");
		try {
			buffer.append(getFirstSplit().getID());
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", split2=");
		try {
			buffer.append(getSecondSplit().getID());
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", date-posted=");
		try {
			buffer.append(getDatePosted().format(DATE_POSTED_FORMAT));
		} catch (Exception e) {
			buffer.append(getDatePosted().toString());
		}

		buffer.append(", date-entered=");
		try {
			buffer.append(getDateEntered().format(DATE_ENTERED_FORMAT));
		} catch (Exception e) {
			buffer.append(getDateEntered().toString());
		}

		buffer.append("]");

		return buffer.toString();
    }

}
