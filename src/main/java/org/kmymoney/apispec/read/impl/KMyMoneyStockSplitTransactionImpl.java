package org.kmymoney.apispec.read.impl;

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

/**
 * xyz
 * 
 * @see KMyMoneyTransaction
 */
public class KMyMoneyStockSplitTransactionImpl extends KMyMoneyTransactionImpl
											   implements KMyMoneyStockSplitTransaction
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyStockSplitTransactionImpl.class);

	// ---------------------------------------------------------------

	public KMyMoneyStockSplitTransactionImpl(KMyMoneyTransactionImpl trx) {
		super( trx );
	}
	
	// ---------------------------------------------------------------

	@Override
	protected void addSplit(KMyMoneyTransactionSplitImpl splt) {
		if ( getSplitsCount() == 1 ) {
			throw new IllegalStateException("This transaction already has a split");
		}
		
		// splt.getActionStr() == null is *not* valid here
		// (as opposed to KMyMoneySimpleTransactionImpl),
		// but implicitly checked with the following:
		if ( splt.getAction() != KMyMoneyTransactionSplit.Action.SPLIT_SHARES ) {
			throw new IllegalArgumentException("the split's action is not " + KMyMoneyTransactionSplit.Action.SPLIT_SHARES);
		}
		
		if ( splt.getAccount().getType() != KMyMoneyAccount.Type.STOCK ) {
			throw new IllegalArgumentException("the split's account's type is not " + KMyMoneyAccount.Type.STOCK);
		}
		
		if ( splt.getAccount().getQualifSecCurrID().getType() == KMMQualifSecCurrID.Type.CURRENCY ) {
			throw new IllegalArgumentException("the split's account's commodity/currency is of type " + KMMQualifSecCurrID.Type.CURRENCY);
		}
		
		super.addSplit( splt );
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
