package com.github.inc0grepoz.lix4j.unit.exp;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.util.PrimitiveConverter;
import com.github.inc0grepoz.lix4j.value.Accessor;

/**
 * Implements a summation and concatenation operator.
 * 
 * @author inc0g-repoz
 */
public class OperatorAdd extends Operator
{

    public OperatorAdd(String name)
    {
        super(name, OperatorType.BINARY);
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands)
    {
        // The first operand may be absent, e.g. "+1"
        if (operands[0] == Accessor.NULL)
        {
            operands[0] = Accessor.ZERO;
        }

        Object[] objects = new Object[operands.length];
        boolean allNumbers = true;

        // The operands values are accessed before the operator is used
        // on them. If at least one of the operands is not a number, all
        // of them should be treated as they're strings.
        for (int i = 0; i < operands.length; i++)
        {
            objects[i] = operands[i].linkedAccess(ctx, null);

            if (!(objects[i] instanceof Number))
            {
                allNumbers = false;
            }
        }

        // Since all of the operands are numbers, a sum can be calculated
        // and a number of a narrowed or the same type returned
        if (allNumbers)
        {
            double rv = 0d;

            for (int i = 0; i < objects.length; i++)
            {
                rv += ((Number) objects[i]).doubleValue();
            }

            return PrimitiveConverter.narrow(rv);
        }

        StringBuilder builder = new StringBuilder();

        // Since at least one of the operators' type is not numeric, a string
        // value is added into the builder for every element and a new string
        // instance is built and returned
        for (int i = 0; i < objects.length; i++)
        {
            builder.append(objects[i]);
        }

        return builder.toString();
    }

}
