package org.kmymoney.apispec.read.impl;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.apispec.read.KMyMoneyStockBuyTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * xyz
 * 
 * @see KMyMoneyTransaction
 */
public class KMyMoneyStockBuyTransactionImpl extends KMyMoneyStockBuySellTransactionImpl
                                             implements KMyMoneyStockBuyTransaction
{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyStockBuyTransactionImpl.class);

	// ---------------------------------------------------------------
    
	public KMyMoneyStockBuyTransactionImpl(final KMyMoneyTransactionImpl trx) {
		super( trx );
	}

	public KMyMoneyStockBuyTransactionImpl(final KMyMoneyStockBuyTransactionImpl trx) {
		super(trx);
		
//		init();
//		
//		try {
//			validate();
//		} catch ( TransactionValidationException exc ) {
//			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a stock-buy transaction");
//		} catch ( Exception exc ) {
//			throw new IllegalArgumentException("argument <trx>: something went wrong");
//		}
	}

	// ---------------------------------------------------------------
    
	@Override
	protected void validateStockAcctSplit(final KMyMoneyTransactionSplit splt) throws TransactionValidationException {
		super.validateStockAcctSplit(splt);

		if ( splt.getSharesRat().compareTo(BigFraction.ZERO) <= 0 ) {
			String msg = "the split's shares is not valid";
			LOGGER.error("validateStockAcctSplit (2): " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getValueRat().compareTo(BigFraction.ZERO) <= 0 ) {
			String msg = "the split's value is not valid";
			LOGGER.error("validateStockAcctSplit (2): " + msg);
			throw new TransactionValidationException(msg);
		}
	}

	@Override
	protected void validateOffsettingAcctSplit(final KMyMoneyTransactionSplit splt) throws TransactionValidationException {
		super.validateOffsettingAcctSplit(splt);
		
		if ( splt.getSharesRat().compareTo(BigFraction.ZERO) >= 0 ) {
			String msg = "the split's shares is not valid";
			LOGGER.error("validateOffsettingAcctSplit (2): " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getValueRat().compareTo(BigFraction.ZERO) >= 0 ) {
			String msg = "the split's value is not valid";
			LOGGER.error("validateOffsettingAcctSplit (2): " + msg);
			throw new TransactionValidationException(msg);
		}
	}
	
	// ---------------------------------------------------------------
	
	@Override
	public String toString() {
		String result = super.toString();
		result = result.replaceAll( "KMyMoneyStockBuySellTransactionImpl", "KMyMoneyStockBuyTransactionImpl" );
		return result;
	}

	public String toStringHuman() {
		String result = super.toStringHuman();
		result = result.replaceAll( "Stock-buy-sell", "Stock-buy" );
		return result;
	}

}
