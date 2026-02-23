package org.kmymoney.apispec.write.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.impl.KMyMoneyWritableTransactionImpl;
import org.kmymoney.apispec.read.impl.KMyMoneySimpleTransactionImpl;
import org.kmymoney.apispec.read.impl.KMyMoneyStockBuyTransactionImpl;
import org.kmymoney.apispec.read.impl.KMyMoneyStockBuyTransactionImpl.SplitAccountType;
import org.kmymoney.apispec.read.impl.TransactionValidationException;
import org.kmymoney.apispec.write.KMyMoneyWritableStockBuyTransaction;
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
public class KMyMoneyWritableStockBuyTransactionImpl extends KMyMoneyWritableTransactionImpl 
                                                     implements KMyMoneyWritableStockBuyTransaction
{
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyWritableStockBuyTransaction.class);

	// ---------------------------------------------------------------
    
    private static final int NOF_SPLITS_STOCK = 1;
    
    private static final int NOF_SPLITS_FEES_TAXES_MIN = 0;
    private static final int NOF_SPLITS_FEES_TAXES_MAX = 4; // more is implausible

    private static final int NOF_SPLITS_OFFSETTING = 1;
    
    // ---

    private static final int NOF_SPLITS_MIN = NOF_SPLITS_STOCK + NOF_SPLITS_FEES_TAXES_MIN + NOF_SPLITS_OFFSETTING;
    private static final int NOF_SPLITS_MAX = NOF_SPLITS_STOCK + NOF_SPLITS_FEES_TAXES_MAX + NOF_SPLITS_OFFSETTING;

	// ---------------------------------------------------------------
    
    private int[] splitCounter;

	// ---------------------------------------------------------------

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public KMyMoneyWritableStockBuyTransactionImpl(final KMyMoneyStockBuyTransactionImpl trx) {
    	super(trx);
    	
    	init();
    	
		try {
			validate();
		} catch ( TransactionValidationException exc ) {
			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a stock-buy transaction");
		} catch ( Exception exc ) {
			throw new IllegalArgumentException("argument <trx>: something went wrong");
		}
    }

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param trx the file we belong to
     */
    public KMyMoneyWritableStockBuyTransactionImpl(final KMyMoneyWritableStockBuyTransaction trx) {
    	super(trx);
    	
    	init();
    	
		try {
			validate();
		} catch ( TransactionValidationException exc ) {
			throw new IllegalArgumentException("argument <trx> does not meet the criteria for a stock-buy transaction");
		} catch ( Exception exc ) {
			throw new IllegalArgumentException("argument <trx>: something went wrong");
		}
    }

    // ---------------------------------------------------------------
    
    // ::TODO: Redundant to GnuCashStockBuyTransactionImpl.init()
    protected void init() {
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
    
	@Override
	public KMyMoneyWritableTransactionSplit getWritableStockAccountSplit() throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() == 0 )
    		throw new TransactionSplitNotFoundException();
	
    	return (KMyMoneyWritableTransactionSplit) getStockAccountSplit();
	}

	@Override
	public List<KMyMoneyWritableTransactionSplit> getWritableExpensesSplits() throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() == 0 )
    		throw new TransactionSplitNotFoundException();
	
    	List<KMyMoneyWritableTransactionSplit> result = new ArrayList<KMyMoneyWritableTransactionSplit>();
    	
    	for ( KMyMoneyTransactionSplit splt : getExpensesSplits() ) {
    		result.add( (KMyMoneyWritableTransactionSplit) splt );
    	}
    	
    	return result;
	}

	@Override
	public KMyMoneyWritableTransactionSplit getWritableOffsettingAccountSplit() throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() == 0 )
    		throw new TransactionSplitNotFoundException();
	
    	return (KMyMoneyWritableTransactionSplit) getOffsettingAccountSplit();
	}

    // ---------------------------------------------------------------
    
	@Override
	public KMyMoneyTransactionSplit getStockAccountSplit() throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() == 0 )
    		throw new TransactionSplitNotFoundException();
	
		for ( KMyMoneyTransactionSplit splt : getSplits() ) {
			if ( splt.getAccount().getType() == KMyMoneyAccount.Type.STOCK ) {
				return splt;
			}
		}
		
		return null;
	}

	@Override
	public List<KMyMoneyTransactionSplit> getExpensesSplits() throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() == 0 )
    		throw new TransactionSplitNotFoundException();
	
		List<KMyMoneyTransactionSplit> result = new ArrayList<KMyMoneyTransactionSplit>();
		
		for ( KMyMoneyTransactionSplit splt : getSplits() ) {
			if ( splt.getAccount().getType() == KMyMoneyAccount.Type.EXPENSE ) {
				result.add(splt);
			}
		}
		
		return result;
	}

	@Override
	public KMyMoneyTransactionSplit getOffsettingAccountSplit() throws TransactionSplitNotFoundException {
    	if ( getSplitsCount() == 0 )
    		throw new TransactionSplitNotFoundException();
	
		for ( KMyMoneyTransactionSplit splt : getSplits() ) {
			if ( splt.getAccount().getType() == KMyMoneyAccount.Type.CHECKING ) {
				return splt;
			}
		}
		
		return null;
	}

    // ----------------------------
    
	@Override
	public FixedPointNumber getNofShares() throws TransactionSplitNotFoundException {
		return getStockAccountSplit().getShares();
	}

	@Override
	public BigFraction getNofSharesRat() throws TransactionSplitNotFoundException {
		return getStockAccountSplit().getSharesRat();
	}

	@Override
	public FixedPointNumber getPricePerShare() throws TransactionSplitNotFoundException {
		FixedPointNumber result = getNetPrice();
		
		result.divide( getNofShares() ); // mutable
		
		return result;
	}

	@Override
	public BigFraction getPricePerShareRat() throws TransactionSplitNotFoundException {
    	BigFraction result = getNetPriceRat();
		
		result = result.divide( getNofSharesRat() ); // immutable
		
		return result;
	}

	@Override
	public FixedPointNumber getNetPrice() throws TransactionSplitNotFoundException {
		FixedPointNumber result = getGrossPrice();
		
		result.subtract( getFeesTaxes() ); // mutable
		
		return result;
	}

	@Override
	public BigFraction getNetPriceRat() throws TransactionSplitNotFoundException {
		BigFraction result = getGrossPriceRat();
		
		result = result.subtract( getFeesTaxesRat() ); // immutable
		
		return result;
	}

	@Override
	public FixedPointNumber getFeesTaxes() throws TransactionSplitNotFoundException {
		FixedPointNumber result = FixedPointNumber.ZERO.copy(); // Caution: FPN is mutable!
		
		for ( KMyMoneyTransactionSplit splt : getExpensesSplits() ) {
			result.add( splt.getValue() ); // mutable
		}
		
		return result;
	}

	@Override
	public BigFraction getFeesTaxesRat() throws TransactionSplitNotFoundException {
		BigFraction result = BigFraction.ZERO; // Caution: BF is immutable
		
		for ( KMyMoneyTransactionSplit splt : getExpensesSplits() ) {
			result = result.add( splt.getValueRat() ); // immutable
		}
		
		return result;
	}

	@Override
	public FixedPointNumber getGrossPrice() throws TransactionSplitNotFoundException {
		return getOffsettingAccountSplit().getValue().negate(); // Notice: negate
	}

	@Override
	public BigFraction getGrossPriceRat() throws TransactionSplitNotFoundException {
		return getOffsettingAccountSplit().getValueRat().negate(); // Notice: negate
	}

    // ---------------------------------------------------------------
    
	@Override
	public void setNofShares(final FixedPointNumber val) throws TransactionSplitNotFoundException {
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.equals(FixedPointNumber.ZERO) ) {
			throw new IllegalArgumentException("argument <val> is = 0");
		}
		
		getWritableStockAccountSplit().setShares(val);
	}

	@Override
	public void setNofShares(final BigFraction val) throws TransactionSplitNotFoundException {
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.equals(BigFraction.ZERO) ) {
			throw new IllegalArgumentException("argument <val> is = 0");
		}
		
		getWritableStockAccountSplit().setShares(val);
	}

	@Override
	public void setPricePerShare(final FixedPointNumber amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(FixedPointNumber.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		FixedPointNumber netPrc = getNofShares().multiply(amt);
		FixedPointNumber feeTax = getFeesTaxes();
		FixedPointNumber grossPrc = netPrc.copy().add(feeTax); // mutable
		setGrossPrice(grossPrc);
	}

	@Override
	public void setPricePerShare(final BigFraction amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		BigFraction netPrc = getNofSharesRat().multiply(amt);
		BigFraction feeTax = getFeesTaxesRat();
		BigFraction grossPrc = netPrc.add(feeTax); // immutable
		setGrossPrice(grossPrc);
	}

	@Override
	public void setGrossPrice(final FixedPointNumber amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( amt.equals(FixedPointNumber.ZERO) ) {
			throw new IllegalArgumentException("argument <amt> is = 0");
		}
		
		FixedPointNumber amtNeg = amt.copy().negate(); // mutable
		
		getWritableOffsettingAccountSplit().setShares(amtNeg);
		getWritableOffsettingAccountSplit().setValue(amtNeg);
	}

	@Override
	public void setGrossPrice(final BigFraction amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( amt.equals(BigFraction.ZERO) ) {
			throw new IllegalArgumentException("argument <amt> is = 0");
		}
		
		BigFraction amtNeg = amt.negate(); // immutable
		
		getWritableOffsettingAccountSplit().setShares(amtNeg);
		getWritableOffsettingAccountSplit().setValue(amtNeg);
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
			String msg = "the split's shares is not equal to its value";
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
			String msg = "the split's shares is not equal to its value";
			LOGGER.error("validateOffsettingAcctSplit: " + msg);
			throw new TransactionValidationException(msg);
		}
	}
	
	// ---------------------------------------------------------------
    
    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("KMyMoneyWritableStockBuyTransactionImpl [");

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
