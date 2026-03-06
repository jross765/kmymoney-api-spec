package org.kmymoney.apispec.write;

import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.apispec.read.KMyMoneySimpleTransaction;
import org.kmymoney.apispec.read.KMyMoneyStockDividendTransaction;
import org.kmymoney.base.basetypes.simple.KMMAcctID;

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
    
    KMyMoneyWritableTransactionSplit       getWritableExpensesSplit(KMMAcctID expAcctID)  throws TransactionSplitNotFoundException;
    
    List<KMyMoneyWritableTransactionSplit> getWritableExpensesSplits()  throws TransactionSplitNotFoundException;
    
    KMyMoneyWritableTransactionSplit       getWritableOffsettingAccountSplit()  throws TransactionSplitNotFoundException;
    
    // ---------------------------------------------------------------
    
    void setStockAcctID(KMMAcctID stockAcctID) throws TransactionSplitNotFoundException;
    
    void setStockAcct(KMyMoneyAccount stockAcct) throws TransactionSplitNotFoundException;
    
    void setOffsetttingAcctID(KMMAcctID offsettingAcctID) throws TransactionSplitNotFoundException;
    
    void setOffsetttingAcct(KMyMoneyAccount pffsettingAcct) throws TransactionSplitNotFoundException;
    
    // ----------------------------
    
    void setGrossDividend(FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void setGrossDividend(BigFraction amt)  throws TransactionSplitNotFoundException;
    
    // ----------------------------
    
    void addFeeTax(KMMAcctID expAcctID, FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void addFeeTax(KMMAcctID expAcctID, BigFraction amt)  throws TransactionSplitNotFoundException;
    
    // ---
    
    void clearFeesTaxes() throws TransactionSplitNotFoundException;
    
    // ----------------------------
    
    void setNetDividend(FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void setNetDividend(BigFraction amt)  throws TransactionSplitNotFoundException;
    
    // ---
    
    void refreshNetDividend() throws TransactionSplitNotFoundException;
    
}
