package org.kmymoney.apispec.write;

import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.apispec.read.KMyMoneySimpleTransaction;
import org.kmymoney.apispec.read.KMyMoneyStockDividendTransaction;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * xyz.
 * 
 * @see KMyMoneySimpleTransaction
 */
public interface KMyMoneyWritableStockDividendTransaction extends KMyMoneyWritableTransaction,
                                                                  KMyMoneyStockDividendTransaction
{

    KMyMoneyWritableTransactionSplit       getWritableStockAccountSplit()  throws TransactionSplitNotFoundException;
    
    KMyMoneyWritableTransactionSplit       getWritableIncomeAccountSplit()  throws TransactionSplitNotFoundException;
    
    List<KMyMoneyWritableTransactionSplit> getWritableExpensesSplits()  throws TransactionSplitNotFoundException;
    
    KMyMoneyWritableTransactionSplit       getWritableOffsettingAccountSplit()  throws TransactionSplitNotFoundException;
    
    // ---------------------------------------------------------------
    
    void setGrossDividend(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setGrossDividend(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setFeesTaxes(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setFeesTaxes(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setNetDividend(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setNetDividend(BigFraction val)  throws TransactionSplitNotFoundException;
    
}
