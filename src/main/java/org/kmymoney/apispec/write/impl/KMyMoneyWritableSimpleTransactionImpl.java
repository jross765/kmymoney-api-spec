package org.kmymoney.apispec.write.impl;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.impl.KMyMoneyWritableTransactionImpl;
import org.kmymoney.apispec.read.impl.KMyMoneySimpleTransactionImpl;
import org.kmymoney.apispec.read.impl.TransactionValidationException;
import org.kmymoney.apispec.write.KMyMoneyWritableSimpleTransaction;
import org.kmymoney.base.basetypes.complex.KMMQualifSecCurrID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * xyz.
 * 
 * @see KMyMoneySimpleTransactionImpl
 */
public class KMyMoneyWritableSimpleTransactionImpl extends KMyMoneyWritableTransactionImpl 
                                                   implements KMyMoneyWritableSimpleTransaction 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyWritableSimpleTransactionImpl.class);

	// ---------------------------------------------------------------
    
    private static final int NOF_SPLITS = 2;

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

    // ----------------------------
    
    /**
     * {@inheritDoc}
     */
	@Override
	public FixedPointNumber getAmount() throws TransactionSplitNotFoundException
	{
    	return getSecondSplit().getValue();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public BigFraction getAmountRat() throws TransactionSplitNotFoundException
	{
    	return getSecondSplit().getValueRat();
	}

    // ---------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     */
	@Override
    public void setAmount(FixedPointNumber amt) throws TransactionSplitNotFoundException {
		FixedPointNumber amtNeg = amt.copy().negate(); // Caution: FixedPointNumber is mutable!
		
    	getWritableFirstSplit().setShares(amtNeg);
    	getWritableFirstSplit().setValue(amtNeg);
		
    	getWritableSecondSplit().setShares(amt);
    	getWritableSecondSplit().setValue(amt);
    }

    /**
     * {@inheritDoc}
     */
	@Override
    public void setAmount(BigFraction amt) throws TransactionSplitNotFoundException {
		BigFraction amtNeg = amt.negate();
		
    	getWritableFirstSplit().setShares(amtNeg);
    	getWritableFirstSplit().setValue(amtNeg);
		
    	getWritableSecondSplit().setShares(amt);
    	getWritableSecondSplit().setValue(amt);
    }

    // ---------------------------------------------------------------
    
	@Override
	public void validate() throws Exception
	{
		if ( getSplitsCount() != NOF_SPLITS ) {
			String msg = "Trx ID " + getID() + ": Number of splits is not " + NOF_SPLITS;
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		// ---
		
		if ( getFirstSplit().getAccount().getQualifSecCurrID().getType() != KMMQualifSecCurrID.Type.CURRENCY ) {
			String msg = "Trx ID " + getID() + ": Security/currency of first split's account is not of type '" + KMMQualifSecCurrID.Type.CURRENCY + "'";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( getSecondSplit().getAccount().getQualifSecCurrID().getType() != KMMQualifSecCurrID.Type.CURRENCY ) {
			String msg = "Trx ID " + getID() + ": Security/currency of second split's account is not of type '" + KMMQualifSecCurrID.Type.CURRENCY + "'";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( ! getFirstSplit().getAccount().getQualifSecCurrID().getCode().equals( getSecondSplit().getAccount().getQualifSecCurrID().getCode() ) ) {
			String msg = "Trx ID " + getID() + ": Security/currency code of the two splits are not equal";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		// ---
		
		if ( getFirstSplit().getSharesRat().doubleValue() != getSecondSplit().getSharesRat().negate().doubleValue() ) {
			String msg = "Trx ID " + getID() + ": Shares of first split is not equal to negative quantity of second split";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( getFirstSplit().getValueRat().doubleValue() != getSecondSplit().getValueRat().negate().doubleValue() ) {
			String msg = "Trx ID " + getID() + ": Value of first split is not equal to negative value of second split";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		// ---
		
		if ( getFirstSplit().getSharesRat().signum() != getFirstSplit().getValueRat().signum() ) {
			String msg = "Trx ID " + getID() + ": Signum of first split's shares and value are not is not equal";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( getSecondSplit().getSharesRat().signum() != getSecondSplit().getValueRat().signum() ) {
			String msg = "Trx ID " + getID() + ": Signum of second split's shares and value are not is not equal";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		// ---
		// redundant:
		
		if ( getBalance().doubleValue() != 0.0 ) {
			String msg = "Trx ID :" + getID() + ": Transaction is not balanced: " + getBalance();
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
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

		buffer.append(", amount=");
		try {
			buffer.append(getAmount());
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
