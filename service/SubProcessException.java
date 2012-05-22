package service;

/**
 * Thrown if wrong data is provided for create/update sub process
 * 
 * @author Ricardas Risys
 * 
 */
public class SubProcessException extends Exception {

	public SubProcessException() {
	}

	public SubProcessException(String message) {
		super(message);
	}

	public SubProcessException(Throwable cause) {
		super(cause);
	}

	public SubProcessException(String message, Throwable cause) {
		super(message, cause);
	}

}
