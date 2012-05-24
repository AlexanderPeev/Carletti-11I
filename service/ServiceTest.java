package service;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import model.InconsistencyException;
import model.OutOfStockSpaceException;
import model.ProductType;
import model.State;
import model.Stock;
import model.StockType;
import model.StorageUnit;
import model.SubProcess;
import model.Tray;

import org.junit.Test;

public class ServiceTest {

	/**
	 * @author Ricardas Risys
	 * 
	 */
	@Test
	public void testCreateSubProcess() {
		Exception thrown = null;
		ProductType pt = new ProductType("Skumbananer");

		// no product type
		try {
			Service.createSubProcess(null, 0, "Coating", 0, 1, 2);
		}
		catch (Exception e) {
			thrown = e;
		}
		Assert.assertNotNull(thrown);

		// negative order to 0 conversion
		thrown = null;
		try {
			SubProcess subProcess = Service.createSubProcess(pt, -1, "Coating",
					0, 1, 2);
			Assert.assertEquals(subProcess.getOrder(), 0);
			Assert.assertEquals(pt.getSubProcesses().get(0), subProcess);
			Assert.assertEquals(subProcess.getName(), "Coating");
			Assert.assertEquals(subProcess.getOrder(), 0);
			Assert.assertEquals(subProcess.getMinTime(), 0);
			Assert.assertEquals(subProcess.getIdealTime(), 1);
			Assert.assertEquals(subProcess.getMaxTime(), 2);

		}
		catch (Exception e) {
			thrown = e;
		}
		Assert.assertNull(thrown);

		// correct creation
		thrown = null;
		try {
			Service.createSubProcess(pt, 1, "Coating", 0, 1, 2);
		}
		catch (Exception e) {
			thrown = e;
		}
		Assert.assertNull(thrown);

		// same order as before inserted subprocess
		thrown = null;
		try {
			Service.createSubProcess(pt, 1, "Coating", 0, 1, 2);
		}
		catch (Exception e) {
			thrown = e;
		}
		Assert.assertNotNull(thrown);

		// pass the empty name to see if created with sanitized name
		thrown = null;
		try {
			SubProcess subProcess = Service
					.createSubProcess(pt, 2, "", 0, 1, 2);
			Assert.assertEquals(subProcess.getName(), "New Sub Process");
		}
		catch (Exception e) {
			thrown = e;
		}
		Assert.assertNull(thrown);

		// wrong order
		thrown = null;
		try {
			Service.createSubProcess(pt, 3, "Coating", 1, 0, 2);
		}
		catch (Exception e) {
			thrown = e;
			Assert.assertEquals(
					thrown.getMessage(),
					"Min time must be lower than Ideal time. Ideal time must be lower then Max time.");
		}
		Assert.assertNotNull(thrown);

		thrown = null;
		try {
			Service.createSubProcess(pt, 3, "Coating", 0, 2, 1);
		}
		catch (Exception e) {
			thrown = e;
			Assert.assertEquals(
					thrown.getMessage(),
					"Min time must be lower than Ideal time. Ideal time must be lower then Max time.");
		}
		Assert.assertNotNull(thrown);

		thrown = null;
		try {
			Service.createSubProcess(pt, 3, "Coating", 2, 1, 0);
		}
		catch (Exception e) {
			thrown = e;
			Assert.assertEquals(
					thrown.getMessage(),
					"Min time must be lower than Ideal time. Ideal time must be lower then Max time.");
		}
		Assert.assertNotNull(thrown);

		thrown = null;
		try {
			Service.createSubProcess(pt, 3, "Coating", 0, 0, 0);
		}
		catch (Exception e) {
			thrown = e;
			Assert.assertEquals(thrown.getMessage(), "Times cannot be equal.");
		}
		Assert.assertNotNull(thrown);

		thrown = null;
		try {
			Service.createSubProcess(pt, 3, "Coating", -2, -1, 0);
		}
		catch (Exception e) {
			thrown = e;
			Assert.assertEquals(thrown.getMessage(),
					"Times cannot be less then 0.");
		}
		Assert.assertNotNull(thrown);
	}

