package com.github.inc0grepoz.lix4j.ctx;

import java.util.ArrayDeque;
import java.util.Map;

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

    private ExecutionVarpool(ArrayDeque<Map<String, Object>> stack)
    {
        super(stack);
    }

    @Override
    public Object get(String namespace, String name)
    {
        Object o = super.get(namespace, name);

        return o == null ? new AccessorUnassigned(namespace, name) : o;
    }

    @Override
    public ExecutionVarpool clone()
    {
        return new ExecutionVarpool(stack.clone());
    }

}
