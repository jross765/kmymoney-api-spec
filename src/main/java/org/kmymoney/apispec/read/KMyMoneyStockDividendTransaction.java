package org.kmymoney.apispec.read;

import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface KMyMoneyStockDividendTransaction extends KMyMoneyTransaction,
												          KMyMoneySpecialTransaction
{

    public KMyMoneyTransactionSplit       getStockAccountSplit()  throws TransactionSplitNotFoundException;
    
    public KMyMoneyTransactionSplit       getIncomeAccountSplit()  throws TransactionSplitNotFoundException;
    
    public List<KMyMoneyTransactionSplit> getExpensesSplits()  throws TransactionSplitNotFoundException;
    
    public KMyMoneyTransactionSplit       getOffsettingAccountSplit()  throws TransactionSplitNotFoundException;
    
    // ---------------------------------------------------------------
    
    FixedPointNumber getGrossDividend()  throws TransactionSplitNotFoundException;
    
    BigFraction      getGrossDividendRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getFeesTaxes()  throws TransactionSplitNotFoundException;
    
    BigFraction      getFeesTaxesRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getNetDividend()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNetDividendRat()  throws TransactionSplitNotFoundException;
    
}
