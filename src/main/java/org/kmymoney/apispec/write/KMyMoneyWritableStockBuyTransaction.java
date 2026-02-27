package org.kmymoney.apispec.write;

import java.util.List;

import org.apache.commons.numbers.fraction.BigFraction;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.apispec.read.KMyMoneySimpleTransaction;
import org.kmymoney.apispec.read.KMyMoneyStockBuyTransaction;
import org.kmymoney.base.basetypes.simple.KMMAcctID;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * xyz.
 * 
 * @see KMyMoneySimpleTransaction
 */
public interface KMyMoneyWritableStockBuyTransaction extends KMyMoneyWritableTransaction,
                                                             KMyMoneyStockBuyTransaction
{

    KMyMoneyWritableTransactionSplit       getWritableStockAccountSplit()  throws TransactionSplitNotFoundException;
    
    KMyMoneyWritableTransactionSplit       getWritableExpensesSplit(KMMAcctID expAcctID)  throws TransactionSplitNotFoundException;
    
    List<KMyMoneyWritableTransactionSplit> getWritableExpensesSplits()  throws TransactionSplitNotFoundException;
    
    KMyMoneyWritableTransactionSplit       getWritableOffsettingAccountSplit()  throws TransactionSplitNotFoundException;
    
    // ---------------------------------------------------------------
    
    void setStockAcctID(KMMAcctID stockAcctID) throws TransactionSplitNotFoundException;
    
    void setStockAcct(KMyMoneyAccount stockAcct) throws TransactionSplitNotFoundException;
    
    void setOffsetttingAcctID(KMMAcctID offsettingAcctID) throws TransactionSplitNotFoundException;
    
    void setOffsetttingAcct(KMyMoneyAccount pffsettingAcct) throws TransactionSplitNotFoundException;
    
    // ----------------------------
    
    void setNofShares(FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setNofShares(BigFraction val)  throws TransactionSplitNotFoundException;
    
    void setNofShares(KMMAcctID stockAcctID, FixedPointNumber val)  throws TransactionSplitNotFoundException;
    
    void setNofShares(KMMAcctID stockAcctID, BigFraction val)  throws TransactionSplitNotFoundException;
    
    // ---
    
    void setPricePerShare(FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void setPricePerShare(BigFraction amt)  throws TransactionSplitNotFoundException;
    
    void setPricePerShare(KMMAcctID stockAcctID, FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void setPricePerShare(KMMAcctID stockAcctID, BigFraction amt)  throws TransactionSplitNotFoundException;
    
    // ----------------------------
    
    void addFeeTax(KMMAcctID expAcctID, FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void addFeeTax(KMMAcctID expAcctID, BigFraction amt)  throws TransactionSplitNotFoundException;
    
    // ---
    
    void clearFeesTaxes() throws TransactionSplitNotFoundException;
    
    // ----------------------------
    
    void setGrossPrice(FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void setGrossPrice(BigFraction amt)  throws TransactionSplitNotFoundException;
    
    void setGrossPrice(KMMAcctID offsettingAcctID, FixedPointNumber amt)  throws TransactionSplitNotFoundException;
    
    void setGrossPrice(KMMAcctID offsettingAcctID, BigFraction amt)  throws TransactionSplitNotFoundException;
    
    // ---
    
    void refreshGrossPrice() throws TransactionSplitNotFoundException;
    
}
