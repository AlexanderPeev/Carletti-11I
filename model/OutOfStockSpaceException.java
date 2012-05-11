package model;

/**
 * 
 * @author Alexander Peev
 * 
 */
public class OutOfStockSpaceException extends Exception {
	public OutOfStockSpaceException() {
		super();
	}

	public OutOfStockSpaceException(String message) {
		super(message);
	}
}
