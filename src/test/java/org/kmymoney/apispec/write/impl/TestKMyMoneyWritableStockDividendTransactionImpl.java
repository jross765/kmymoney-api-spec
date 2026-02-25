package org.kmymoney.apispec.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.net.URL;

import org.apache.commons.numbers.fraction.BigFraction;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.api.read.impl.aux.KMMFileStats;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.api.write.impl.KMyMoneyWritableTransactionImpl;
import org.kmymoney.apispec.ConstTest;
import org.kmymoney.apispec.read.impl.KMyMoneyStockDividendTransactionImpl;
import org.kmymoney.apispec.read.impl.TestKMyMoneyStockDividendTransactionImpl;
import org.kmymoney.apispec.write.KMyMoneyWritableStockDividendTransaction;
import org.kmymoney.base.basetypes.simple.KMMTrxID;

import junit.framework.JUnit4TestAdapter;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class TestKMyMoneyWritableStockDividendTransactionImpl {
	private static final KMMTrxID TRX_1_ID = TestKMyMoneyStockDividendTransactionImpl.TRX_1_ID;
	private static final KMMTrxID TRX_4_ID = TestKMyMoneyStockDividendTransactionImpl.TRX_4_ID;

	// -----------------------------------------------------------------

	private KMyMoneyWritableFileImpl kmmInFile = null;
	private KMyMoneyFileImpl kmmOutFile = null;

	private KMMFileStats kmmInFileStats = null;
	private KMMFileStats kmmOutFileStats = null;

	private KMMTrxID newTrxID = null;

	// https://stackoverflow.com/questions/11884141/deleting-file-and-directory-in-junit
	@SuppressWarnings("exports")
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestKMyMoneyWritableStockDividendTransactionImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL kmmFileURL = classLoader.getResource(Const.KMM_FILENAME);
		// System.err.println("KMyMoney test file resource: '" + kmmFileURL + "'");
		URL kmmInFileURL = null;
		File kmmInFileRaw = null;
		try {
			kmmInFileURL = classLoader.getResource(ConstTest.KMM_FILENAME);
			kmmInFileRaw = new File(kmmInFileURL.getFile());
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			kmmInFile = new KMyMoneyWritableFileImpl(kmmInFileRaw);
		} catch (Exception exc) {
			System.err.println("Cannot parse KMyMoney in-file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------
	// PART 1: Read existing objects as modifiable ones
	// (and see whether they are fully symmetrical to their read-only
	// counterparts)
	// -----------------------------------------------------------------
	// Cf. TestKMyMoneyStockBuyTransactionImpl.test02
	//
	// Check whether the KMyMoneyWritableTransaction objects returned by
	// KMyMoneyWritableFileImpl.getWritableStockBuyTransactionByID() are actually
	// complete (as complete as returned be KMyMoneyFileImpl.getTransactionByID().
	
	@Test
	public void test01_2() throws Exception {
		KMyMoneyWritableTransaction genTrx = kmmInFile.getWritableTransactionByID(TRX_1_ID);
		assertNotEquals(null, genTrx);

		assertEquals(TRX_1_ID, genTrx.getID());
		
		KMyMoneyStockDividendTransactionImpl roSpecTrx = new KMyMoneyStockDividendTransactionImpl((KMyMoneyTransactionImpl) genTrx);
		KMyMoneyWritableStockDividendTransaction specTrx = new KMyMoneyWritableStockDividendTransactionImpl(roSpecTrx);
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
		
		assertEquals(BigFraction.of(15, 1),   specTrx.getGrossDividendRat());
		assertEquals(BigFraction.of(87, 20),  specTrx.getFeesTaxesRat());
		assertEquals(BigFraction.of(213, 20), specTrx.getNetDividendRat());
		
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

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the KMyMoneyWritableStockBuyTransaction objects returned by
	// can actually be modified -- both in memory and persisted in file.

	@Test
	public void test02_1() throws Exception {
		KMyMoneyWritableTransaction genTrx = kmmInFile.getWritableTransactionByID(TRX_1_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_1_ID, genTrx.getID());
		
		KMyMoneyStockDividendTransactionImpl specTrxRO = new KMyMoneyStockDividendTransactionImpl((KMyMoneyWritableTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_1_ID, specTrxRO.getID());

		KMyMoneyWritableStockDividendTransaction specTrxRW = new KMyMoneyWritableStockDividendTransactionImpl(specTrxRO);
		assertNotEquals(null, specTrxRW);
		assertEquals(TRX_1_ID, specTrxRW.getID());
		
		// ----------------------------
		// Modify the object

		specTrxRW.setNetDividend(new FixedPointNumber("11.65"));
		
		try {
			specTrxRW.validate();
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0); // <-- trx is not balanced
		}
		
		// Correct gross amount:
		specTrxRW.setGrossDividend(new FixedPointNumber("16.00"));
		
		try {
			specTrxRW.validate();
			assertEquals(0, 0); // <-- now everything's OK
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
		
		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_1_check_memory(specTrxRW);

		// ----------------------------
		// Now, check whether the modified object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.KMM_FILENAME_OUT);
		// System.err.println("Outfile for TestKMyMoneyWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the KMyMoney file writer does not like that.
		kmmInFile.writeFile(outFile);

		test02_1_check_persisted(outFile);
	}

	// ---------------------------------------------------------------

	private void test02_1_check_memory(KMyMoneyWritableStockDividendTransaction trx) throws Exception {
		assertEquals(5, trx.getSplitsCount());
		
		assertEquals("S0002", trx.getStockAccountSplit().getID().toString());
		assertEquals("S0005", trx.getIncomeAccountSplit().getID().toString());
		assertEquals(2, trx.getExpensesSplits().size());
		assertEquals("S0003", trx.getExpensesSplits().get(0).getID().toString());
		assertEquals("S0004", trx.getExpensesSplits().get(1).getID().toString());
		assertEquals("S0001", trx.getOffsettingAccountSplit().getID().toString());
		
		assertEquals(trx.getSplits().get(0).toString(), trx.getOffsettingAccountSplit().toString());
		assertEquals(trx.getSplits().get(1).toString(), trx.getStockAccountSplit().toString());
		assertEquals(trx.getSplits().get(2).toString(), trx.getExpensesSplits().get(0).toString());
		assertEquals(trx.getSplits().get(3).toString(), trx.getExpensesSplits().get(1).toString());
		assertEquals(trx.getSplits().get(4).toString(), trx.getIncomeAccountSplit().toString());
		
		// ---
		
		assertEquals(16.0,  trx.getGrossDividend().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(4.35,  trx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(11.65, trx.getNetDividend().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals(BigFraction.of(16, 1),   trx.getGrossDividendRat()); // changed
		assertEquals(BigFraction.of(87, 20),  trx.getFeesTaxesRat());
		assertEquals(BigFraction.of(233, 20), trx.getNetDividendRat()); // changed
		
		assertEquals(trx.getGrossDividendRat().doubleValue(), trx.getGrossDividend().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getFeesTaxesRat().doubleValue(),     trx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getNetDividendRat().doubleValue(),   trx.getNetDividend().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		kmmOutFile = new KMyMoneyFileImpl(outFile);
		kmmOutFileStats = new KMMFileStats(kmmOutFile);

		KMyMoneyTransaction genTrx = kmmOutFile.getTransactionByID(TRX_1_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_1_ID, genTrx.getID());

		KMyMoneyStockDividendTransactionImpl specTrxRO = new KMyMoneyStockDividendTransactionImpl((KMyMoneyTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_1_ID, specTrxRO.getID());
		
		// ---
		
		assertEquals(5, specTrxRO.getSplitsCount());
		
		assertEquals("S0002", specTrxRO.getStockAccountSplit().getID().toString());
		assertEquals("S0005", specTrxRO.getIncomeAccountSplit().getID().toString());
		assertEquals(2, specTrxRO.getExpensesSplits().size());
		assertEquals("S0003", specTrxRO.getExpensesSplits().get(0).getID().toString());
		assertEquals("S0004", specTrxRO.getExpensesSplits().get(1).getID().toString());
		assertEquals("S0001", specTrxRO.getOffsettingAccountSplit().getID().toString());
		
		assertEquals(specTrxRO.getSplits().get(0).toString(), specTrxRO.getOffsettingAccountSplit().toString());
		assertEquals(specTrxRO.getSplits().get(1).toString(), specTrxRO.getStockAccountSplit().toString());
		assertEquals(specTrxRO.getSplits().get(2).toString(), specTrxRO.getExpensesSplits().get(0).toString());
		assertEquals(specTrxRO.getSplits().get(3).toString(), specTrxRO.getExpensesSplits().get(1).toString());
		assertEquals(specTrxRO.getSplits().get(4).toString(), specTrxRO.getIncomeAccountSplit().toString());
		
		// ---
		
		assertEquals(16.0,  specTrxRO.getGrossDividend().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(4.35,  specTrxRO.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(11.65, specTrxRO.getNetDividend().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals(BigFraction.of(16, 1),   specTrxRO.getGrossDividendRat()); // changed
		assertEquals(BigFraction.of(87, 20),  specTrxRO.getFeesTaxesRat());
		assertEquals(BigFraction.of(233, 20), specTrxRO.getNetDividendRat()); // changed
		
		assertEquals(specTrxRO.getGrossDividendRat().doubleValue(), specTrxRO.getGrossDividend().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getFeesTaxesRat().doubleValue(),     specTrxRO.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getNetDividendRat().doubleValue(),   specTrxRO.getNetDividend().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	// -----------------------------------------------------------------
	// PART 3: Create new objects
	// -----------------------------------------------------------------

	// ------------------------------
	// PART 3.1: High-Level
	// ------------------------------
	
	// NOT POSSIBLE (THE WAY WE NORMALLY USE THE API).
	// A special transaction must correctly validate the moment we instantiate it.
	// It is therefore not possible to create an empty instance of KMyMoneyWritableTransaction,
	// because 'empty' means 'no splits', which in turn means 'is not valid'.
	
	// You must therefore always generate a generic transaction with two splits etc.
	// But this already is implicitly tested elsewhere.

	// ------------------------------
	// PART 3.2: Low-Level
	// ------------------------------
	
	// ::EMPTY
	
	// -----------------------------------------------------------------
	// PART 4: Delete objects
	// -----------------------------------------------------------------

	// ------------------------------
	// PART 4.1: High-Level
	// ------------------------------
	
	// ::TODO

	// ------------------------------
	// PART 4.2: Low-Level
	// ------------------------------

	// ::TODO

}
