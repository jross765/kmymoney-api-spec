package org.kmymoney.apispec.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.net.URL;

import org.apache.commons.numbers.fraction.BigFraction;
import org.junit.Before;
import org.junit.Test;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.apispec.ConstTest;
import org.kmymoney.apispec.read.KMyMoneyStockDividendTransaction;
import org.kmymoney.base.basetypes.simple.KMMTrxID;

import junit.framework.JUnit4TestAdapter;

public class TestKMyMoneyStockDividendTransactionImpl {
	
	public static final KMMTrxID TRX_1_ID = new KMMTrxID("T000000000000000020");
	public static final KMMTrxID TRX_4_ID = new KMMTrxID("T000000000000000001");
	
	// -----------------------------------------------------------------

	private KMyMoneyFile kmmFile = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestKMyMoneyStockDividendTransactionImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL kmmFileURL = classLoader.getResource(Const.KMM_FILENAME);
		// System.err.println("KMyMoney test file resource: '" + kmmFileURL + "'");
		URL kmmFileURL = null;
		File kmmFileRaw = null;
		try {
			kmmFileURL = classLoader.getResource(ConstTest.KMM_FILENAME);
			kmmFileRaw = new File(kmmFileURL.getFile());
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			kmmFile = new KMyMoneyFileImpl(kmmFileRaw);
		} catch (Exception exc) {
			System.err.println("Cannot parse KMyMoney file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		KMyMoneyTransaction genTrx = kmmFile.getTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);

		assertEquals(TRX_4_ID, genTrx.getID());
		assertEquals(2, genTrx.getSplitsCount());
		
		try {
			KMyMoneyStockDividendTransaction specTrx = new KMyMoneyStockDividendTransactionImpl((KMyMoneyTransactionImpl) genTrx);
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test02() throws Exception {
		KMyMoneyTransaction genTrx = kmmFile.getTransactionByID(TRX_1_ID);
		assertNotEquals(null, genTrx);

		assertEquals(TRX_1_ID, genTrx.getID());
		
		KMyMoneyStockDividendTransaction specTrx = new KMyMoneyStockDividendTransactionImpl((KMyMoneyTransactionImpl) genTrx);
		assertNotEquals(null, specTrx);
		
		assertEquals(5, specTrx.getSplitsCount());
		
		assertEquals("S0002", specTrx.getStockAccountSplit().getID().toString());
		assertEquals("S0005", specTrx.getIncomeAccountSplit().getID().toString());
		assertEquals(2, specTrx.getExpensesSplits().size());
		assertEquals("S0003", specTrx.getExpensesSplits().get(0).getID().toString());
		assertEquals("S0004", specTrx.getExpensesSplits().get(1).getID().toString());
		assertEquals("S0001", specTrx.getOffsettingAccountSplit().getID().toString());
		
		assertEquals(specTrx.getSplits().get(0).toString(), specTrx.getOffsettingAccountSplit().toString());
		assertEquals(specTrx.getSplits().get(1).toString(), specTrx.getStockAccountSplit().toString());
		assertEquals(specTrx.getSplits().get(2).toString(), specTrx.getExpensesSplits().get(0).toString());
		assertEquals(specTrx.getSplits().get(3).toString(), specTrx.getExpensesSplits().get(1).toString());
		assertEquals(specTrx.getSplits().get(4).toString(), specTrx.getIncomeAccountSplit().toString());
		
		// ---
		
		assertEquals(15.0, specTrx.getGrossDividend().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(4.35, specTrx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(10.65, specTrx.getNetDividend().doubleValue(), ConstTest.DIFF_TOLERANCE);
		
		assertEquals(BigFraction.of(15, 1),    specTrx.getGrossDividendRat());
		assertEquals(BigFraction.of(87, 20),   specTrx.getFeesTaxesRat());
		assertEquals(BigFraction.of(213, 20),  specTrx.getNetDividendRat());
		
		assertEquals(specTrx.getGrossDividendRat().doubleValue(), specTrx.getGrossDividend().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getFeesTaxesRat().doubleValue(),     specTrx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getNetDividendRat().doubleValue(),   specTrx.getNetDividend().doubleValue(), ConstTest.DIFF_TOLERANCE);
		
		// ---
		
		try {
			specTrx.validate();
			assertEquals(0, 0);
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
	}

}
