/**
 * @author Ricardas Risys
 */

package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import model.Stock;

public class SquareGraphic extends JPanel {

	private Graphics2D g2d;
	private boolean isSelected = false;
	private Rectangle2D.Double square = new Rectangle2D.Double(0, 0, 83, 83);
	private Rectangle2D.Double squareInside = new Rectangle2D.Double(1, 1, 81,
			81);
	private StockState state = StockState.EARLY;
	private Stock stock;

	// private GradientPaint gradient = new GradientPaint(0, 0, Color.YELLOW,
	// 83,
	// 0, Color.BLUE, true);

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2d = (Graphics2D) g;
		if (state.equals(StockState.EARLY)) {
			g2d.setPaint(new Color(0x5d74ff)); // too early
		}

		if (state.equals(StockState.MINOPT)) {
			g2d.setPaint(new Color(0x3bbb25)); // between min-opt
		}

		if (state.equals(StockState.OPTMAX)) {
			g2d.setPaint(new Color(0xe7f730)); // between opt-max
		}

		if (state.equals(StockState.WASTE)) {
			g2d.setPaint(new Color(0xff3636)); // waste
		}
		g2d.fill(square);

		if (this.isSelected == true) {
			g2d.setPaint(Color.DARK_GRAY);
			g2d.draw(square);
			g2d.setPaint(Color.DARK_GRAY);
			g2d.draw(squareInside);
		}
	}

	public void deselect() {
		this.isSelected = false;
		repaint();
	}

	public void toggleClick() {
		if (this.isSelected == false) {
			this.isSelected = true;
		} else {
			this.isSelected = false;
		}
		repaint();
	}

	public void setState(StockState state) {
		this.state = state;
	}

	public boolean getIsSelected() {
		return this.isSelected;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public Stock getStock() {
		return this.stock;
	}

	enum StockState {
		EARLY, MINOPT, OPTMAX, WASTE;
	}

}