	/**
	 * @author Thomas Sameul Jansen van Rensburg
	 */
	@Test
	public void testCreateState() {
		Date startDate = new Date(10);
		Date endDate = new Date(15);
		State state = Service.createState(null, null, startDate, endDate);
		Assert.assertEquals(state, null);// get what you should get,state

		SubProcess subProcess = new SubProcess(9, "Coating");
		state = Service.createState(subProcess, null, startDate, endDate);
		Assert.assertEquals(state, null);

		ProductType pt = new ProductType("Skumbananer");
		pt.addSubProcess(subProcess);
		Stock stock = new Stock("Core");
		StorageUnit su = new StorageUnit(stock, 0);
		Tray tray = new Tray(su, pt, 0);

		state = Service.createState(null, tray, startDate, endDate);
		Assert.assertEquals(state, null);

		state = Service.createState(subProcess, tray, endDate, startDate);
		Assert.assertEquals(state, null);

		state = Service.createState(subProcess, tray, startDate, endDate);
		Assert.assertNotNull(state);

		Assert.assertEquals(subProcess, state.getSubProcess());// checking link
																// between dao.

		state = Service
				.createState(subProcess, tray, new Date(-5), new Date(0));
		Assert.assertEquals(state, null);

		state = Service.createState(subProcess, tray, new Date(0), new Date(0));
		Assert.assertEquals(state, null);

		state = Service.createState(subProcess, tray, new Date(0), null);
		Assert.assertNotNull(state);

		state = Service.createState(subProcess, tray, null, null);
		Assert.assertEquals(state, null);
	}

	/**
	 * @author Alexander Peev
	 * @throws PickTooLateException
	 * @throws PickTooEarlyException
	 * @throws StockNotCompatibleException
	 * @throws InconsistencyException
	 * @throws OutOfStockSpaceException
	 */
	@Test
	public void testPickTrays() throws OutOfStockSpaceException,
			InconsistencyException, StockNotCompatibleException,
			PickTooEarlyException, PickTooLateException {
		Stock destination = new Stock("Test stock", StockType.SEMI, 1, 16, 1);
		StorageUnit su = new StorageUnit(destination, 0);
		destination.addStorageUnit(su);
		long t = System.currentTimeMillis();
		Date time = new Date(t);

		Service.pickTrays(null, destination, time);
		Assert.assertEquals(0, su.getTrays().size());

		ProductType pt = new ProductType("Test Product Type");
		SubProcess sp1 = new SubProcess(0, "Test SP 1", 2, 3, 4), sp2 = new SubProcess(
				1, "Test SP 2", 2, 3, 4);
		sp1.addStock(destination);
		sp2.addStock(destination);
		pt.addSubProcess(sp1);
		pt.addSubProcess(sp2);
		Tray t1 = new Tray(pt), t2 = new Tray(pt), t3 = new Tray(pt), t4 = new Tray(
				pt), t5 = new Tray(pt);
		Set<Tray> trays = new HashSet<Tray>();
		trays.add(t1);

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(time);
		cal.add(GregorianCalendar.MINUTE, 2);

		Service.pickTrays(trays, null, cal.getTime());
		Assert.assertEquals(0, su.getTrays().size());

		Service.pickTrays(new HashSet<Tray>(), destination, cal.getTime());
		Assert.assertEquals(0, su.getTrays().size());

		Service.pickTrays(trays, destination, cal.getTime());
		Assert.assertEquals(1, su.getTrays().size());
		Assert.assertTrue(su.getTrays().contains(t1));
		Assert.assertEquals(t1.getCurrentState().getStartTime(), cal.getTime());

		trays.clear();
		trays.add(t2);
		trays.add(t3);
		Service.pickTrays(trays, destination, cal.getTime());
		Assert.assertEquals(3, su.getTrays().size());
		Assert.assertTrue(su.getTrays().contains(t1));
		Assert.assertTrue(su.getTrays().contains(t2));
		Assert.assertTrue(su.getTrays().contains(t3));
		Assert.assertEquals(t1.getCurrentState().getStartTime(), cal.getTime());
		Assert.assertEquals(t2.getCurrentState().getStartTime(), cal.getTime());
		Assert.assertEquals(t3.getCurrentState().getStartTime(), cal.getTime());

		Date calTime = cal.getTime(), curTime = new Date(
				System.currentTimeMillis());
		cal.setTime(curTime);
		cal.add(GregorianCalendar.MINUTE, -2);
		t4.getCurrentState().setStartTime(cal.getTime());
		t5.getCurrentState().setStartTime(cal.getTime());
		trays.clear();
		trays.add(t4);
		trays.add(t5);
		Service.pickTrays(trays, destination, null);
		Assert.assertEquals(5, su.getTrays().size());
		Assert.assertTrue(su.getTrays().contains(t1));
		Assert.assertTrue(su.getTrays().contains(t2));
		Assert.assertTrue(su.getTrays().contains(t3));
		Assert.assertTrue(su.getTrays().contains(t4));
		Assert.assertTrue(su.getTrays().contains(t5));
		Assert.assertEquals(t1.getCurrentState().getStartTime(), calTime);
		Assert.assertEquals(t2.getCurrentState().getStartTime(), calTime);
		Assert.assertEquals(t3.getCurrentState().getStartTime(), calTime);
		Assert.assertEquals(t4.getCurrentState().getStartTime(), curTime);
		Assert.assertEquals(t5.getCurrentState().getStartTime(), curTime);

	}

