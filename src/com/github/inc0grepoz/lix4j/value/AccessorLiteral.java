package com.github.inc0grepoz.lix4j.value;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;

/**
 * Stores a single precached Java object or a wrapped
 * primitive value for a read-only access.
 * 
 * @author inc0g-repoz
 */
public class AccessorLiteral extends Accessor
{

    /**
     * Creates a new {@code AccessorValue} with a specified
     * instance stored.
     * 
     * @param instance an {@code Object} to store
     * @return a new {@code AccessorValue}
     */
    public static AccessorLiteral of(Object instance)
    {
        return new AccessorLiteral(instance);
    }

    final Object instance;

    AccessorLiteral(Object instance)
    {
        this.instance = instance;
    }

    @Override
    public String toString()
    {
        if (instance == null)
        {
            return null;
        }

        String rv;

        if (instance instanceof String)
        {
            rv = '"' + (String) instance + '"';
        }
        else
        {
            rv = instance.toString();
        }

        return rv + (next == null ? "" : "." + next);
    }

    @Override
    public Object access(ExecutionContext ctx, Object src)
    {
        return instance;
    }

}
