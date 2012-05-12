package model;

/**
 * A generic exception thrown in case an inconsistency in the data storage or
 * overall application is discovered.
 * 
 * @author Alexander Peev
 * 
 */
public class InconsistencyException extends Exception {
	public InconsistencyException() {
		super();
	}

	public InconsistencyException(String message) {
		super(message);
	}
}