	/**
	 * @author Alexander Peev
	 * @throws PickTooEarlyException
	 *             or fails the test.
	 */
	@Test(expected = PickTooEarlyException.class)
	public void testPickTraysEarly() throws OutOfStockSpaceException,
			InconsistencyException, StockNotCompatibleException,
			PickTooEarlyException, PickTooLateException {
		Stock destination = new Stock("Test stock", StockType.SEMI, 1, 16, 1);
		StorageUnit su = new StorageUnit(destination, 0);
		destination.addStorageUnit(su);
		long t = System.currentTimeMillis();
		Date time = new Date(t);

		ProductType pt = new ProductType("Test Product Type");
		SubProcess sp1 = new SubProcess(0, "Test SP 1", 2, 3, 4), sp2 = new SubProcess(
				1, "Test SP 2", 2, 3, 4);
		sp1.addStock(destination);
		sp2.addStock(destination);
		pt.addSubProcess(sp1);
		pt.addSubProcess(sp2);
		Tray t1 = new Tray(pt);
		Set<Tray> trays = new HashSet<Tray>();
		trays.add(t1);

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(time);
		cal.add(GregorianCalendar.MINUTE, 2);
		cal.add(GregorianCalendar.SECOND, -1);

		Service.pickTrays(trays, destination, cal.getTime());
		Assert.assertEquals(1, su.getTrays().size());
		Assert.assertTrue(su.getTrays().contains(t1));
		Assert.assertEquals(t1.getCurrentState().getStartTime(), cal.getTime());

		Assert.fail("Exception not thrown!");
	}

