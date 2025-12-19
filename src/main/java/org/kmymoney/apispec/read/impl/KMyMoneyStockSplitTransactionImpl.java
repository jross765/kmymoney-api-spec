package org.kmymoney.apispec.read.impl;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionSplitImpl;
import org.kmymoney.apispec.read.KMyMoneyStockSplitTransaction;
import org.kmymoney.base.basetypes.complex.KMMQualifSecCurrID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * xyz
 * 
 * @see KMyMoneyTransaction
 */
public class KMyMoneyStockSplitTransactionImpl extends KMyMoneyTransactionImpl
											   implements KMyMoneyStockSplitTransaction
{
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyStockSplitTransactionImpl.class);

	// ---------------------------------------------------------------
    
    private static final int NOF_SPLITS = 1;

	// ---------------------------------------------------------------

	public KMyMoneyStockSplitTransactionImpl(KMyMoneyTransactionImpl trx) {
		super( trx );
		
		try {
			validate();
		} catch ( TransactionValidationException exc ) {
			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a simple transaction");
		} catch ( Exception exc ) {
			throw new IllegalArgumentException("argument <trx>: something went wrong");
		}
	}
	
	// ---------------------------------------------------------------

	@Override
	protected void addSplit(KMyMoneyTransactionSplitImpl splt) {
		if ( getSplitsCount() == NOF_SPLITS ) {
			throw new IllegalStateException("This transaction already has a split");
		}
		
		try {
			validate();
		} catch ( TransactionValidationException exc ) {
			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a simple transaction");
		} catch ( Exception exc ) {
			throw new IllegalArgumentException("argument <trx>: something went wrong");
		}
		
		super.addSplit( splt );
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
		// (as opposed to KMyMoneySimpleTransactionImpl),
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
	
    /**
     * {@inheritDoc}
     */
	@Override
	public KMyMoneyTransactionSplit getSplit() throws TransactionSplitNotFoundException {
		return getSplits().get(0);
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
		KMyMoneyAccount acct = getSplit().getAccount();
		return acct.getBalance(getSplit());
	}
	
	@Override
	public BigFraction getNofSharesAfterSplitRat() throws TransactionSplitNotFoundException {
		KMyMoneyAccount acct = getSplit().getAccount();
		return acct.getBalanceRat(getSplit());
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
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("KMyMoneyStockSplitTransactionImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", balance=");
		buffer.append(getBalanceFormatted());

		buffer.append(", memo='");
		buffer.append(getMemo() + "'");

		buffer.append(", split=");
		try {
			buffer.append(getSplit().getID());
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
