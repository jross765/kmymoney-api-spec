package org.kmymoney.apispec.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.net.URL;

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
import org.kmymoney.apispec.read.impl.KMyMoneySimpleTransactionImpl;
import org.kmymoney.apispec.read.impl.TestKMyMoneySimpleTransactionImpl;
import org.kmymoney.apispec.write.KMyMoneyWritableSimpleTransaction;
import org.kmymoney.base.basetypes.simple.KMMTrxID;

import junit.framework.JUnit4TestAdapter;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class TestKMyMoneyWritableSimpleTransactionImpl {
	private static final KMMTrxID TRX_1_ID = TestKMyMoneySimpleTransactionImpl.TRX_1_ID;
	private static final KMMTrxID TRX_4_ID = TestKMyMoneySimpleTransactionImpl.TRX_4_ID;
//	private static final KMMTrxID TRX_10_ID = new KMMTrxID("c97032ba41684b2bb5d1391c9d7547e9");
//
//	private static final KMMAcctID ACCT_1_ID  = new KMMAcctID("bbf77a599bd24a3dbfec3dd1d0bb9f5c"); // Root Account:Aktiva:Sichteinlagen:KK:Giro RaiBa
//	private static final KMMAcctID ACCT_20_ID = new KMMAcctID("b88e9eca9c73411b947b882d0bf8ec6f"); // Root Account::Aktiva::Sichteinlagen::nicht-KK::Sparkonto

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
		return new JUnit4TestAdapter(TestKMyMoneyWritableSimpleTransactionImpl.class);
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
	// Cf. TestKMyMoneySimpleTransactionImpl.test02
	//
	// Check whether the KMyMoneyWritableTransaction objects returned by
	// KMyMoneyWritableFileImpl.getWritableSimpleTransactionByID() are actually
	// complete (as complete as returned be KMyMoneyFileImpl.getTransactionByID().
	
	@Test
	public void test01_2() throws Exception {
		KMyMoneyWritableTransaction genTrx = kmmInFile.getWritableTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_4_ID, genTrx.getID());
		
		KMyMoneySimpleTransactionImpl specTrxRO = new KMyMoneySimpleTransactionImpl((KMyMoneyWritableTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_4_ID, specTrxRO.getID());

		KMyMoneyWritableSimpleTransaction specTrxRW = new KMyMoneyWritableSimpleTransactionImpl(specTrxRO);
		assertNotEquals(null, specTrxRW);
		assertEquals(TRX_4_ID, specTrxRW.getID());
		
		// ---
		
		assertEquals(2, specTrxRW.getSplitsCount());
		
		assertEquals("S0001", specTrxRW.getFirstSplit().getID().toString());
		assertEquals(10000.0, specTrxRW.getFirstSplit().getShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(10000.0, specTrxRW.getFirstSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed

		assertEquals("S0002", specTrxRW.getSecondSplit().getID().toString());
		assertEquals(-10000.0, specTrxRW.getSecondSplit().getShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(-10000.0, specTrxRW.getSecondSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals(specTrxRW.getSplits().get(0).getID().toString(), specTrxRW.getFirstSplit().getID().toString());
		assertEquals(specTrxRW.getSplits().get(1).getID().toString(), specTrxRW.getSecondSplit().getID().toString());
		
		// ---
		
		assertEquals(-10000.0, specTrxRW.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE);
		// ---
		
		try {
			specTrxRW.validate();
			assertEquals(0, 0);
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
	}

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the KMyMoneyWritableSimpleTransaction objects returned by
	// can actually be modified -- both in memory and persisted in file.

	@Test
	public void test02_1() throws Exception {
		KMyMoneyWritableTransaction genTrx = kmmInFile.getWritableTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_4_ID, genTrx.getID());
		
		KMyMoneySimpleTransactionImpl specTrxRO = new KMyMoneySimpleTransactionImpl((KMyMoneyWritableTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_4_ID, specTrxRO.getID());

		KMyMoneyWritableSimpleTransaction specTrxRW = new KMyMoneyWritableSimpleTransactionImpl((KMyMoneySimpleTransactionImpl) specTrxRO);
		assertNotEquals(null, specTrxRW);
		assertEquals(TRX_4_ID, specTrxRW.getID());
		
		// ----------------------------
		// Modify the object

		KMyMoneyWritableTransactionSplit splt1 = specTrxRW.getWritableFirstSplit();
		splt1.setShares(new FixedPointNumber("200"));
		splt1.setValue(new FixedPointNumber("200"));
		
		try {
			specTrxRW.validate();
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0); // <-- split 1 not symmetric to split 2
		}
		
		KMyMoneyWritableTransactionSplit splt2 = specTrxRW.getWritableSecondSplit();
		splt2.setShares(new FixedPointNumber("-200"));
		splt2.setValue(new FixedPointNumber("-300")); // <-- sic
		
		try {
			specTrxRW.validate();
			assertEquals(0, 1);
		} catch ( Exception ext ) {
			assertEquals(0, 0); // <-- splits still not symmetric
		}
		
		splt2.setValue(new FixedPointNumber("-200")); // <-- now correct
		
		try {
			specTrxRW.validate();
			assertEquals(0, 0); // <-- now finally
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

	@Test
	public void test02_2() throws Exception {
		KMyMoneyWritableTransaction genTrx = kmmInFile.getWritableTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_4_ID, genTrx.getID());
		
		KMyMoneySimpleTransactionImpl specTrxRO = new KMyMoneySimpleTransactionImpl((KMyMoneyWritableTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_4_ID, specTrxRO.getID());

		KMyMoneyWritableSimpleTransaction specTrxRW = new KMyMoneyWritableSimpleTransactionImpl((KMyMoneySimpleTransactionImpl) specTrxRO);
		assertNotEquals(null, specTrxRW);
		assertEquals(TRX_4_ID, specTrxRW.getID());
		
		// ----------------------------
		// Modify the object

		specTrxRW.setAmount(new FixedPointNumber("300"));

		try {
			specTrxRW.validate();
			assertEquals(0, 0);
		} catch ( Exception ext ) {
			assertEquals(0, 1);
		}
		
		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_2_check_memory(specTrxRW);

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

		// Not necessary:
		// test02_2_check_persisted(outFile);
	}

	// ---------------------------------------------------------------

	private void test02_1_check_memory(KMyMoneyWritableSimpleTransaction trx) throws Exception {
		assertEquals(2, trx.getSplitsCount()); // unchanged
		
		assertEquals("S0001", trx.getFirstSplit().getID().toString()); // unchanged
		assertEquals(200.0, trx.getFirstSplit().getShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(200.0, trx.getFirstSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals("S0002", trx.getSecondSplit().getID().toString()); // unchanged
		assertEquals(-200.0, trx.getSecondSplit().getShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(-200.0, trx.getSecondSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed

		assertEquals(-200.0, trx.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		kmmOutFile = new KMyMoneyFileImpl(outFile);
		kmmOutFileStats = new KMMFileStats(kmmOutFile);

		KMyMoneyTransaction genTrx = kmmOutFile.getTransactionByID(TRX_4_ID);
		assertNotEquals(null, genTrx);
		assertEquals(TRX_4_ID, genTrx.getID());

		KMyMoneySimpleTransactionImpl specTrxRO = new KMyMoneySimpleTransactionImpl((KMyMoneyTransactionImpl) genTrx);
		assertNotEquals(null, specTrxRO);
		assertEquals(TRX_4_ID, specTrxRO.getID());

		// ---
		
		assertEquals(2, specTrxRO.getSplitsCount()); // unchanged
		
		assertEquals("S0001", specTrxRO.getFirstSplit().getID().toString()); // unchanged
		assertEquals(200.0, specTrxRO.getFirstSplit().getShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(200.0, specTrxRO.getFirstSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals("S0002", specTrxRO.getSecondSplit().getID().toString()); // unchanged
		assertEquals(-200.0, specTrxRO.getSecondSplit().getShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(-200.0, specTrxRO.getSecondSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed

		assertEquals(-200.0, specTrxRO.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
	}

	// ---------------------------------------------------------------

	private void test02_2_check_memory(KMyMoneyWritableSimpleTransaction trx) throws Exception {
		assertEquals(2, trx.getSplitsCount()); // unchanged
		
		assertEquals("S0001", trx.getFirstSplit().getID().toString()); // unchanged
		assertEquals(-300.0, trx.getFirstSplit().getShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(-300.0, trx.getFirstSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		
		assertEquals("S0002", trx.getSecondSplit().getID().toString()); // unchanged
		assertEquals(300.0, trx.getSecondSplit().getShares().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(300.0, trx.getSecondSplit().getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed

		assertEquals(300.0, trx.getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
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
