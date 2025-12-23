package org.kmymoney.apispec.write;

import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.apispec.read.KMyMoneySimpleTransaction;
import org.kmymoney.apispec.read.KMyMoneyStockBuyTransaction;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * xyz.
 * 
 * @see KMyMoneySimpleTransaction
 */
public interface KMyMoneyWritableStockBuyTransaction extends KMyMoneyWritableTransaction,
                                                             KMyMoneyStockBuyTransaction
{

    KMyMoneyWritableTransactionSplit       getWritableStockAccountSplit()  throws TransactionSplitNotFoundException;
    
    List<KMyMoneyWritableTransactionSplit> getWritableExpensesSplits()  throws TransactionSplitNotFoundException;
    
    KMyMoneyWritableTransactionSplit       getWritableOffsettingAccountSplit()  throws TransactionSplitNotFoundException;
    
    // ---------------------------------------------------------------
    
    void setNofShares(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setNofShares(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setPricePerShare(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setPricePerShare(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setNetPrice(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setNetPrice(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setFeesTaxes(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setFeesTaxes(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setGrossPrice(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setGrossPrice(BigFraction val)  throws TransactionSplitNotFoundException;
    
}
