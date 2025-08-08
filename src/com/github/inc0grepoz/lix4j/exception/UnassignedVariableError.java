package com.github.inc0grepoz.lix4j.exception;

/**
 * An exception that can be thrown due to an unassigned
 * variable being accessed.
 * 
 * @author inc0g-repoz
 */
public final class UnassignedVariableError extends RuntimeException
{

    // Implements serializable
    private static final long serialVersionUID = -3779776297422589337L;

    /**
     * Constructs a new unassigned variable error with the specified
     * detail message and cause.
     * 
     * @param message the detail message
     */
    public UnassignedVariableError(String message)
    {
        super(message);
    }

    /**
     * Constructs a new unassigned variable error with the specified
     * detail message and cause.
     * 
     * @param message   the detail message
     * @param throwable the cause
     */
    public UnassignedVariableError(String message, Throwable throwable)
    {
        super(message, throwable);
    }

}
