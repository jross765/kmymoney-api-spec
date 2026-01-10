package org.kmymoney.apispec.read.impl;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionSplitImpl;
import org.kmymoney.apispec.read.KMyMoneySimpleTransaction;
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
public class KMyMoneySimpleTransactionImpl extends KMyMoneyTransactionImpl
										   implements KMyMoneySimpleTransaction
{
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneySimpleTransactionImpl.class);

	// ---------------------------------------------------------------
    
    private static final int NOF_SPLITS = 2;

	// ---------------------------------------------------------------

	public KMyMoneySimpleTransactionImpl(KMyMoneyTransactionImpl trx) {
		super(trx);
		
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
	protected void addSplit(final KMyMoneyTransactionSplitImpl splt) {
		if ( getSplitsCount() == NOF_SPLITS ) {
			throw new IllegalStateException("This transaction already has two splits");
		}

		if ( splt.getActionStr() != null ) { // null is valid!
			if ( splt.getAction() != KMyMoneyTransactionSplit.Action.DEPOSIT ||
				 splt.getAction() != KMyMoneyTransactionSplit.Action.WITHDRAWAL ||
				 
				 splt.getAction() != KMyMoneyTransactionSplit.Action.BUY_SHARES ||
				 splt.getAction() != KMyMoneyTransactionSplit.Action.SELL_SHARES || 
					 
				 splt.getAction() != KMyMoneyTransactionSplit.Action.DIVIDEND ||
				 splt.getAction() != KMyMoneyTransactionSplit.Action.INTEREST_INCOME ||
				 splt.getAction() != KMyMoneyTransactionSplit.Action.SPLIT_SHARES ) {
					throw new IllegalArgumentException("the split's action is not valid");
				}
		}
		
		if ( splt.getAccount().getType() != KMyMoneyAccount.Type.CHECKING &&
			 splt.getAccount().getType() != KMyMoneyAccount.Type.CASH &&
			 splt.getAccount().getType() != KMyMoneyAccount.Type.CREDIT_CARD ) {
			throw new IllegalArgumentException("the split's account's type is not valid");
		}
		
		if ( splt.getAccount().getQualifSecCurrID().getType() != KMMQualifSecCurrID.Type.CURRENCY ) {
			throw new IllegalArgumentException("the split's account's security/currency is not of type " + KMMQualifSecCurrID.Type.CURRENCY);
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
	
    /**
     * {@inheritDoc}
     */
	@Override
    public KMyMoneyTransactionSplit getFirstSplit() throws TransactionSplitNotFoundException {
		if ( getSplitsCount() <= 0 )
			throw new TransactionSplitNotFoundException();
	
    	return getSplits().get(0);
    }

    /**
     * {@inheritDoc}
     */
	@Override
    public KMyMoneyTransactionSplit getSecondSplit() throws TransactionSplitNotFoundException {
		if ( getSplitsCount() <= 1 )
			throw new TransactionSplitNotFoundException();

		return getSplits().get(1);
    }

    // ---------------------------------------------------------------
    
    public FixedPointNumber getAmount() throws TransactionSplitNotFoundException {
    	return getSecondSplit().getValue();
    }
    
    public BigFraction getAmountRat() throws TransactionSplitNotFoundException {
    	return getSecondSplit().getValueRat();
    }
    
	// ---------------------------------------------------------------
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("KMyMoneySimpleTransactionImpl [");

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
