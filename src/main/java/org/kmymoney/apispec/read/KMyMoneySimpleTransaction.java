package org.kmymoney.apispec.read;

import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;

public interface KMyMoneySimpleTransaction extends KMyMoneyTransaction
{

    /**
     *
     * @return the first split of this transaction or null.
     * <br>
     * <em>Caution</em>: This only makes sense for simple transactions
     * that consist of only two splits. 
     * By no means is that guaranteed or even "normal"!
     *  
     * @throws TransactionSplitNotFoundException
     * 
     * @see #getSecondSplit()
     * @see #getSplits()
     * @see #getSplitsCount()
    */
    public KMyMoneyTransactionSplit getFirstSplit()  throws TransactionSplitNotFoundException;
    
    /**
     * @return the second split of this transaction or null.
     * <br>
     * <em>Caution</em>: This only makes sense for simple transactions
     * that consist of only two splits. 
     * By no means is that guaranteed or even "normal"!
     * 
     * @throws TransactionSplitNotFoundException 
     *
     * @see #getFirstSplit()
     * @see #getSplits()
     * @see #getSplitsCount()
     */
	public KMyMoneyTransactionSplit getSecondSplit() throws TransactionSplitNotFoundException;

}
