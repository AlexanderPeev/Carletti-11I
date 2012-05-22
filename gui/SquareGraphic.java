/**
 * @author Ricardas Risys
 */

package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Stock;
import model.StockState;
import model.StorageUnit;

public class SquareGraphic extends JPanel {

	private Graphics2D g2d;
	private boolean isSelected = false;
	private Rectangle2D.Double square, squareInside;
	private StockState state = StockState.EARLY;
	private Stock stock;
	private JLabel lblName;

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		square = new Rectangle2D.Double(0, 0, this.getWidth() - 1,
				this.getHeight() - 1);
		squareInside = new Rectangle2D.Double(1, 1, this.getWidth() - 3,
				this.getHeight() - 3);
		g2d = (Graphics2D) g;
		g2d.setPaint(StockState.getColor(state));
		g2d.fill(square);

		if (this.isSelected == true) {
			g2d.setPaint(Color.DARK_GRAY);
			g2d.draw(square);
			g2d.setPaint(Color.DARK_GRAY);
			g2d.draw(squareInside);
		}

		updateStockInfo();
		lblName.setBounds(10, 10, this.getWidth() - 20, this.getHeight() - 20);

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

	public void updateStockInfo() {
		int usedStocks = 0;

		for (StorageUnit su : stock.getStorageUnits()) {
			if (su.getTrays().size() > 0) {
				usedStocks++;
			}
		}

		int percent = 100 - (usedStocks * 100 / stock.getCapacity());
		String stockInfo = "Name: " + stock.getName() + "<br>";
		stockInfo += "Remaining Space: " + percent + "%";

		lblName.setText("<html>" + stockInfo + "</html>");
	}

	public void setStock(Stock stock) {
		this.stock = stock;

		lblName = new JLabel();
		updateStockInfo();
		add(lblName);
	}

	public Stock getStock() {
		return this.stock;
	}

}
