package de.uka.iti.lights;

/**
 * This exception is thrown by class {@link Lights} if a board decription has a
 * wrong format.
 * 
 * @author Mattias Ulbrich
 * @version 2008.1
 */
public class LightsFormatException extends Exception {

	private static final long serialVersionUID = -1030942250398403013L;

	public LightsFormatException() {
		super();
	}

	public LightsFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public LightsFormatException(String message) {
		super(message);
	}

	public LightsFormatException(Throwable cause) {
		super(cause);
	}

}
