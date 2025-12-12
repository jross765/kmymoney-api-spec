package org.kmymoney.apispec.read.impl;

import java.util.ArrayList;
import java.util.List;

import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionSplitImpl;
import org.kmymoney.apispec.read.KMyMoneyStockBuyTransaction;
import org.kmymoney.base.basetypes.complex.KMMQualifSecCurrID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;

/**
 * xyz
 * 
 * @see KMyMoneyTransaction
 */
public class KMyMoneyStockBuyTransactionImpl extends KMyMoneyTransactionImpl
										     implements KMyMoneyStockBuyTransaction
{
	public enum SplitAccountType {
		STOCK,
		TAXES_FEES,
		OFFSETTING
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyStockBuyTransactionImpl.class);

	// ---------------------------------------------------------------
    
    private static final int NOF_SPLITS_STOCK = 1;
    
    private static final int NOF_SPLITS_FEES_TAXES_MIN = 0;
    private static final int NOF_SPLITS_FEES_TAXES_MAX = 4; // more is unplausible

    private static final int NOF_SPLITS_OFFSETTING = 1;
    
    // ---

    private static final int NOF_SPLITS_MIN = NOF_SPLITS_STOCK + NOF_SPLITS_FEES_TAXES_MIN + NOF_SPLITS_OFFSETTING;
    private static final int NOF_SPLITS_MAX = NOF_SPLITS_STOCK + NOF_SPLITS_FEES_TAXES_MAX + NOF_SPLITS_OFFSETTING;

	// ---------------------------------------------------------------
    
    private int[] splitCounter;

	// ---------------------------------------------------------------

	public KMyMoneyStockBuyTransactionImpl(KMyMoneyTransactionImpl trx) {
		super(trx);
		
		init();
		
		try {
			validate();
		} catch ( TransactionValidationException exc ) {
			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a simple transaction");
		} catch ( Exception exc ) {
			throw new IllegalArgumentException("argument <trx>: something went wrong");
		}
	}

	// ---------------------------------------------------------------
	
	private void init() {
	    splitCounter = new int[SplitAccountType.values().length];
	    
	    for ( SplitAccountType type : SplitAccountType.values() ) {
	    	splitCounter[type.ordinal()] = 0;
	    }
	    
	    try
		{
			if ( getStockAccountSplit() != null ) {
				splitCounter[SplitAccountType.STOCK.ordinal()] = 1;
			}
			
		    if ( getExpensesSplits().size() != 0 ) {
		    	splitCounter[SplitAccountType.TAXES_FEES.ordinal()] = getExpensesSplits().size();
		    }

		    if ( getOffsettingAccountSplit() != null ) {
		    	splitCounter[SplitAccountType.OFFSETTING.ordinal()] = 1;
		    }
		}
		catch ( TransactionSplitNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// ---------------------------------------------------------------

	// ::TODO / ::CHECK: Really necessary? Or rather dead code?
	@Override
	protected void addSplit(KMyMoneyTransactionSplitImpl splt) {
		if ( getSplitsCount() >= NOF_SPLITS_MAX ) {
			throw new IllegalStateException("This transaction already has the maximum number of splits");
		}
		
		if ( splt.getAccount().getType() == KMyMoneyAccount.Type.STOCK ) {
			try {
				validateStockAcctSplit(splt);
			} catch ( TransactionValidationException exc ) {
				throw new IllegalArgumentException("argument <trx> does not meet the criteria for a simple transaction");
			} catch ( Exception exc ) {
				throw new IllegalArgumentException("argument <trx>: something went wrong");
			}
		} else if ( splt.getAccount().getType() == KMyMoneyAccount.Type.EXPENSE ) {
			try {
				validateTaxesFeesAcctSplit(splt);
			} catch ( TransactionValidationException exc ) {
				throw new IllegalArgumentException("argument <trx> does not meet the criteria for a simple transaction");
			} catch ( Exception exc ) {
				throw new IllegalArgumentException("argument <trx>: something went wrong");
			}
		} else if ( splt.getAccount().getType() == KMyMoneyAccount.Type.CHECKING ) {
			try {
				validateOffsettingAcctSplit(splt);
			} catch ( TransactionValidationException exc ) {
				throw new IllegalArgumentException("argument <trx> does not meet the criteria for a simple transaction");
			} catch ( Exception exc ) {
				throw new IllegalArgumentException("argument <trx>: something went wrong");
			}
		}
		
		super.addSplit(splt);
	}

	public void addSplit(final SplitAccountType type, final KMyMoneyTransactionSplitImpl splt) throws TransactionValidationException {
		if ( type == SplitAccountType.STOCK ) {
			addStockAcctSplit(splt);
		} else if ( type == SplitAccountType.TAXES_FEES ) {
			addTaxesFeesAcctSplit(splt);
		} else if ( type == SplitAccountType.OFFSETTING ) {
			addOffsettingAcctSplit(splt);
		} 
	}
	
	private void addStockAcctSplit(final KMyMoneyTransactionSplitImpl splt) throws TransactionValidationException {
		if ( splitCounter[SplitAccountType.STOCK.ordinal()] > 0 ) {
			throw new IllegalStateException("Stock account split already set");
		}
		
		validateStockAcctSplit( splt );
		
		super.addSplit(splt);
		splitCounter[SplitAccountType.STOCK.ordinal()]++;
	}
	
	private void addTaxesFeesAcctSplit(final KMyMoneyTransactionSplitImpl splt) throws TransactionValidationException {
		if ( splitCounter[SplitAccountType.TAXES_FEES.ordinal()] > 0 /* ::TODO */ ) {
			throw new IllegalStateException("Taxes/fees account split already set");
		}

		validateTaxesFeesAcctSplit( splt );
		
		super.addSplit(splt);
		splitCounter[SplitAccountType.TAXES_FEES.ordinal()]++;
	}
	
	private void addOffsettingAcctSplit(final KMyMoneyTransactionSplitImpl splt) throws TransactionValidationException {
		if ( splitCounter[SplitAccountType.OFFSETTING.ordinal()] > 0 ) {
			throw new IllegalStateException("Offsetting account split already set");
		}
		
		validateOffsettingAcctSplit( splt );
		
		super.addSplit(splt);
		splitCounter[SplitAccountType.OFFSETTING.ordinal()]++;
	}
	
	// ---------------------------------------------------------------
	
	@Override
	public void validate() throws Exception
	{
		if ( getSplitsCount() < NOF_SPLITS_MIN ) {
			String msg = "Trx ID " + getID() + ": Number of splits (altogether) is < " + NOF_SPLITS_MIN;
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( getSplitsCount() > NOF_SPLITS_MAX ) {
			String msg = "Trx ID " + getID() + ": Number of splits (altogether) is > " + NOF_SPLITS_MAX;
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splitCounter[SplitAccountType.STOCK.ordinal()] != NOF_SPLITS_STOCK ) {
			String msg = "Trx ID " + getID() + ": Number of splits to stock account is not " + NOF_SPLITS_STOCK;
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splitCounter[SplitAccountType.TAXES_FEES.ordinal()] < NOF_SPLITS_FEES_TAXES_MIN || 
			 splitCounter[SplitAccountType.TAXES_FEES.ordinal()] > NOF_SPLITS_FEES_TAXES_MAX ) {
			String msg = "Trx ID " + getID() + ": Number of splits to expenses account is not between " + NOF_SPLITS_FEES_TAXES_MIN + " and " + NOF_SPLITS_FEES_TAXES_MAX;
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splitCounter[SplitAccountType.OFFSETTING.ordinal()] != NOF_SPLITS_OFFSETTING ) {
			String msg = "Trx ID " + getID() + ": Number of splits to offsetting account is not " + NOF_SPLITS_OFFSETTING;
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		// ---
		
		validateStockAcctSplit( getStockAccountSplit() );
		
		for ( KMyMoneyTransactionSplit splt : getExpensesSplits() ) {
			validateTaxesFeesAcctSplit( splt );
		}
		
		validateOffsettingAcctSplit( getOffsettingAccountSplit() );

		// ---
		
		if ( getBalance().doubleValue() != 0.0 ) {
			String msg = "Trx ID :" + getID() + ": Transaction is not balanced: " + getBalance();
			LOGGER.error("validate: " + msg);
			throw new TransactionValidationException(msg);
		}
	}
	
	// ----------------------------
	
	private void validateStockAcctSplit(final KMyMoneyTransactionSplit splt) throws TransactionValidationException {
		if ( splt.getAction() != KMyMoneyTransactionSplit.Action.BUY_SHARES ) {
			String msg = "the split's action is not valid";
			LOGGER.error("validateStockAcctSplit: " + msg);
			throw new TransactionValidationException("msg");
		}
		
		if ( splt.getAccount().getType() != KMyMoneyAccount.Type.STOCK ) {
			String msg = "the split's account's type is not valid";
			LOGGER.error("validateStockAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getAccount().getQualifSecCurrID().getType() == KMMQualifSecCurrID.Type.CURRENCY ) {
			String msg = "the split's account's security/currency is not valid";
			LOGGER.error("validateStockAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getShares().doubleValue() <= 0.0 ) {
			String msg = "the split's shares is not valid";
			LOGGER.error("validateStockAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getValue().doubleValue() <= 0.0 ) {
			String msg = "the split's value is not valid";
			LOGGER.error("validateStockAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getPrice().doubleValue() <= 0.0 ) {
			String msg = "the split's price is not valid";
			LOGGER.error("validateStockAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
	}
	
	private void validateTaxesFeesAcctSplit(final KMyMoneyTransactionSplit splt) throws TransactionValidationException {
		if ( splt.getAction() != null ) { // null is valid!
			String msg = "the split's action is not valid";
			LOGGER.error("validateTaxesFeesAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getAccount().getType() != KMyMoneyAccount.Type.EXPENSE ) {
			String msg = "the split's account's type is not valid";
			LOGGER.error("validateTaxesFeesAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getAccount().getQualifSecCurrID().getType() != KMMQualifSecCurrID.Type.CURRENCY ) {
			String msg = "the split's account's security/currency is not valid";
			LOGGER.error("validateTaxesFeesAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getShares().doubleValue() <= 0.0 ) {
			String msg = "the split's shares is not valid";
			LOGGER.error("validateTaxesFeesAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getValue().doubleValue() <= 0.0 ) {
			String msg = "the split's value is not valid";
			LOGGER.error("validateTaxesFeesAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getPrice().doubleValue() != 1.0 ) {
			String msg = "the split's price is not valid";
			LOGGER.error("validateStockAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( ! splt.getShares().equals( splt.getValue() ) ) {
			String msg = "the split's shares is not equal to ist value";
			LOGGER.error("validateTaxesFeesAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
	}
	
	private void validateOffsettingAcctSplit(final KMyMoneyTransactionSplit splt) throws TransactionValidationException {
		if ( splt.getAction() != null ) { // null is valid!
			String msg = "the split's action is not valid";
			LOGGER.error("validateOffsettingAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getAccount().getType() != KMyMoneyAccount.Type.CHECKING ) {
			String msg = "the split's account's type is not valid";
			LOGGER.error("validateOffsettingAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getAccount().getQualifSecCurrID().getType() != KMMQualifSecCurrID.Type.CURRENCY ) {
			String msg = "the split's account's security/currency is not valid";
			LOGGER.error("validateOffsettingAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getShares().doubleValue() >= 0.0 ) {
			String msg = "the split's shares is not valid";
			LOGGER.error("validateOffsettingAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getValue().doubleValue() >= 0.0 ) {
			String msg = "the split's value is not valid";
			LOGGER.error("validateOffsettingAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( splt.getPrice().doubleValue() != 1.0 ) {
			String msg = "the split's price is not valid";
			LOGGER.error("validateStockAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
		
		if ( ! splt.getShares().equals( splt.getValue() ) ) {
			String msg = "the split's shares is not equal to ist value";
			LOGGER.error("validateOffsettingAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
	}
	
	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KMyMoneyTransactionSplit getStockAccountSplit() throws TransactionSplitNotFoundException
	{
		for ( KMyMoneyTransactionSplit splt : getSplits() ) {
			if ( splt.getAccount().getType() == KMyMoneyAccount.Type.STOCK ) {
				return splt;
			}
		}
		
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<KMyMoneyTransactionSplit> getExpensesSplits() throws TransactionSplitNotFoundException
	{
		List<KMyMoneyTransactionSplit> result = new ArrayList<KMyMoneyTransactionSplit>();
		
		for ( KMyMoneyTransactionSplit splt : getSplits() ) {
			if ( splt.getAccount().getType() == KMyMoneyAccount.Type.EXPENSE ) {
				result.add(splt);
			}
		}
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KMyMoneyTransactionSplit getOffsettingAccountSplit() throws TransactionSplitNotFoundException
	{
		for ( KMyMoneyTransactionSplit splt : getSplits() ) {
			if ( splt.getAccount().getType() == KMyMoneyAccount.Type.CHECKING ) {
				return splt;
			}
		}
		
		return null;
	}
	
	// ---------------------------------------------------------------
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("KMyMoneyStockBuyTransactionImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", balance=");
		buffer.append(getBalanceFormatted());

		buffer.append(", memo='");
		buffer.append(getMemo() + "'");

		buffer.append(", stock-acct=");
		try {
			buffer.append(getStockAccountSplit().getAccount().getID());
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", #expenses-splits=");
		try {
			buffer.append(getExpensesSplits().size());
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", offset-acct=");
		try {
			buffer.append(getOffsettingAccountSplit().getAccount().getID());
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
