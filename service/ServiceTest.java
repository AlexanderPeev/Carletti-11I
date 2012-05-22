/**
 * 
 */
package service;

import java.sql.Date;

import junit.framework.Assert;
import model.ProductType;
import model.State;
import model.Stock;
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
	 */
	@Test
	public void testPickTrays() {
		;
	}

}
