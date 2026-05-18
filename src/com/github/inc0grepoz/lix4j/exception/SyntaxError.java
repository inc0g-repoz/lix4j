package com.github.inc0grepoz.lix4j.exception;

import ctxfree.lex.AST;

/**
 * An exception that can be thrown due to bad syntax
 * during a script compilation.
 * 
 * @author inc0g-repoz
 */
public final class SyntaxError extends RuntimeException
{

    public static void unexpectedTokenGroupType(AST.Node node)
    {
        String gtn = node.getGroupType().name().toLowerCase();
        StringBuilder msg = new StringBuilder();
        msg.append("Unexpected token group type (");
        msg.append(gtn);
        msg.append("), line: ");
        msg.append(node.getLine());
        throw new SyntaxError(msg.toString());
    }

    // Implements serializable
    private static final long serialVersionUID = -7932907360413850284L;

    /**
     * Constructs a new syntax error with the specified detail message.
     * 
     * @param message the detail message
     */
    public SyntaxError(String message)
    {
        super(message);
    }

    /**
     * Constructs a new syntax error with the specified detail message
     * and cause.
     * 
     * @param message   the detail message
     * @param throwable the cause
     */
    public SyntaxError(String message, Throwable throwable)
    {
        super(message, throwable);
    }

}