	/**
	 * @author Alexander Peev
	 * @throws PickTooLateException
	 *             or fails the test.
	 */
	@Test(expected = PickTooLateException.class)
	public void testPickTraysLate() throws OutOfStockSpaceException,
			InconsistencyException, StockNotCompatibleException,
			PickTooEarlyException, PickTooLateException {
		Stock destination = new Stock("Test stock", StockType.SEMI, 1, 16, 1);
		StorageUnit su = new StorageUnit(destination, 0);
		destination.addStorageUnit(su);
		long t = System.currentTimeMillis();
		Date time = new Date(t);

		ProductType pt = new ProductType("Test Product Type");
		SubProcess sp1 = new SubProcess(0, "Test SP 1", 2, 3, 4), sp2 = new SubProcess(
				1, "Test SP 2", 2, 3, 4);
		sp1.addStock(destination);
		sp2.addStock(destination);
		pt.addSubProcess(sp1);
		pt.addSubProcess(sp2);
		Tray t1 = new Tray(pt);
		Set<Tray> trays = new HashSet<Tray>();
		trays.add(t1);

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(time);
		cal.add(GregorianCalendar.MINUTE, 4);

		Service.pickTrays(trays, destination, cal.getTime());
		Assert.assertEquals(1, su.getTrays().size());
		Assert.assertTrue(su.getTrays().contains(t1));
		Assert.assertEquals(t1.getCurrentState().getStartTime(), cal.getTime());

		Assert.fail("Exception not thrown!");
	}

	/**
	 * @author Alexander Peev
	 * @throws StockNotCompatibleException
	 *             or fails the test.
	 */
	@Test(expected = StockNotCompatibleException.class)
	public void testPickTraysIncompatible() throws OutOfStockSpaceException,
			InconsistencyException, StockNotCompatibleException,
			PickTooEarlyException, PickTooLateException {
		Stock destination = new Stock("Test stock", StockType.SEMI, 1, 16, 1);
		StorageUnit su = new StorageUnit(destination, 0);
		destination.addStorageUnit(su);
		long t = System.currentTimeMillis();
		Date time = new Date(t);

		ProductType pt = new ProductType("Test Product Type");
		SubProcess sp1 = new SubProcess(0, "Test SP 1", 2, 3, 4), sp2 = new SubProcess(
				1, "Test SP 2", 2, 3, 4);
		pt.addSubProcess(sp1);
		pt.addSubProcess(sp2);
		Tray t1 = new Tray(pt);
		Set<Tray> trays = new HashSet<Tray>();
		trays.add(t1);

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(time);
		cal.add(GregorianCalendar.MINUTE, 2);

		Service.pickTrays(trays, destination, cal.getTime());
		Assert.assertEquals(1, su.getTrays().size());
		Assert.assertTrue(su.getTrays().contains(t1));
		Assert.assertEquals(t1.getCurrentState().getStartTime(), cal.getTime());

		Assert.fail("Exception not thrown!");
	}

	/**
	 * @author Alexander Peev
	 * @throws OutOfStockSpaceException
	 *             or fails the test.
	 */
	@Test(expected = OutOfStockSpaceException.class)
	public void testPickTraysOverfilled() throws OutOfStockSpaceException,
			InconsistencyException, StockNotCompatibleException,
			PickTooEarlyException, PickTooLateException {
		Stock destination = new Stock("Test stock", StockType.SEMI, 1, 16, 1);
		StorageUnit su = new StorageUnit(destination, 0);
		for (int i = 0; i < destination.getMaxTraysPerStorageUnit(); i++) {
			su.addTray(new Tray());
		}
		destination.addStorageUnit(su);
		long t = System.currentTimeMillis();
		Date time = new Date(t);

		ProductType pt = new ProductType("Test Product Type");
		SubProcess sp1 = new SubProcess(0, "Test SP 1", 2, 3, 4), sp2 = new SubProcess(
				1, "Test SP 2", 2, 3, 4);
		sp1.addStock(destination);
		sp2.addStock(destination);
		pt.addSubProcess(sp1);
		pt.addSubProcess(sp2);
		Tray t1 = new Tray(pt);
		Set<Tray> trays = new HashSet<Tray>();
		trays.add(t1);

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(time);
		cal.add(GregorianCalendar.MINUTE, 2);

		Service.pickTrays(trays, destination, cal.getTime());
		Assert.assertEquals(1, su.getTrays().size());
		Assert.assertTrue(su.getTrays().contains(t1));
		Assert.assertEquals(t1.getCurrentState().getStartTime(), cal.getTime());

		Assert.fail("Exception not thrown!");
	}

