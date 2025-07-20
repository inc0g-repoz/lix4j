package com.github.inc0grepoz.lix4j.unit.expression;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.value.Accessor;

/**
 * Implements the assignment operator.
 * 
 * @author inc0g-repoz
 */
public class OperatorAssign extends Operator
{

    public OperatorAssign(String name)
    {
        super(name, OperatorType.BINARY);
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands)
    {
        Object rv = null;

        // Every right-hand part of the expression needs to be assigned to the
        // variable in the left-hand part, e.g. "third = second = first"
        for (int i = operands.length - 2; 0 <= i; i--)
        {
            // Accessing the right-hand to assign it to the left-hand
            rv = operands[i].mutate(ctx, null, operands[i + 1].linkedAccess(ctx, null));
        }

        return rv; // the far left variable value
    }

}
