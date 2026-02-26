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
import org.kmymoney.apispec.read.KMyMoneyStockSplitTransaction;
import org.kmymoney.apispec.read.impl.KMyMoneyStockSplitTransactionImpl;
import org.kmymoney.apispec.read.impl.TestKMyMoneyStockSplitTransactionImpl;
import org.kmymoney.apispec.write.KMyMoneyWritableStockSplitTransaction;
import org.kmymoney.base.basetypes.simple.KMMTrxID;

import junit.framework.JUnit4TestAdapter;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class TestKMyMoneyWritableStockSplitTransactionImpl {
	private static final KMMTrxID TRX_1_ID = TestKMyMoneyStockSplitTransactionImpl.TRX_1_ID;
	private static final KMMTrxID TRX_4_ID = TestKMyMoneyStockSplitTransactionImpl.TRX_4_ID;

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
		return new JUnit4TestAdapter(TestKMyMoneyWritableStockSplitTransactionImpl.class);
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
		KMyMoneyWritableTransaction genTrx = kmmInFile.getWritableTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);

		assertEquals(TRX_4_ID, genTrx.getID());
		
		KMyMoneyStockSplitTransaction specTrx = new KMyMoneyStockSplitTransactionImpl((KMyMoneyTransactionImpl) genTrx);
		assertNotEquals(null, specTrx);
		
		// ---
		
		assertEquals(1, specTrx.getSplitsCount());
		
		assertEquals("S0001", specTrx.getSplit().getID().toString());
		
		assertEquals(specTrx.getSplits().get(0).toString(), specTrx.getSplit().toString());
		
		// ---
		
		assertEquals(17.0, specTrx.getNofAddShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2.0,  specTrx.getSplitFactor().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(17.0, specTrx.getNofSharesBeforeSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(34.0, specTrx.getNofSharesAfterSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
		
		assertEquals(BigFraction.of(17, 1), specTrx.getNofAddSharesRat());
		assertEquals(BigFraction.of(2, 1),  specTrx.getSplitFactorRat());
		assertEquals(BigFraction.of(17, 1), specTrx.getNofSharesBeforeSplitRat());
		assertEquals(BigFraction.of(34, 1), specTrx.getNofSharesAfterSplitRat());
		
		assertEquals(specTrx.getNofAddSharesRat().doubleValue(),         specTrx.getNofAddShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getSplitFactorRat().doubleValue(),          specTrx.getSplitFactor().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getNofSharesBeforeSplitRat().doubleValue(), specTrx.getNofSharesBeforeSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getNofSharesAfterSplitRat().doubleValue(),  specTrx.getNofSharesAfterSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
		
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
		KMyMoneyWritableTransaction genTrx = kmmInFile.getWritableTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_4_ID, genTrx.getID());
		
		KMyMoneyStockSplitTransactionImpl specTrxRO = new KMyMoneyStockSplitTransactionImpl((KMyMoneyWritableTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_4_ID, specTrxRO.getID());

		KMyMoneyWritableStockSplitTransaction specTrxRW = new KMyMoneyWritableStockSplitTransactionImpl(specTrxRO);
		assertNotEquals(null, specTrxRW);
		assertEquals(TRX_4_ID, specTrxRW.getID());
		
		// ----------------------------
		// Variant 1:
		// Modify the object

		specTrxRW.setNofAddShares(new FixedPointNumber("34"));
		
		try {
			specTrxRW.validate();
			assertEquals(0, 0);
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
		
		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_1_check_memory(specTrxRW);

		// ----------------------------
		// Variant 2:
		
		specTrxRW.setSplitFactor(BigFraction.of(3, 1)); // redundant / idempotent
		
		try {
			specTrxRW.validate();
			assertEquals(0, 0);
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
		
		test02_1_check_memory(specTrxRW);

		// ----------------------------
		// Variant 3:
		
		specTrxRW.setNofSharesAfterSplit(new FixedPointNumber("51")); // redundant / idempotent
		
		try {
			specTrxRW.validate();
			assertEquals(0, 0);
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
		
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

	private void test02_1_check_memory(KMyMoneyWritableStockSplitTransaction trx) throws Exception {
		assertEquals(1, trx.getSplitsCount());
		
		// ---
		
		assertEquals(34.0, trx.getNofAddShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(3.0,  trx.getSplitFactor().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(17.0, trx.getNofSharesBeforeSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(51.0, trx.getNofSharesAfterSplit().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals(BigFraction.of(34, 1), trx.getNofAddSharesRat()); // changed
		assertEquals(BigFraction.of(3, 1),  trx.getSplitFactorRat()); // changed
		assertEquals(BigFraction.of(17, 1), trx.getNofSharesBeforeSplitRat());
		assertEquals(BigFraction.of(51, 1), trx.getNofSharesAfterSplitRat()); // changed
		
		assertEquals(trx.getNofAddSharesRat().doubleValue(),         trx.getNofAddShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getSplitFactorRat().doubleValue(),          trx.getSplitFactor().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getNofSharesBeforeSplitRat().doubleValue(), trx.getNofSharesBeforeSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getNofSharesAfterSplitRat().doubleValue(),  trx.getNofSharesAfterSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		kmmOutFile = new KMyMoneyFileImpl(outFile);
		kmmOutFileStats = new KMMFileStats(kmmOutFile);

		KMyMoneyTransaction genTrx = kmmOutFile.getTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_4_ID, genTrx.getID());

		KMyMoneyStockSplitTransactionImpl specTrxRO = new KMyMoneyStockSplitTransactionImpl((KMyMoneyTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_4_ID, specTrxRO.getID());
		
		// ---
		
		assertEquals(1, specTrxRO.getSplitsCount());
		
		// ---
		
		assertEquals(34.0, specTrxRO.getNofAddShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(3.0,  specTrxRO.getSplitFactor().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(17.0, specTrxRO.getNofSharesBeforeSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(51.0, specTrxRO.getNofSharesAfterSplit().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals(BigFraction.of(34, 1), specTrxRO.getNofAddSharesRat()); // changed
		assertEquals(BigFraction.of(3, 1),  specTrxRO.getSplitFactorRat()); // changed
		assertEquals(BigFraction.of(17, 1), specTrxRO.getNofSharesBeforeSplitRat());
		assertEquals(BigFraction.of(51, 1), specTrxRO.getNofSharesAfterSplitRat()); // changed
		
		assertEquals(specTrxRO.getNofAddSharesRat().doubleValue(),         specTrxRO.getNofAddShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getSplitFactorRat().doubleValue(),          specTrxRO.getSplitFactor().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getNofSharesBeforeSplitRat().doubleValue(), specTrxRO.getNofSharesBeforeSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getNofSharesAfterSplitRat().doubleValue(),  specTrxRO.getNofSharesAfterSplit().doubleValue(), ConstTest.DIFF_TOLERANCE);
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
