package gui;

import java.util.Date;

import javax.swing.JFrame;

import model.ProductType;
import model.State;
import model.Stock;
import model.StockType;
import model.StorageUnit;
import model.SubProcess;
import model.Tray;

public class StorageUnitDisplayTest extends JFrame {
	private StorageUnitDisplayPanel pnlDisplay;
	private StorageUnit unit;
	private ProductType product;
	private SubProcess subProcess;

	public StorageUnitDisplayTest(DisplayMode mode) {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if (mode == DisplayMode.FULL) {
			this.setLocation(200, 200);
			this.setSize(400, 500);
		}
		else {
			this.setLocation(20, 20);
			this.setSize(40, 120);
		}
		this.setTitle(mode + " Resizable Storage Unit Display Tester");
		unit = new StorageUnit(new Stock("Test stock", StockType.SEMI, 30, 16,
				5), 3);
		pnlDisplay = new StorageUnitDisplayPanel(unit, mode);
		product = new ProductType("Skumbananer");
		subProcess = new SubProcess(0, "Core Production", 100, 200, 300);
		product.addSubProcess(subProcess);
		Tray tray = new Tray(unit, product, 3);
		State state = new State(tray, new Date(
				System.currentTimeMillis() - 80 * 60000));
		tray.addState(state);
		state.setSubProcess(subProcess);
		unit.addTray(tray);
		// if (mode != DisplayMode.FULL)
		pnlDisplay.addSelectedTray(tray);
		tray = new Tray(unit, product, 4);
		state = new State(tray, new Date(
				System.currentTimeMillis() - 120 * 60000));
		tray.addState(state);
		state.setSubProcess(subProcess);
		unit.addTray(tray);
		// if (mode != DisplayMode.FULL)
		pnlDisplay.addSelectedTray(tray);
		tray = new Tray(unit, product, 9);
		state = new State(tray, new Date(
				System.currentTimeMillis() - 220 * 60000));
		tray.addState(state);
		state.setSubProcess(subProcess);
		unit.addTray(tray);
		tray = new Tray(unit, product, 12);
		state = new State(tray, new Date(
				System.currentTimeMillis() - 320 * 60000));
		tray.addState(state);
		state.setSubProcess(subProcess);
		unit.addTray(tray);
		this.add(pnlDisplay);
	}

	public static void main(String[] args) {
		StorageUnitDisplayTest frame = new StorageUnitDisplayTest(
				DisplayMode.COMPACT), frame2 = new StorageUnitDisplayTest(
				DisplayMode.FULL);
		frame.setVisible(true);
		frame2.setVisible(true);
	}
}
