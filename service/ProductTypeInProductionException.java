package service;

/**
 * Thrown in case an attempt is made to delete a ProductType while it still has
 * trays associated with it.
 * 
 * @author Alexander Peev
 * 
 */
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
