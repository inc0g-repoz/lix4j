package com.github.inc0grepoz.lix4j.unit.expression;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.util.PrimitiveConverter;
import com.github.inc0grepoz.lix4j.value.Accessor;

/**
 * Implements a chained number division operator.
 * 
 * @author inc0g-repoz
 */
public class OperatorDivide extends Operator
{

    public OperatorDivide(String name)
    {
        super(name, OperatorType.BINARY);
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands)
    {
        Object object;
        double rv = 0d;

        // Going through all of the operands and dividing
        // the first operand by them
        for (int i = 0; i < operands.length; i++)
        {
            object = operands[i].linkedAccess(ctx, null);

            // This operator can only be applied to numbers
            if (object instanceof Number)
            {
                // The first operand is a divisible
                if (i == 0)
                {
                    rv = ((Number) object).doubleValue();
                }
                // Dividers are used on the divisible
                else
                {
                    rv /= ((Number) object).doubleValue();
                }
            }
            else
            {
                throw new UnsupportedOperationException("Attempted to divide an illegal type");
            }
        }

        // A result of a narrowed or the same type is returned
        return PrimitiveConverter.narrow(rv);
    }

}
