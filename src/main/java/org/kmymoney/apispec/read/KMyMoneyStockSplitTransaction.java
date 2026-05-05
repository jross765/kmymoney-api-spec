package org.kmymoney.apispec.read;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface KMyMoneyStockSplitTransaction extends KMyMoneyTransaction,
													   KMyMoneySpecialTransaction
{

	KMyMoneyTransactionSplit getSplit() throws TransactionSplitNotFoundException;
	
    // ---------------------------------------------------------------
    
	@Deprecated
    FixedPointNumber getSplitFactor()  throws TransactionSplitNotFoundException;
    
    BigFraction      getSplitFactorRat()  throws TransactionSplitNotFoundException;
    
	@Deprecated
    FixedPointNumber getNofAddShares()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNofAddSharesRat()  throws TransactionSplitNotFoundException;
    
	@Deprecated
    FixedPointNumber getNofSharesBeforeSplit()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNofSharesBeforeSplitRat()  throws TransactionSplitNotFoundException;
    
	@Deprecated
    FixedPointNumber getNofSharesAfterSplit()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNofSharesAfterSplitRat()  throws TransactionSplitNotFoundException;
    
}
