package service;

public class ProductTypeInProductionException extends Exception {

	public ProductTypeInProductionException() {
	}

	public ProductTypeInProductionException(String message) {
		super(message);
	}

	public ProductTypeInProductionException(Throwable cause) {
		super(cause);
	}

	public ProductTypeInProductionException(String message, Throwable cause) {
		super(message, cause);
	}

}
