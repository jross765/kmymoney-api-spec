package org.kmymoney.apispec.read;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface KMyMoneyStockSplitTransaction extends KMyMoneyTransaction,
													   KMyMoneySpecialTransaction
{

	public KMyMoneyTransactionSplit getSplit() throws TransactionSplitNotFoundException;
	
    // ---------------------------------------------------------------
    
    FixedPointNumber getSplitFactor()  throws TransactionSplitNotFoundException;
    
    BigFraction      getSplitFactorRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getNofAddShares()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNofAddSharesRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getNofSharesBeforeSplit()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNofSharesBeforeSplitRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getNofSharesAfterSplit()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNofSharesAfterSplitRat()  throws TransactionSplitNotFoundException;
    
}
