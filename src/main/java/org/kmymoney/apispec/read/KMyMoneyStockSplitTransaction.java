package org.kmymoney.apispec.read;

import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;

public interface KMyMoneyStockSplitTransaction extends KMyMoneyTransaction
{

	public KMyMoneyTransactionSplit getSplit() throws TransactionSplitNotFoundException;
	
}
