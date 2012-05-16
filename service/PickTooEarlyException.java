package service;

/**
 * Thrown if an attempt is made to pick a tray before its minimum time has
 * elapsed for the current sub process.
 * 
 * @author Alexander Peev
 * 
 */
public class PickTooEarlyException extends Exception {

	public PickTooEarlyException() {
	}

	public PickTooEarlyException(String message) {
		super(message);
	}

	public PickTooEarlyException(Throwable cause) {
		super(cause);
	}

	public PickTooEarlyException(String message, Throwable cause) {
		super(message, cause);
	}

}
