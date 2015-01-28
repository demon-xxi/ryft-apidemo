package com.metasys.ryft;

/**
 * Exception holding a message that can be displayed to the user.
 *
 * @author Sylvain Crozon
 *
 */
public class RyftException extends Exception {

    public static final RyftException GENERIC = new RyftException("Something went wrong");
    public static final RyftException GENERATE_PROGRAM = new RyftException("Error generating the C program");
    public static final RyftException EXECUTE_PROGRAM = new RyftException("Error executing the C program");

    public RyftException(String message) {
        super(message);
    }

}
