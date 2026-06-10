package com.github.inc0grepoz.lix4j.ctx.pool;

import java.util.ArrayDeque;

import com.github.inc0grepoz.lix4j.lookup.Lookup;
import com.github.inc0grepoz.lix4j.lookup.id.ResolvedIdentifier;
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
    public Object get(ResolvedIdentifier id)
    {
        Object o = super.get(id);
        return o == null ? new AccessorUnassigned(id) : o;
    }

    @Override
    public ExecutionVarpool clone()
    {
        return new ExecutionVarpool(stack.clone());
    }

}
