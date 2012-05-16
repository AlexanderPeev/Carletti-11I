package service;

public class StockNotCompatibleException extends Exception {
	public StockNotCompatibleException() {
		super();
	}

	public StockNotCompatibleException(String message) {
		super(message);
	}
}
