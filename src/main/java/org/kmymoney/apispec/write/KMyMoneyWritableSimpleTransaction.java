package org.kmymoney.apispec.write;

import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.apispec.read.KMyMoneySimpleTransaction;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;

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

}
