package org.kmymoney.apispec.write;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.apispec.read.KMyMoneySimpleTransaction;
import org.kmymoney.apispec.read.KMyMoneyStockSplitTransaction;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * xyz.
 * 
 * @see KMyMoneySimpleTransaction
 */
public interface KMyMoneyWritableStockSplitTransaction extends KMyMoneyWritableTransaction,
                                                               KMyMoneyStockSplitTransaction
{

	KMyMoneyWritableTransactionSplit getWritableSplit() throws TransactionSplitNotFoundException;
	
    // ---------------------------------------------------------------
    
    void setSplitFactor(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setSplitFactor(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setNofAddShares(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setNofAddShares(BigFraction val)  throws TransactionSplitNotFoundException;
    
//    void setNofSharesBeforeSplit(FixedPointNumber val)  throws TransactionSplitNotFoundException;
//    
//    void setNofSharesBeforeSplit(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setNofSharesAfterSplit(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setNofSharesAfterSplit(BigFraction val)  throws TransactionSplitNotFoundException;
    
}
