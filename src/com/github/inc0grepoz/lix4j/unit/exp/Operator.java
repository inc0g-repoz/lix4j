package com.github.inc0grepoz.lix4j.unit.exp;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.value.Accessor;

import ctxfree.lex.Lexer;

/**
 * Represents an abstract implementation of some operator.
 * 
 * @author inc0g-repoz
 */
public abstract class Operator
{

    final String name;
    final OperatorType type;

    /**
     * Creates a new {@code operator} abstraction with a
     * name and type specified.
     * 
     * @param name
     * @param type
     */
    protected Operator(String name, OperatorType type)
    {
        this.name = name;
        this.type = type;
    }

    /**
     * Returns the name of this {@code Operator}
     * 
     * @return the name of this {@code Operator}
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the type of this {@code Operator}
     * 
     * @return the type of this {@code Operator}
     */
    public OperatorType getType()
    {
        return type;
    }

    /**
     * Returns {@code true}, if the given token matches
     * the name of this operator; otherwise, {@code false}.
     * 
     * @param token a token to test
     * @return a {@code boolean} value
     */
    public boolean isNameMatching(Lexer.Token token)
    {
        if (token == null) return false;
        switch (token.getTokenType())
        {
        case SPECIAL_CHARACTER:
            return name.length() == 1 && name.charAt(0) == token.getChar();
        case SPECIAL_COMPOSITE:
            return name.equals(token.getString());
        default:
            return false;
        }
    }

    /**
     * Calculates and returns the result of the operation
     * performed on the specified operands in the given
     * function {@code ExecutionContext}.
     * 
     * @param ctx      a function {@code ExecutionContext}
     * @param operands an array of accessors for operands
     * @return the result of the operation performed
     */
    public abstract Object evaluate(ExecutionContext ctx, Accessor[] operands);

}
