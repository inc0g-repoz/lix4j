package com.github.inc0grepoz.lix4j.unit.exp;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.util.PrimitiveConverter;
import com.github.inc0grepoz.lix4j.value.Accessor;

/**
 * Implements a chained number multiplication operator.
 * 
 * @author inc0g-repoz
 */
public class OperatorMultiply extends Operator
{

    public OperatorMultiply(String name)
    {
        super(name, OperatorType.BINARY);
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands)
    {
        Object object;
        double rv = 0d;

        // Going through all of the operands and multiplying
        // the first operand by them
        for (int i = 0; i < operands.length; i++)
        {
            object = operands[i].linkedAccess(ctx, null);

            // This operator can only be applied to numbers
            if (object instanceof Number)
            {
                // The first operand is not multiplied
                if (i == 0)
                {
                    rv = ((Number) object).doubleValue();
                }
                // Dividers are used on the divisible
                else
                {
                    rv *= ((Number) object).doubleValue();
                }
            }
            else
            {
                throw new UnsupportedOperationException("Attempted to multiply an illegal type");
            }
        }

        // A result of a narrowed or the same type is returned
        return PrimitiveConverter.narrow(rv);
    }

}
