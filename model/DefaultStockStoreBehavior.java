package model;

import java.util.Iterator;
import java.util.List;

/**
 * Default behavior for storing trays in a stock. It uses the first-fit
 * principle.
 * 
 * @author Alexander Peev
 * 
 */
public class DefaultStockStoreBehavior implements StockStoreBehavior {
	@Override
	public void storeTrays(List<Tray> trays, Stock destination) {
		Iterator<StorageUnit> i = destination.getStorageUnitsIterator();
		while (i.hasNext()) {
			StorageUnit unit = i.next();
			for (int taken = unit.getTrays().size(); taken < destination
					.getMaxTraysPerStorageUnit(); taken++) {
				Tray tray = trays.remove(0);
				unit.addTray(tray);
				if (trays.size() < 1) return;
			}
		}
	}
}
