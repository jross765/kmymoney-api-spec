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
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.api.write.impl.KMyMoneyWritableTransactionImpl;
import org.kmymoney.apispec.ConstTest;
import org.kmymoney.apispec.read.impl.KMyMoneyStockBuyTransactionImpl;
import org.kmymoney.apispec.read.impl.TestKMyMoneyStockBuyTransactionImpl;
import org.kmymoney.apispec.write.KMyMoneyWritableStockBuyTransaction;
import org.kmymoney.base.basetypes.simple.KMMTrxID;

import junit.framework.JUnit4TestAdapter;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class TestKMyMoneyWritableStockBuyTransactionImpl {
	private static final KMMTrxID TRX_1_ID = TestKMyMoneyStockBuyTransactionImpl.TRX_1_ID;
	private static final KMMTrxID TRX_4_ID = TestKMyMoneyStockBuyTransactionImpl.TRX_4_ID;

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
		return new JUnit4TestAdapter(TestKMyMoneyWritableStockBuyTransactionImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL kmmFileURL = classLoader.getResource(Const.GCSH_FILENAME);
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
		
		KMyMoneyStockBuyTransactionImpl roSpecTrx = new KMyMoneyStockBuyTransactionImpl((KMyMoneyTransactionImpl) genTrx);
		KMyMoneyWritableStockBuyTransaction specTrx = new KMyMoneyWritableStockBuyTransactionImpl(roSpecTrx);
		assertNotEquals(null, specTrx);
		
		assertEquals(3, specTrx.getSplitsCount());
		
		assertEquals("S0003", specTrx.getStockAccountSplit().getID().toString());
		assertEquals(1, specTrx.getExpensesSplits().size());
		assertEquals("S0002", specTrx.getExpensesSplits().get(0).getID().toString());
		assertEquals("S0001", specTrx.getOffsettingAccountSplit().getID().toString());
		
		assertEquals(specTrx.getSplits().get(0).toString(), specTrx.getOffsettingAccountSplit().toString());
		assertEquals(specTrx.getSplits().get(1).toString(), specTrx.getExpensesSplits().get(0).toString());
		assertEquals(specTrx.getSplits().get(2).toString(), specTrx.getStockAccountSplit().toString());
		
		// ---
		
		assertEquals(15.0,    specTrx.getNofShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(120.0,   specTrx.getPricePerShare().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1800.0,  specTrx.getNetPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(10.0,    specTrx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1810.00, specTrx.getGrossPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		
		assertEquals(BigFraction.of(15, 1),     specTrx.getNofSharesRat());
		assertEquals(BigFraction.of(120, 1),    specTrx.getPricePerShareRat());
		assertEquals(BigFraction.of(1800, 1),   specTrx.getNetPriceRat());
		assertEquals(BigFraction.of(10, 1),     specTrx.getFeesTaxesRat());
		assertEquals(BigFraction.of(1810, 1),   specTrx.getGrossPriceRat());
		
		assertEquals(specTrx.getNofSharesRat().doubleValue(),     specTrx.getNofShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getPricePerShareRat().doubleValue(), specTrx.getPricePerShare().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getNetPriceRat().doubleValue(),      specTrx.getNetPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getFeesTaxesRat().doubleValue(),     specTrx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrx.getGrossPriceRat().doubleValue(),    specTrx.getGrossPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		
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
		
		KMyMoneyStockBuyTransactionImpl specTrxRO = new KMyMoneyStockBuyTransactionImpl((KMyMoneyWritableTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_1_ID, specTrxRO.getID());

		KMyMoneyWritableStockBuyTransaction specTrxRW = new KMyMoneyWritableStockBuyTransactionImpl(specTrxRO);
		assertNotEquals(null, specTrxRW);
		assertEquals(TRX_1_ID, specTrxRW.getID());
		
		// ----------------------------
		// Modify the object

		KMyMoneyWritableTransactionSplit splt1 = specTrxRW.getWritableStockAccountSplit();
		splt1.setShares(new FixedPointNumber("20"));
		splt1.setValue(new FixedPointNumber("2500.00"));
		
		try {
			specTrxRW.validate();
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0); // <-- trx is not balanced
		}
		
		KMyMoneyWritableTransactionSplit splt2 = specTrxRW.getWritableOffsettingAccountSplit();
		splt2.setShares(new FixedPointNumber("-2500.00")); // <-- net (!) amount
		splt2.setValue(new FixedPointNumber("-2600.00")); // <-- sic, not equal to qty
		
		try {
			specTrxRW.validate();
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0); // <-- splt2: value <> qty
		}
		
		splt2.setValue(new FixedPointNumber("-2500.00")); // <-- splt2 now consistent
		
		try {
			specTrxRW.validate();
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0); // <-- splt2 consistent, but not correct, so trx still not balanced
		}
		
		// Add fees and taxes to gross amount:
		splt2.setValue(new FixedPointNumber("-2510.00"));
		splt2.setShares(new FixedPointNumber("-2510.00"));
		
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

	private void test02_1_check_memory(KMyMoneyWritableStockBuyTransaction trx) throws Exception {
		assertEquals(3, trx.getSplitsCount());
		
		assertEquals("S0003", trx.getStockAccountSplit().getID().toString());
		assertEquals(1, trx.getExpensesSplits().size());
		assertEquals("S0002", trx.getExpensesSplits().get(0).getID().toString());
		assertEquals("S0001", trx.getOffsettingAccountSplit().getID().toString());
		
		assertEquals(trx.getSplits().get(0).toString(), trx.getOffsettingAccountSplit().toString());
		assertEquals(trx.getSplits().get(1).toString(), trx.getExpensesSplits().get(0).toString());
		assertEquals(trx.getSplits().get(2).toString(), trx.getStockAccountSplit().toString());
		
		// ---
		
		assertEquals(20.0,    trx.getNofShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(125.0,   trx.getPricePerShare().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2500.0,  trx.getNetPrice().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(10.0,    trx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2510.00, trx.getGrossPrice().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals(BigFraction.of(20, 1),   trx.getNofSharesRat()); // changed
		assertEquals(BigFraction.of(125, 1),  trx.getPricePerShareRat());
		assertEquals(BigFraction.of(2500, 1), trx.getNetPriceRat()); // changed
		assertEquals(BigFraction.of(10, 1),   trx.getFeesTaxesRat());
		assertEquals(BigFraction.of(2510, 1), trx.getGrossPriceRat()); // changed
		
		assertEquals(trx.getNofSharesRat().doubleValue(),     trx.getNofShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getPricePerShareRat().doubleValue(), trx.getPricePerShare().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getNetPriceRat().doubleValue(),      trx.getNetPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getFeesTaxesRat().doubleValue(),     trx.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(trx.getGrossPriceRat().doubleValue(),    trx.getGrossPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		kmmOutFile = new KMyMoneyFileImpl(outFile);
		kmmOutFileStats = new KMMFileStats(kmmOutFile);

		KMyMoneyTransaction genTrx = kmmOutFile.getTransactionByID(TRX_1_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_1_ID, genTrx.getID());

		KMyMoneyStockBuyTransactionImpl specTrxRO = new KMyMoneyStockBuyTransactionImpl((KMyMoneyTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_1_ID, specTrxRO.getID());
		
		// ---
		
		assertEquals(3, specTrxRO.getSplitsCount());
		
		assertEquals("S0003", specTrxRO.getStockAccountSplit().getID().toString());
		assertEquals(1, specTrxRO.getExpensesSplits().size());
		assertEquals("S0002", specTrxRO.getExpensesSplits().get(0).getID().toString());
		assertEquals("S0001", specTrxRO.getOffsettingAccountSplit().getID().toString());
		
		assertEquals(specTrxRO.getSplits().get(0).toString(), specTrxRO.getOffsettingAccountSplit().toString());
		assertEquals(specTrxRO.getSplits().get(1).toString(), specTrxRO.getExpensesSplits().get(0).toString());
		assertEquals(specTrxRO.getSplits().get(2).toString(), specTrxRO.getStockAccountSplit().toString());
		
		// ---
		
		assertEquals(20.0,    specTrxRO.getNofShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(125.0,   specTrxRO.getPricePerShare().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2500.0,  specTrxRO.getNetPrice().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(10.0,    specTrxRO.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2510.00, specTrxRO.getGrossPrice().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals(BigFraction.of(20, 1),   specTrxRO.getNofSharesRat()); // changed
		assertEquals(BigFraction.of(125, 1),  specTrxRO.getPricePerShareRat());
		assertEquals(BigFraction.of(2500, 1), specTrxRO.getNetPriceRat()); // changed
		assertEquals(BigFraction.of(10, 1),   specTrxRO.getFeesTaxesRat());
		assertEquals(BigFraction.of(2510, 1), specTrxRO.getGrossPriceRat()); // changed
		
		assertEquals(specTrxRO.getNofSharesRat().doubleValue(),     specTrxRO.getNofShares().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getPricePerShareRat().doubleValue(), specTrxRO.getPricePerShare().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getNetPriceRat().doubleValue(),      specTrxRO.getNetPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getFeesTaxesRat().doubleValue(),     specTrxRO.getFeesTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(specTrxRO.getGrossPriceRat().doubleValue(),    specTrxRO.getGrossPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
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
