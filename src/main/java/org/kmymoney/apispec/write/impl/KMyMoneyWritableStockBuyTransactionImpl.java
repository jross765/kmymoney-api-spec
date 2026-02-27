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
import org.kmymoney.base.basetypes.complex.KMMComplAcctID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.base.basetypes.simple.KMMAcctID;
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
    public KMyMoneyWritableTransactionSplit getWritableExpensesSplit(KMMAcctID expAcctID)  throws TransactionSplitNotFoundException {
    	for ( KMyMoneyWritableTransactionSplit splt : getWritableExpensesSplits() ) {
    		if ( splt.getAccountID().getStdID().equals( expAcctID ) ) {
    			return splt;
    		}
    	}
    	
    	throw new TransactionSplitNotFoundException();
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
    public KMyMoneyTransactionSplit getExpensesSplit(KMMAcctID expAcctID)  throws TransactionSplitNotFoundException {
    	for ( KMyMoneyTransactionSplit splt : getExpensesSplits() ) {
    		if ( splt.getAccountID().getStdID().equals( expAcctID ) ) {
    			return splt;
    		}
    	}
    	
    	throw new TransactionSplitNotFoundException();
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
		return getPricePerShare_Var2();
	}

	private FixedPointNumber getPricePerShare_Var1() throws TransactionSplitNotFoundException {
		FixedPointNumber result = getNetPrice();
		
		result.divide( getNofShares() ); // mutable
		
		return result;
	}

	private FixedPointNumber getPricePerShare_Var2() throws TransactionSplitNotFoundException {
		return getStockAccountSplit().getPrice();
	}

	@Override
	public BigFraction getPricePerShareRat() throws TransactionSplitNotFoundException {
    	BigFraction result = getNetPriceRat();
		
		result = result.divide( getNofSharesRat() ); // immutable
		
		return result;
	}

	@Override
	public FixedPointNumber getNetPrice() throws TransactionSplitNotFoundException {
		return getNetPrice_Var1();
	}

	private FixedPointNumber getNetPrice_Var1() throws TransactionSplitNotFoundException {
		FixedPointNumber result = getGrossPrice();
		
		result.subtract( getFeesTaxes() ); // mutable
		
		return result;
	}

	private FixedPointNumber getNetPrice_Var2() throws TransactionSplitNotFoundException {
		FixedPointNumber result = getNofShares().multiply( getPricePerShare() );
		
		return result;
	}

	@Override
	public BigFraction getNetPriceRat() throws TransactionSplitNotFoundException {
		return getNetPriceRat_Var1();
	}

	private BigFraction getNetPriceRat_Var1() throws TransactionSplitNotFoundException {
		BigFraction result = getGrossPriceRat();
		
		result = result.subtract( getFeesTaxesRat() ); // immutable
		
		return result;
	}

	private BigFraction getNetPriceRat_Var2() throws TransactionSplitNotFoundException {
		BigFraction result = getNofSharesRat().multiply( getPricePerShareRat() );
		
		return result;
	}

	@Override
	public FixedPointNumber getFeeTax(final KMMAcctID expAcctID) throws TransactionSplitNotFoundException {
		for ( KMyMoneyTransactionSplit splt : getExpensesSplits() ) {
			if ( splt.getAccountID().getStdID().equals( expAcctID ) ) {
				return splt.getValue();
			}
		}
		
		return FixedPointNumber.ZERO.copy(); // mutable
	}

	@Override
	public BigFraction getFeeTaxRat(final KMMAcctID expAcctID) throws TransactionSplitNotFoundException {
		for ( KMyMoneyTransactionSplit splt : getExpensesSplits() ) {
			if ( splt.getAccountID().getStdID().equals( expAcctID ) ) {
				return splt.getValueRat();
			}
		}
		
		return BigFraction.ZERO;
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
	public void setStockAcctID(KMMAcctID stockAcctID) throws TransactionSplitNotFoundException {
		if ( stockAcctID == null ) {
			throw new IllegalArgumentException("argument <stockAcctID> is null");
		}
		
		if ( ! stockAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <stockAcctID> is not set");
		}
		
		// ---
		
		KMyMoneyAccount stockAcct = getKMyMoneyFile().getAccountByID(stockAcctID);
		if ( stockAcct == null ) {
			LOGGER.error("setStockAcctID: " +
					"Stock-buy transaction " + getID() + ": " +
					"Could not find account with ID " + stockAcctID);
			throw new IllegalStateException("Could not find account with ID " + stockAcctID);
		}
		
		// ---
		
		setStockAcct(stockAcct);
	}

	@Override
	public void setStockAcct(KMyMoneyAccount stockAcct) throws TransactionSplitNotFoundException {
		if ( stockAcct == null ) {
			throw new IllegalArgumentException("argument <stockAcct> is null");
		}
		
		// ---
		
		if ( stockAcct.getType() != KMyMoneyAccount.Type.STOCK ) {
			LOGGER.error("setStockAcct: " +
						"Stock-buy transaction " + getID() + ": " +
						"Account with ID " + stockAcct.getID() + " is not of type " + KMyMoneyAccount.Type.STOCK);
			throw new IllegalArgumentException("Account with ID " + stockAcct.getID() + " is not of type " + KMyMoneyAccount.Type.STOCK);
		}
		
		boolean acctChange = false;
		KMMComplAcctID oldAcctID = getStockAccountSplit().getAccountID();
		if ( ! oldAcctID.equals( stockAcct.getID() ) ) {
			acctChange = true;
		}
		if ( acctChange ) {
			LOGGER.debug("setStockAcct: " +
						"Stock-buy transaction " + getID() + ": " +
						"Changing offsetting account ID from " + oldAcctID + " to " + stockAcct.getID());
		}

		// ---
		
		getWritableStockAccountSplit().setAccountID(stockAcct.getID());
	}
	
	// ----------------------------

	@Override
	public void setOffsetttingAcctID(KMMAcctID offsettingAcctID) throws TransactionSplitNotFoundException
	{
		if ( offsettingAcctID == null ) {
			throw new IllegalArgumentException("argument <offsettingAcctID> is null");
		}
		
		if ( ! offsettingAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <offsettingAcctID> is not set");
		}
		
		// ---
		
		KMyMoneyAccount offsettingAcct = getKMyMoneyFile().getAccountByID(offsettingAcctID);
		if ( offsettingAcct == null ) {
			LOGGER.error("setStockAcctID: " +
					"Stock-buy transaction " + getID() + ": " +
					"Could not find account with ID " + offsettingAcctID);
			throw new IllegalStateException("Could not find account with ID " + offsettingAcctID);
		}
		
		// ---
		
		setOffsetttingAcct(offsettingAcct);
	}

	@Override
	public void setOffsetttingAcct(KMyMoneyAccount offsettingAcct) throws TransactionSplitNotFoundException
	{
		if ( offsettingAcct == null ) {
			throw new IllegalArgumentException("argument <offsettingAcct> is null");
		}
		
		// ---
		
		if ( offsettingAcct.getType() != KMyMoneyAccount.Type.CHECKING ) {
			LOGGER.error("setOffsetttingAcct: " +
						"Stock-buy transaction " + getID() + ": " +
						"Account with ID " + offsettingAcct.getID() + " is not of type " + KMyMoneyAccount.Type.CHECKING);
			throw new IllegalArgumentException("Account with ID " + offsettingAcct.getID() + " is not of type " + KMyMoneyAccount.Type.CHECKING);
		}
		
		boolean acctChange = false;
		KMMComplAcctID oldAcctID = getStockAccountSplit().getAccountID();
		if ( ! oldAcctID.equals( offsettingAcct.getID() ) ) {
			acctChange = true;
		}
		if ( acctChange ) {
			LOGGER.debug("setOffsetttingAcct: " +
						"Stock-buy transaction " + getID() + ": " +
						"Changing offsetting account ID from " + oldAcctID + " to " + offsettingAcct.getID());
		}

		// ---
		
		getWritableOffsettingAccountSplit().setAccountID(offsettingAcct.getID());
	}

    // ---------------------------------------------------------------
    
	@Override
	public void setNofShares(final FixedPointNumber val) throws TransactionSplitNotFoundException {
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.compareTo(FixedPointNumber.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <val> is <= 0");
		}
		
		// ---
		
		getWritableStockAccountSplit().setShares(val);
	}

	@Override
	public void setNofShares(final BigFraction val) throws TransactionSplitNotFoundException {
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( val.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <val> is <= 0");
		}
		
		// ---
		
		getWritableStockAccountSplit().setShares(val);
	}

	// ----------------------------

	@Override
	public void setNofShares(KMMAcctID stockAcctID, FixedPointNumber val) throws TransactionSplitNotFoundException {
		if ( stockAcctID == null ) {
			throw new IllegalArgumentException("argument <stockAcctID> is null");
		}
		
		if ( ! stockAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <stockAcctID> is not set");
		}
		
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		if ( val.compareTo(FixedPointNumber.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <val> is <= 0");
		}
		
		// ---
		
		setStockAcctID(stockAcctID);
		setNofShares(val);
	}

	@Override
	public void setNofShares(KMMAcctID stockAcctID, BigFraction val) throws TransactionSplitNotFoundException {
		if ( stockAcctID == null ) {
			throw new IllegalArgumentException("argument <stockAcctID> is null");
		}
		
		if ( ! stockAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <stockAcctID> is not set");
		}
		
		if ( val == null ) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		if ( val.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <val> is <= 0");
		}
		
		// ---
		
		setStockAcctID(stockAcctID);
		setNofShares(val);
	}
	
	// ----------------------------

	@Override
	public void setPricePerShare(final FixedPointNumber amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(FixedPointNumber.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		// ---
		
		getWritableStockAccountSplit().setPrice(amt);
		
		FixedPointNumber netPrc = getNofShares().multiply(amt);
		getWritableStockAccountSplit().setValue(netPrc);
	}

	@Override
	public void setPricePerShare(final BigFraction amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		// ---
		
		getWritableStockAccountSplit().setPrice(amt);
		
		BigFraction netPrc = getNofSharesRat().multiply(amt);
		getWritableStockAccountSplit().setValue(netPrc);
	}
	
	// ----------------------------

	@Override
	public void setPricePerShare(KMMAcctID stockAcctID, FixedPointNumber amt) throws TransactionSplitNotFoundException {
		if ( stockAcctID == null ) {
			throw new IllegalArgumentException("argument <stockAcctID> is null");
		}
		
		if ( ! stockAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <stockAcctID> is not set");
		}
		
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(FixedPointNumber.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		// ---
		
		setStockAcctID(stockAcctID);
		setPricePerShare(amt);
	}

	@Override
	public void setPricePerShare(KMMAcctID stockAcctID, BigFraction amt) throws TransactionSplitNotFoundException {
		if ( stockAcctID == null ) {
			throw new IllegalArgumentException("argument <stockAcctID> is null");
		}
		
		if ( ! stockAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <stockAcctID> is not set");
		}
		
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		// ---
		
		setStockAcctID(stockAcctID);
		setPricePerShare(amt);
	}

	// ----------------------------

	@Override
	public void addFeeTax(KMMAcctID expAcctID, FixedPointNumber amt) throws TransactionSplitNotFoundException {
		if ( expAcctID == null ) {
			throw new IllegalArgumentException("argument <expAcctID> is null");
		}
		
		if ( ! expAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <expAcctID> is not set");
		}
		
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.equals(FixedPointNumber.ZERO) ) {
			throw new IllegalArgumentException("argument <amt> is = 0");
		}
		
		// ---
		
		KMyMoneyAccount expAcct = getKMyMoneyFile().getAccountByID(expAcctID);
		if ( expAcct == null ) {
			LOGGER.error("addFeeTax: " +
					"Stock-buy transaction " + getID() + ": " +
					"Could not find account with ID " + expAcctID);
			throw new IllegalStateException("Could not find account with ID " + expAcctID);
		}
		
		if ( expAcct.getType() != KMyMoneyAccount.Type.EXPENSE ) {
			LOGGER.error("addFeeTax: " +
						"Stock-buy transaction " + getID() + ": " +
						"Account with ID " + expAcct.getID() + " is not of type " + KMyMoneyAccount.Type.EXPENSE);
			throw new IllegalArgumentException("Account with ID " + expAcct.getID() + " is not of type " + KMyMoneyAccount.Type.EXPENSE);
		}
		
		// ---

		KMyMoneyWritableTransactionSplit expSplt = null;
		for ( KMyMoneyWritableTransactionSplit splt : getWritableExpensesSplits() ) {
			if ( splt.getAccountID().getStdID().equals( expAcctID ) ) {
				expSplt = splt;
				LOGGER.warn("addFeeTax: " +
						"Stock-buy transaction " + getID() + ": " +
						"Created new split for account " + expAcctID + ": " + splt.getID());
				LOGGER.warn("addFeeTax: Will overwrite data");
				break;
			}
		}
		if ( expSplt == null ) {
			expSplt = createWritableSplit(expAcct);
			LOGGER.debug("addFeeTax: " +
						"Stock-buy transaction " + getID() + ": " +
						"Created new split for account " + expAcctID + ": " + expSplt.getID());
		}
		
		expSplt.setShares(amt);
		expSplt.setValue(amt);
	}

	@Override
	public void addFeeTax(KMMAcctID expAcctID, BigFraction amt) throws TransactionSplitNotFoundException {
		if ( expAcctID == null ) {
			throw new IllegalArgumentException("argument <expAcctID> is null");
		}
		
		if ( ! expAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <expAcctID> is not set");
		}
		
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		if ( amt.equals(BigFraction.ZERO) ) {
			throw new IllegalArgumentException("argument <amt> is = 0");
		}
		
		// ---
		
		KMyMoneyAccount expAcct = getKMyMoneyFile().getAccountByID(expAcctID);
		if ( expAcct == null ) {
			LOGGER.error("addFeeTax: " +
					"Stock-buy transaction " + getID() + ": " +
					"Could not find account with ID " + expAcctID);
			throw new IllegalStateException("Could not find account with ID " + expAcctID);
		}
		
		if ( expAcct.getType() != KMyMoneyAccount.Type.EXPENSE ) {
			LOGGER.error("addFeeTax: " +
						"Stock-buy transaction " + getID() + ": " +
						"Account with ID " + expAcct.getID() + " is not of type " + KMyMoneyAccount.Type.EXPENSE);
			throw new IllegalArgumentException("Account with ID " + expAcct.getID() + " is not of type " + KMyMoneyAccount.Type.EXPENSE);
		}
		
		// ---

		KMyMoneyWritableTransactionSplit expSplt = null;
		for ( KMyMoneyWritableTransactionSplit splt : getWritableExpensesSplits() ) {
			if ( splt.getAccountID().getStdID().equals( expAcctID ) ) {
				expSplt = splt;
				LOGGER.warn("addFeeTax: " +
						"Stock-buy transaction " + getID() + ": " +
						"Created new split for account " + expAcctID + ": " + splt.getID());
				LOGGER.warn("addFeeTax: Will overwrite data");
				break;
			}
		}
		if ( expSplt == null ) {
			expSplt = createWritableSplit(expAcct);
			LOGGER.debug("addFeeTax: " +
						"Stock-buy transaction " + getID() + ": " +
						"Created new split for account " + expAcctID + ": " + expSplt.getID());
		}
		
		expSplt.setShares(amt);
		expSplt.setValue(amt);
	}

	@Override
	public void clearFeesTaxes() throws TransactionSplitNotFoundException {
		for ( KMyMoneyWritableTransactionSplit splt : getWritableExpensesSplits() ) {
			getKMyMoneyFile().removeTransactionSplit(splt);
		}
	}

	// ----------------------------

	@Override
	public void setGrossPrice(FixedPointNumber amt) throws TransactionSplitNotFoundException {
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( amt.compareTo(FixedPointNumber.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		// ---
		
		FixedPointNumber amtNeg = amt.copy().negate(); // mutable
		
		getWritableOffsettingAccountSplit().setShares(amtNeg);
		getWritableOffsettingAccountSplit().setValue(amtNeg);
	}
	
	@Override
	public void setGrossPrice(BigFraction amt) throws TransactionSplitNotFoundException	{
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( amt.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		// ---
		
		BigFraction amtNeg = amt.negate(); // immutable
		
		getWritableOffsettingAccountSplit().setShares(amtNeg);
		getWritableOffsettingAccountSplit().setValue(amtNeg);
	}

	// ----------------------------

	@Override
	public void setGrossPrice(final KMMAcctID offsettingAcctID, final FixedPointNumber amt) throws TransactionSplitNotFoundException {
		if ( offsettingAcctID == null ) {
			throw new IllegalArgumentException("argument <offsettingAcctID> is null");
		}
		
		if ( ! offsettingAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <offsettingAcctID> is not set");
		}
		
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( amt.compareTo(FixedPointNumber.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		// ---
		
		setOffsetttingAcctID(offsettingAcctID);
		setGrossPrice(amt);
	}

	@Override
	public void setGrossPrice(final KMMAcctID offsettingAcctID, final BigFraction amt) throws TransactionSplitNotFoundException {
		if ( offsettingAcctID == null ) {
			throw new IllegalArgumentException("argument <offsettingAcctID> is null");
		}
		
		if ( ! offsettingAcctID.isSet() ) {
			throw new IllegalArgumentException("argument <offsettingAcctID> is not set");
		}
		
		if ( amt == null ) {
			throw new IllegalArgumentException("argument <amt> is null");
		}
		
		// CAUTION: < 0 is valid (adjustment posting)
		if ( amt.compareTo(BigFraction.ZERO) <= 0 ) {
			throw new IllegalArgumentException("argument <amt> is <= 0");
		}
		
		// ---
		
		setOffsetttingAcctID(offsettingAcctID);
		setGrossPrice(amt);
	}

	// ----------------------------
	
	@Override
	public void refreshGrossPrice() throws TransactionSplitNotFoundException
	{
		FixedPointNumber grossPrc = getNetPrice_Var2().add( getFeesTaxes() ); // <-- important: Var2
		setGrossPrice(grossPrc);
	}

	// ---------------------------------------------------------------
	
	@Override
    // ::TODO: Redundant to KMyMoneyStockBuyTransactionImpl.validate()
	// (as well as the following helper functions)
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
		
//		if ( splt.getPrice().doubleValue() != 1.0 ) {
//			String msg = "the split's price is not valid";
//			LOGGER.error("validateStockAcctSplit: " + msg);
//			throw new TransactionValidationException(msg);
//		}
		
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
