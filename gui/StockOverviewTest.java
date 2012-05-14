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

public class StockOverviewTest extends JFrame {
	private StockOverviewPanel pnlDisplay;
	private static StorageUnit unit;
	private ProductType product = new ProductType("Skumbananer");
	private SubProcess subProcess = new SubProcess(0, "Core Production", 100,
			200, 300);
	private Stock stock;

	public StockOverviewTest() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocation(200, 200);
		this.setSize(600, 400);

		this.setTitle("Resizable Stock Overview Tester");
		if (unit == null) {
			stock = new Stock("Test stock", StockType.SEMI, 30, 16, 5);
			for (int i = 0; i < 23; i++) {
				unit = new StorageUnit(stock, 3);
				if (i == 20) product = new ProductType("P-Taerter");
				if (i == 20) subProcess = new SubProcess(0, "Core Production",
						100, 200, 300);
				product.addSubProcess(subProcess);
				Tray tray = new Tray(unit, product, 3);
				State state = new State(tray, new Date(
						System.currentTimeMillis() - 80 * 60000));
				tray.addState(state);
				state.setSubProcess(subProcess);
				unit.addTray(tray);
				// if (mode != DisplayMode.FULL)
				StorageUnitDisplayPanel.addSelectedTray(tray);
				tray = new Tray(unit, product, 4);
				state = new State(tray, new Date(
						System.currentTimeMillis() - 99 * 60000));
				tray.addState(state);
				state.setSubProcess(subProcess);
				unit.addTray(tray);
				// if (mode != DisplayMode.FULL)
				StorageUnitDisplayPanel.addSelectedTray(tray);
				tray = new Tray(unit, product, 9);
				state = new State(tray, new Date(
						System.currentTimeMillis() - 198 * 60000));
				tray.addState(state);
				state.setSubProcess(subProcess);
				unit.addTray(tray);
				tray = new Tray(unit, product, 12);
				state = new State(tray, new Date(
						System.currentTimeMillis() - 297 * 60000));
				tray.addState(state);
				state.setSubProcess(subProcess);
				unit.addTray(tray);
			}
		}
		pnlDisplay = new StockOverviewPanel(null);
		pnlDisplay.setStock(stock);
		this.add(pnlDisplay);
	}

	public static void main(String[] args) {
		StockOverviewTest frame = new StockOverviewTest();
		frame.setVisible(true);
	}

}
