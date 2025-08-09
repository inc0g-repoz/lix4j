package com.github.inc0grepoz.lix4j.value;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.ctx.Identifier;

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
        return new AccessorVariableStatic(var, false);
    }

    /**
     * Creates and returns a new accessor for a variable
     * with a specified name.
     * 
     * @param identifier the variable identifier
     * @return a new {@code AccessorVariableStatic}
     */
    public static AccessorVariableStatic of(Identifier identifier)
    {
        return new AccessorVariableStatic(new Variable(identifier), true);
    }

    private final Variable variable;
    private final boolean declaration;

    AccessorVariableStatic(Variable variable, boolean declaration)
    {
        super(variable.getIdentifier());

        this.variable = variable;
        this.declaration = declaration;
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
                    ? variable.set(val, declaration)
                    : next.mutate(ctx, variable.get(), val);
        }

        return mutateElement(ctx, variable.get(), val);
    }

    public Variable getVariable()
    {
        return variable;
    }

}
