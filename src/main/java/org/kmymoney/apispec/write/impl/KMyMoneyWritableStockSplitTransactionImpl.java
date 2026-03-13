package org.kmymoney.apispec.write.impl;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.impl.KMyMoneyWritableTransactionImpl;
import org.kmymoney.apispec.read.impl.KMyMoneySimpleTransactionImpl;
import org.kmymoney.apispec.read.impl.KMyMoneyStockSplitTransactionImpl;
import org.kmymoney.apispec.read.impl.TransactionValidationException;
import org.kmymoney.apispec.write.KMyMoneyWritableStockSplitTransaction;
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
public class KMyMoneyWritableStockSplitTransactionImpl extends KMyMoneyWritableTransactionImpl 
                                                       implements KMyMoneyWritableStockSplitTransaction
{
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyWritableStockSplitTransactionImpl.class);

	// ---------------------------------------------------------------
    
    private static final int NOF_SPLITS = 1;

	// ---------------------------------------------------------------

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public KMyMoneyWritableStockSplitTransactionImpl(final KMyMoneyStockSplitTransactionImpl trx) {
    	super(trx);
    	
		try {
			validate();
		} catch ( TransactionValidationException exc ) {
			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a stock-split transaction");
		} catch ( Exception exc ) {
			throw new IllegalArgumentException("argument <trx>: something went wrong");
		}
    }

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public KMyMoneyWritableStockSplitTransactionImpl(final KMyMoneyWritableStockSplitTransaction trx) {
    	super(trx);
    	
		try {
			validate();
		} catch ( TransactionValidationException exc ) {
			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a stock-split transaction");
		} catch ( Exception exc ) {
			throw new IllegalArgumentException("argument <trx>: something went wrong");
		}
    }

    // ---------------------------------------------------------------
    
	@Override
	public KMyMoneyTransactionSplit getSplit() throws TransactionSplitNotFoundException
	{
    	if ( getSplitsCount() == 0 )
    		throw new TransactionSplitNotFoundException();
	
		return getSplits().get(0);
	}

	@Override
	public KMyMoneyWritableTransactionSplit getWritableSplit() throws TransactionSplitNotFoundException
	{
    	return (KMyMoneyWritableTransactionSplit) getSplit();
	}

	// ---------------------------------------------------------------
	
	@Override
	public FixedPointNumber getSplitFactor() throws TransactionSplitNotFoundException {
		return getSplit().getShares();
	}

	@Override
	public BigFraction getSplitFactorRat() throws TransactionSplitNotFoundException {
		return getSplit().getSharesRat();
	}

	@Override
	public FixedPointNumber getNofAddShares() throws TransactionSplitNotFoundException {
		return getNofSharesAfterSplit().subtract( getNofSharesBeforeSplit() );
	}

	@Override
	public BigFraction getNofAddSharesRat() throws TransactionSplitNotFoundException {
		return getNofSharesAfterSplitRat().subtract( getNofSharesBeforeSplitRat() );
	}
	
	@Override
	public FixedPointNumber getNofSharesBeforeSplit() throws TransactionSplitNotFoundException {
		KMyMoneyAccount acct = getSplit().getAccount();
		return acct.getBalance(getPreviousSplit());
	}

	@Override
	public BigFraction getNofSharesBeforeSplitRat() throws TransactionSplitNotFoundException {
		KMyMoneyAccount acct = getSplit().getAccount();
		return acct.getBalanceRat(getPreviousSplit());
	}

	@Override
	public FixedPointNumber getNofSharesAfterSplit() throws TransactionSplitNotFoundException {
		// Altern. 1:
		// KMyMoneyAccount acct = getSplit().getAccount();
		// return acct.getBalance(getSplit());
		// Altern. 2:
		return getNofSharesBeforeSplit().multiply( getSplitFactor() );
	}
	
	@Override
	public BigFraction getNofSharesAfterSplitRat() throws TransactionSplitNotFoundException {
		// Altern. 1:
		// KMyMoneyAccount acct = getSplit().getAccount();
		// return acct.getBalanceRat(getSplit());
		// Altern. 2:
		return getNofSharesBeforeSplitRat().multiply( getSplitFactorRat() );
	}
	
	// ----------------------------
	
	public KMyMoneyTransactionSplit getPreviousSplit() throws TransactionSplitNotFoundException {
		KMyMoneyAccount acct = getSplit().getAccount();
		
		KMyMoneyTransactionSplit prevSplt = null;
		for ( KMyMoneyTransactionSplit splt : acct.getTransactionSplits() ) {
			if ( splt.getID().equals( getSplit().getID() )) {
				return prevSplt;
			}
			
			prevSplt = splt;
		}
		
		return null;
	}

	// ---------------------------------------------------------------
    
	@Override
	public void setSplitFactor(FixedPointNumber val) throws TransactionSplitNotFoundException
	{
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.equals(FixedPointNumber.ZERO) ) {
			throw new IllegalArgumentException("argument <val> is = 0");
		}
		
		getWritableSplit().setShares(val);
	}

	@Override
	public void setSplitFactor(BigFraction val) throws TransactionSplitNotFoundException
	{
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.equals(BigFraction.ZERO) ) {
			throw new IllegalArgumentException("argument <val> is = 0");
		}
		
		getWritableSplit().setShares(val);
	}

	@Override
	public void setNofAddShares(FixedPointNumber val) throws TransactionSplitNotFoundException
	{
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.equals(FixedPointNumber.ZERO) ) {
			throw new IllegalArgumentException("argument <val> is = 0");
		}
		
		FixedPointNumber nofShrAfter = getNofSharesBeforeSplit().add(val);
		FixedPointNumber factor      = nofShrAfter.divide( getNofSharesBeforeSplit() );
		setSplitFactor(factor);
	}

	@Override
	public void setNofAddShares(BigFraction val) throws TransactionSplitNotFoundException
	{
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.equals(BigFraction.ZERO) ) {
			throw new IllegalArgumentException("argument <val> is = 0");
		}
		
		BigFraction nofShrAfter = getNofSharesBeforeSplitRat().add(val);
		BigFraction factor      = nofShrAfter.divide( getNofSharesBeforeSplitRat() );
		setSplitFactor(factor);
	}

	@Override
	public void setNofSharesAfterSplit(FixedPointNumber val) throws TransactionSplitNotFoundException
	{
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.equals(FixedPointNumber.ZERO) ) {
			throw new IllegalArgumentException("argument <val> is = 0");
		}
		
		FixedPointNumber factor = val.divide( getNofSharesBeforeSplit() );
		setSplitFactor(factor);
	}

	@Override
	public void setNofSharesAfterSplit(BigFraction val) throws TransactionSplitNotFoundException
	{
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.equals(BigFraction.ZERO) ) {
			throw new IllegalArgumentException("argument <val> is = 0");
		}
		
		BigFraction factor = val.divide( getNofSharesBeforeSplitRat() );
		setSplitFactor(factor);
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
		
		// splt.getActionStr() == null is *not* valid here
		// (as opposed to KMyMoneyStockSplitTransactionImpl),
		// but implicitly checked with the following:
		if ( getSplit().getAction() != KMyMoneyTransactionSplit.Action.SPLIT_SHARES ) {
			throw new IllegalArgumentException("the split's action is not " + KMyMoneyTransactionSplit.Action.SPLIT_SHARES);
		}
		
		if ( getSplit().getAccount().getType() != KMyMoneyAccount.Type.STOCK ) {
			throw new IllegalArgumentException("the split's account's type is not " + KMyMoneyAccount.Type.STOCK);
		}
		
		if ( getSplit().getAccount().getQualifSecCurrID().getType() == KMMQualifSecCurrID.Type.CURRENCY ) {
			String msg = "Trx ID " + getID() + ": Security/currency of first split's account is of type '" + KMMQualifSecCurrID.Type.CURRENCY + "'";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( getSplit().getSharesRat().doubleValue() == 0.0 ) {
			String msg = "Trx ID " + getID() + ": Shares of the split is = 0";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( getSplit().getValueRat().doubleValue() != 0.0 ) {
			String msg = "Trx ID " + getID() + ": Value of the split is != 0";
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
	}
	
	// ---------------------------------------------------------------
    
    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("KMyMoneyWritableStockSplitTransactionImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", balance=");
		buffer.append(getBalanceFormatted());

		buffer.append(", memo='");
		buffer.append(getMemo() + "'");

//		buffer.append(", split=");
//		try {
//			buffer.append(getSplit().getID());
//		} catch (Exception e) {
//			buffer.append("ERROR");
//		}

		buffer.append(", stock-acct=");
		try {
			buffer.append(getSplit().getAccount().getID());
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", factor=");
		try {
			buffer.append(getSplitFactor());
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
