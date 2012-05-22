package model;

import java.util.List;

/**
 * Strategy pattern behavior interface. Composited by the Stock class. Provides
 * a decoupled implementation of a tray storing algorithm.
 * 
 * @author Alexander Peev
 * 
 */
public interface StockStoreBehavior {
	public void storeTrays(List<Tray> trays, Stock destination);
}
