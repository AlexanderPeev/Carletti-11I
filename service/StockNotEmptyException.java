package service;

/**
 * Thrown in case a particular Stock object is not empty of Trays. Should be
 * thrown only in cases when this occurrence is erroneous, e.g. when deleting a
 * Stock.
 * 
 * @author Alexander Peev
 * 
 */
public class StockNotEmptyException extends Exception {
	public StockNotEmptyException() {
		super();
	}

	public StockNotEmptyException(String message) {
		super(message);
	}
}
