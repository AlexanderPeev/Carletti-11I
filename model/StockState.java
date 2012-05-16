/**
 * @author Ricardas Risys
 */

package model;

import java.awt.Color;

public enum StockState {
	EARLY, MINIMUM_OPTIMAL, OPTIMAL_MAXIMUM, WASTE;

	public static Color getColor(StockState state) {
		if (state.equals(StockState.EARLY)) {
			return new Color(0x5d74ff);
		} else if (state.equals(StockState.MINIMUM_OPTIMAL)) {
			return new Color(0x3bbb25);
		} else if (state.equals(StockState.OPTIMAL_MAXIMUM)) {
			return new Color(0xe7f730);
		} else if (state.equals(StockState.WASTE)) {
			return new Color(0xff3636);
		}

		return null;
	}
}
