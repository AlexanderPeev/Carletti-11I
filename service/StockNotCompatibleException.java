package service;

/**
 * Thrown in case an attempt is made to store trays in a Stock which is not
 * compatible to the trays' next SubProcess.
 * 
 * @author Alexander Peev
 * 
 */
public class StockNotCompatibleException extends Exception {
	public StockNotCompatibleException() {
		super();
	}

	public StockNotCompatibleException(String message) {
		super(message);
	}
}
