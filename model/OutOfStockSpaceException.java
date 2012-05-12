package model;

/**
 * A generic exception, thrown when a request to store Trays in a particular
 * Stock fails, due to a lack of available free space.
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
