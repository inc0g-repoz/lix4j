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
     * @param name the variable name
     * @return a new {@code AccessorVariable}
     */
    public static AccessorVariableStack of(String name)
    {
        return new AccessorVariableStack(name);
    }

    AccessorVariableStack(String name)
    {
        super(name);
    }

    @Override
    public String toString()
    {
        return "$" + name + (next == null ? "" : "." + next);
    }

    @Override
    public Object access(ExecutionContext ctx, Object src) {
        Object rv = ctx.getVarpool().get(name);
        return elementIndex == null ? rv : accessElement(ctx, rv);
    }

    @Override
    public Object mutate(ExecutionContext ctx, Object src, Object val)
    {
        if (elementIndex == null)
        {
            return next == null
                    ? ctx.getVarpool().set(name, val)
                    : next.mutate(ctx, ctx.getVarpool().get(name), val);
        }

        return mutateElement(ctx, ctx.getVarpool().get(name), val);
    }

}
