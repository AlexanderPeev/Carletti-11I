package gui;

import model.Stock;
import model.StorageUnit;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Stock stock = new Stock("testing stock");
		for (int i = 0; i < 5; i++) {
			new StorageUnit(stock).getStock();
		}

		for (StorageUnit storageUnit : stock.getStorageUnits()) {
			System.out.println(storageUnit);
			System.out.println(storageUnit.getStock());
		}
	}
}
