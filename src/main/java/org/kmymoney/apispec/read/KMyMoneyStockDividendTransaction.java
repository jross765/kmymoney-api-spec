package org.kmymoney.apispec.read;

import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.base.basetypes.simple.KMMAcctID;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface KMyMoneyStockDividendTransaction extends KMyMoneyTransaction,
												          KMyMoneySpecialTransaction
{

    KMyMoneyTransactionSplit       getStockAccountSplit()  throws TransactionSplitNotFoundException;
    
    KMyMoneyTransactionSplit       getIncomeAccountSplit()  throws TransactionSplitNotFoundException;
    
    KMyMoneyTransactionSplit       getExpensesSplit(KMMAcctID expAcctID)  throws TransactionSplitNotFoundException;
    
    List<KMyMoneyTransactionSplit> getExpensesSplits()  throws TransactionSplitNotFoundException;
    
    KMyMoneyTransactionSplit       getOffsettingAccountSplit()  throws TransactionSplitNotFoundException;
    
    // ---------------------------------------------------------------
    
    FixedPointNumber getGrossDividend()  throws TransactionSplitNotFoundException;
    
    BigFraction      getGrossDividendRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getFeeTax(KMMAcctID expAcctID)  throws TransactionSplitNotFoundException;
    
    BigFraction      getFeeTaxRat(KMMAcctID expAcctID)  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getFeesTaxes()  throws TransactionSplitNotFoundException;
    
    BigFraction      getFeesTaxesRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getNetDividend()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNetDividendRat()  throws TransactionSplitNotFoundException;
    
}
