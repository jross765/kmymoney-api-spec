package org.kmymoney.apispec.read.impl;

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

/**
 * xyz
 * 
 * @see KMyMoneyTransaction
 */
public class KMyMoneySimpleTransactionImpl extends KMyMoneyTransactionImpl
										   implements KMyMoneySimpleTransaction
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneySimpleTransactionImpl.class);

	// ---------------------------------------------------------------

	public KMyMoneySimpleTransactionImpl(KMyMoneyTransactionImpl trx) {
		super(trx);
	}

	// ---------------------------------------------------------------
	
	@Override
	protected void addSplit(KMyMoneyTransactionSplitImpl splt) {
		if ( getSplitsCount() == 2 ) {
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
			throw new IllegalArgumentException("the split's account's commodity/currency is not of type " + KMMQualifSecCurrID.Type.CURRENCY);
		}
		
		super.addSplit( splt );
	}

	// ---------------------------------------------------------------
	
    /**
     * {@inheritDoc}
     */
	@Override
    public KMyMoneyTransactionSplit getFirstSplit() throws TransactionSplitNotFoundException {
    	if ( getSplits().size() == 0 )
    		throw new TransactionSplitNotFoundException();
	
    	return getSplits().get(0);
    }

    /**
     * {@inheritDoc}
     */
	@Override
    public KMyMoneyTransactionSplit getSecondSplit() throws TransactionSplitNotFoundException {
		if ( getSplits().size() <= 1 )
			throw new TransactionSplitNotFoundException();

		return getSplits().get(1);
    }

	// ---------------------------------------------------------------
	
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
