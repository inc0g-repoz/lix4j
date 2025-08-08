package com.github.inc0grepoz.lix4j.value;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;

/**
 * Represents an accessor for variables stored in a
 * variable pool during execution of some function.
 * 
 * @author inc0g-repoz
 */
public class AccessorVariableStack extends AccessorVariable
{

    /**
     * Creates and returns a new accessor for a variable
     * with a specified name.
     * 
     * @param namespace the variable namespace
     * @param name      the variable name
     * @return a new {@code AccessorVariable}
     */
    public static AccessorVariableStack of(String namespace, String name)
    {
        return new AccessorVariableStack(namespace, name);
    }

    AccessorVariableStack(String namespace, String name)
    {
        super(namespace, name);
    }

    @Override
    public Object access(ExecutionContext ctx, Object src)
    {
        Object rv = ctx.getVarpool().get(namespace, name);
        return elementIndex == null ? rv : accessElement(ctx, unwrapSource(rv));
    }

    @Override
    public Object mutate(ExecutionContext ctx, Object src, Object val)
    {
        if (elementIndex == null)
        {
            return next == null
                    ? ctx.getVarpool().set(namespace, name, val)
                    : next.mutate(ctx, ctx.getVarpool().get(namespace, name), val);
        }

        return mutateElement(ctx, ctx.getVarpool().get(namespace, name), val);
    }

}
