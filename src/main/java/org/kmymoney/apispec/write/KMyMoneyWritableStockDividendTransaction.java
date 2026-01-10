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
    
//    void setGrossDividend(FixedPointNumber amt)  throws TransactionSplitNotFoundException;
//    
//    void setGrossDividend(BigFraction amt)  throws TransactionSplitNotFoundException;
//    
//    void setFeesTaxes(FixedPointNumber amt)  throws TransactionSplitNotFoundException;
//    
//    void setFeesTaxes(BigFraction amt)  throws TransactionSplitNotFoundException;
    
    void setNetDividend(FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void setNetDividend(BigFraction amt)  throws TransactionSplitNotFoundException;
    
}
