package org.kmymoney.apispec.read;

import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.base.basetypes.simple.KMMAcctID;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface KMyMoneyStockBuyTransaction extends KMyMoneyTransaction,
												     KMyMoneySpecialTransaction
{

    KMyMoneyTransactionSplit       getStockAccountSplit()  throws TransactionSplitNotFoundException;
    
    KMyMoneyTransactionSplit       getExpensesSplit(KMMAcctID expAcctID)  throws TransactionSplitNotFoundException;
    
    List<KMyMoneyTransactionSplit> getExpensesSplits()  throws TransactionSplitNotFoundException;
    
    KMyMoneyTransactionSplit       getOffsettingAccountSplit()  throws TransactionSplitNotFoundException;
    
    // ---------------------------------------------------------------
    
    FixedPointNumber getNofShares()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNofSharesRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getPricePerShare()  throws TransactionSplitNotFoundException;
    
    BigFraction      getPricePerShareRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getNetPrice()  throws TransactionSplitNotFoundException;
    
    BigFraction      getNetPriceRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getFeeTax(KMMAcctID expAcctID)  throws TransactionSplitNotFoundException;
    
    BigFraction      getFeeTaxRat(KMMAcctID expAcctID)  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getFeesTaxes()  throws TransactionSplitNotFoundException;
    
    BigFraction      getFeesTaxesRat()  throws TransactionSplitNotFoundException;
    
    FixedPointNumber getGrossPrice()  throws TransactionSplitNotFoundException;
    
    BigFraction      getGrossPriceRat()  throws TransactionSplitNotFoundException;
    
}
