package org.kmymoney.apispec.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.apispec.ConstTest;
import org.kmymoney.apispec.read.KMyMoneyStockSplitTransaction;
import org.kmymoney.base.basetypes.simple.KMMTrxID;

import junit.framework.JUnit4TestAdapter;

public class TestKMyMoneyStockSplitTransactionImpl {
	
	public static final KMMTrxID TRX_1_ID = new KMMTrxID("T000000000000000017");
	public static final KMMTrxID TRX_4_ID = new KMMTrxID("T000000000000000019");
	
	// -----------------------------------------------------------------

	private KMyMoneyFile kmmFile = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestKMyMoneyStockSplitTransactionImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL kmmFileURL = classLoader.getResource(Const.KMM_FILENAME);
		// System.err.println("KMyMoney test file resource: '" + kmmFileURL + "'");
		InputStream kmmFileStream = null;
		try {
			kmmFileStream = classLoader.getResourceAsStream(ConstTest.KMM_FILENAME);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			kmmFile = new KMyMoneyFileImpl(kmmFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse KMyMoney file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		KMyMoneyTransaction genTrx = kmmFile.getTransactionByID(TRX_1_ID);
		assertNotEquals(null, genTrx);

		assertEquals(TRX_1_ID, genTrx.getID());
		assertEquals(3, genTrx.getSplitsCount());
		
		try {
			KMyMoneyStockSplitTransaction specTrx = new KMyMoneyStockSplitTransactionImpl((KMyMoneyTransactionImpl) genTrx);
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test02() throws Exception {
		KMyMoneyTransaction genTrx = kmmFile.getTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);

		assertEquals(TRX_4_ID, genTrx.getID());
		
		KMyMoneyStockSplitTransaction specTrx = new KMyMoneyStockSplitTransactionImpl((KMyMoneyTransactionImpl) genTrx);
		assertNotEquals(null, specTrx);
		
		assertEquals(1, specTrx.getSplitsCount());
		
		assertEquals("S0001", specTrx.getSplit().getID().toString());
		
		assertEquals(specTrx.getSplits().get(0).toString(), specTrx.getSplit().toString());
		
		try {
			specTrx.validate();
			assertEquals(0, 0);
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
	}

}
