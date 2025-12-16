package org.kmymoney.apispec.read;

import java.util.List;

import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;

public interface KMyMoneyStockDividendTransaction extends KMyMoneyTransaction,
												          KMyMoneySpecialTransaction
{

    public KMyMoneyTransactionSplit       getStockAccountSplit()  throws TransactionSplitNotFoundException;
    
    public KMyMoneyTransactionSplit       getIncomeAccountSplit()  throws TransactionSplitNotFoundException;
    
    public List<KMyMoneyTransactionSplit> getExpensesSplits()  throws TransactionSplitNotFoundException;
    
    public KMyMoneyTransactionSplit       getOffsettingAccountSplit()  throws TransactionSplitNotFoundException;
    
}
