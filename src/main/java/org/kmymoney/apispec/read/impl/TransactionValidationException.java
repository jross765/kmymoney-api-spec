package org.kmymoney.apispec.read.impl;

public class TransactionValidationException extends Exception
{

	private static final long serialVersionUID = -3252763001717417103L;
	
	// ---------------------------------------------------------------
	
	/*
	public TransactionValidationException() {
		super();
	}
	*/

	public TransactionValidationException(String msg) {
		super(msg);
	}

}