	/**
	 * @author Tsvetomir Iliev
	 * @throws InconsistencyException
	 * @throws OutOfStockSpaceException
	 */
	@Test
	public void testCreateTray() throws OutOfStockSpaceException,
			InconsistencyException {

		int amount = 0;
		Set<Tray> trays = Service.createTrays(null, null, amount);
		Assert.assertEquals(trays, null);

		ProductType productType = new ProductType("Skumbananer");
		SubProcess sp = new SubProcess(0, "Test", 2, 3, 4);
		productType.addSubProcess(sp);

		trays = Service.createTrays(productType, null, amount);
		Assert.assertEquals(trays, null);

		Stock stock = new Stock("Cores Machines", StockType.MACHINE, 1, 150, 1);
		StorageUnit su = new StorageUnit(stock, 0);
		stock.addStorageUnit(su);
		sp.addStock(stock);

		trays = Service.createTrays(productType, stock, amount);
		Assert.assertNotNull(trays);
		Assert.assertEquals(trays.size(), 0);

		trays = Service.createTrays(null, stock, amount);
		Assert.assertEquals(trays, null);

		trays = Service.createTrays(productType, stock, amount);
		Assert.assertNotNull(trays);

		trays = Service.createTrays(null, stock, amount);
		Assert.assertEquals(trays, null);
		/*
		 * trays = Service.createTrays(productType, stock, amount);
		 * Assert.assertEquals(trays, null); trays =
		 * Service.createTrays(productType, stock, amount);
		 * Assert.assertNotNull(trays);
		 */
		trays = Service.createTrays(null, stock, 0);
		Assert.assertEquals(trays, null);

		trays = Service.createTrays(productType, null, 1);
		Assert.assertEquals(trays, null);

		stock.removeStorageUnit(su);
		su = new StorageUnit(stock, 0);
		stock.addStorageUnit(su);

		trays = Service.createTrays(productType, stock, 0);
		Assert.assertNotNull(trays);

		trays = Service.createTrays(productType, stock, 15);
		Assert.assertNotNull(trays);

		stock.removeStorageUnit(su);
		su = new StorageUnit(stock, 0);
		stock.addStorageUnit(su);

		trays = Service.createTrays(productType, stock, -15);
		Assert.assertNotNull(trays);

		stock.removeStorageUnit(su);
		su = new StorageUnit(stock, 0);
		stock.addStorageUnit(su);

		trays = Service.createTrays(productType, stock, 75);

		stock.removeStorageUnit(su);
		su = new StorageUnit(stock, 0);
		stock.addStorageUnit(su);
		Assert.assertNotNull(trays);

		trays = Service.createTrays(productType, stock, -1);
		Assert.assertNotNull(trays);

		stock.removeStorageUnit(su);
		su = new StorageUnit(stock, 0);
		stock.addStorageUnit(su);

		try {
			trays = Service.createTrays(productType, stock, 151);
			Assert.assertNotNull(trays);
			Assert.fail();
		}
		catch (OutOfStockSpaceException ex) {
			ex.getMessage();
		}

		stock.removeStorageUnit(su);
		su = new StorageUnit(stock, 0);
		stock.addStorageUnit(su);

		trays = Service.createTrays(productType, stock, -151);
		Assert.assertNotNull(trays);

		stock.removeStorageUnit(su);
		su = new StorageUnit(stock, 0);
		stock.addStorageUnit(su);

		trays = Service.createTrays(productType, stock, 15);
		Assert.assertNotNull(trays);

		stock.removeStorageUnit(su);
		su = new StorageUnit(stock, 0);
		stock.addStorageUnit(su);

	}
}
