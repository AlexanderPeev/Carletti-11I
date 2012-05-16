package service;

/**
 * Thrown if an attempt is made to pick a tray after its maximum time has
 * elapsed for the current sub process.
 * 
 * @author Alexander Peev
 * 
 */
public class PickTooLateException extends Exception {

	public PickTooLateException() {
	}

	public PickTooLateException(String message) {
		super(message);
	}

	public PickTooLateException(Throwable cause) {
		super(cause);
	}

	public PickTooLateException(String message, Throwable cause) {
		super(message, cause);
	}

}
