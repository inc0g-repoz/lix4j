package com.github.inc0grepoz.lix4j.value;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.ctx.Identifier;

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
     * with a specified identifier.
     * 
     * @param id the variable identifier
     * @return a new {@code AccessorVariable}
     */
    public static AccessorVariableStack of(Identifier id)
    {
        return new AccessorVariableStack(id);
    }

    AccessorVariableStack(Identifier identifier)
    {
        super(identifier);
    }

    @Override
    public Object access(ExecutionContext ctx, Object src)
    {
        Object rv = ctx.getVarpool().get(identifier);
        return elementIndex == null ? rv : accessElement(ctx, unwrapSource(rv));
    }

    @Override
    public Object mutate(ExecutionContext ctx, Object src, Object val)
    {
        if (elementIndex == null)
        {
            return next == null
                    ? ctx.getVarpool().set(identifier, val)
                    : next.mutate(ctx, ctx.getVarpool().get(identifier), val);
        }

        return mutateElement(ctx, ctx.getVarpool().get(identifier), val);
    }

}
