package com.github.inc0grepoz.lix4j.unit.expression;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.util.PrimitiveTester;
import com.github.inc0grepoz.lix4j.value.Accessor;

/**
 * Implements an equality operator.
 * 
 * @author inc0g-repoz
 */
public class OperatorEqual extends Operator
{

    public OperatorEqual(String name)
    {
        super(name, OperatorType.BINARY);
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands)
    {
        // The first operand is accessed and evaluated right away, and
        // we consider all of the operands equal in the beginning
        Object scd, fst = operands[0].linkedAccess(ctx, null);
        boolean equal = true;

        // Looping through chained operands and looking for something
        // that is "not equal"
        for (int i = 1; i < operands.length; i++)
        {
            scd = operands[i].linkedAccess(ctx, null);

            // Numbers are compared by values
            if (PrimitiveTester.isPrimitiveType(fst)
                    && PrimitiveTester.isPrimitiveType(scd))
            {
                if (fst instanceof Number && scd instanceof Number) // primitives
                {
                    equal = ((Number) fst).doubleValue() == ((Number) scd).doubleValue();
                }
                else // wrapped primitives
                {
                    equal = fst.equals(scd);
                }
            }
            // Objects are compared by pointers
            else
            {
                equal = fst == scd;
            }

            if (!equal)
            {
                return false; // not all of them are equal
            }

            // Saving the current operand value
            fst = scd;
        }

        return true; // all equal
    }

}
