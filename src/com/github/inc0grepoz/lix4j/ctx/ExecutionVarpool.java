package com.github.inc0grepoz.lix4j.ctx;

import java.util.ArrayDeque;

import com.github.inc0grepoz.lix4j.value.AccessorUnassigned;

/**
 * Represents a non-static pool for variables.
 * 
 * @author inc0g-repoz
 */
public class ExecutionVarpool extends Varpool<Object> implements Cloneable
{

    /**
     * Creates a new non-static pool.
     */
    public ExecutionVarpool()
    {
        this(new ArrayDeque<>());
    }

    private ExecutionVarpool(ArrayDeque<Lookup<Object>> stack)
    {
        super(stack);
    }

    @Override
    public Object get(Identifier identifier)
    {
        Object o = super.get(identifier);

        return o == null ? new AccessorUnassigned(identifier) : o;
    }

    @Override
    public ExecutionVarpool clone()
    {
        return new ExecutionVarpool(stack.clone());
    }

}
