package com.github.inc0grepoz.lix4j.value;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;

/**
 * Represents an accessor for static variables.
 * 
 * @author inc0g-repoz
 */
public class AccessorVariableStatic extends AccessorVariable
{

    /**
     * Creates and returns a new accessor for a variable
     * with an existing pointer.
     * 
     * @param var the variable pointer
     * @return a new {@code AccessorVariableStatic}
     */
    public static AccessorVariableStatic of(Variable var)
    {
        return new AccessorVariableStatic(var);
    }

    /**
     * Creates and returns a new accessor for a variable
     * with a specified name.
     * 
     * @param name the variable name
     * @return a new {@code AccessorVariableStatic}
     */
    public static AccessorVariableStatic of(String namespace, String name)
    {
        return of(new Variable(namespace, name));
    }

    private final Variable variable;

    AccessorVariableStatic(Variable var)
    {
        super(var.getNamespace(), var.getName());
        variable = var;
    }

    @Override
    public String toString()
    {
        return "static->" + super.toString();
    }

    @Override
    public Object access(ExecutionContext ctx, Object src)
    {
        if (src != null && src.getClass() == AccessorUnassigned.class)
        {
            src = ((AccessorUnassigned) src).access(null, null);
        }

        Object rv = variable.get();
        return elementIndex == null ? rv : accessElement(ctx, unwrapSource(rv));
    }

    @Override
    public Object mutate(ExecutionContext ctx, Object src, Object val)
    {
        if (elementIndex == null)
        {
            return next == null
                    ? variable.set(val)
                    : next.mutate(ctx, variable.get(), val);
        }

        return mutateElement(ctx, variable.get(), val);
    }

    public Variable getVariable()
    {
        return variable;
    }

}
