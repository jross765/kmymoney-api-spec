package org.kmymoney.apispec.write;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.apispec.read.KMyMoneySimpleTransaction;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * xyz.
 * 
 * @see KMyMoneySimpleTransaction
 */
public interface KMyMoneyWritableSimpleTransaction extends KMyMoneyWritableTransaction,
                                                           KMyMoneySimpleTransaction
{

    /**
     * @return the first of the two splits.
     * @throws TransactionSplitNotFoundException 
     *  
     * @see #getWritableSecondSplit()
     */
    KMyMoneyWritableTransactionSplit getWritableFirstSplit() throws TransactionSplitNotFoundException;
    
    /**
     * @return the second of the two splits.
     * @throws TransactionSplitNotFoundException 
     *  
     * @see #getWritableFirstSplit()
     */
    KMyMoneyWritableTransactionSplit getWritableSecondSplit() throws TransactionSplitNotFoundException;

    // ---------------------------------------------------------------
    
    void setAmount(FixedPointNumber amt) throws TransactionSplitNotFoundException;

    void setAmount(BigFraction amt) throws TransactionSplitNotFoundException;

}
