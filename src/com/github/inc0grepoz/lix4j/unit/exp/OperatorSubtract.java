package com.github.inc0grepoz.lix4j.unit.exp;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.util.PrimitiveConverter;
import com.github.inc0grepoz.lix4j.value.Accessor;

public class OperatorSubtract extends Operator
{

    public OperatorSubtract(String name)
    {
        super(name, OperatorType.BINARY);
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands)
    {
        if (operands[0] == Accessor.NULL)
        {
            operands[0] = Accessor.ZERO;
        }

        Object object;
        double rv = 0d;

        for (int i = 0; i < operands.length; i++)
        {
            object = operands[i].linkedAccess(ctx, null);

            if (object instanceof Number)
            {
                if (i == 0)
                {
                    rv = ((Number) object).doubleValue();
                }
                else
                {
                    rv -= ((Number) object).doubleValue();
                }
            }
            else
            {
                throw new UnsupportedOperationException("Could not substract from " + object);
            }
        }

        return PrimitiveConverter.narrow(rv);
    }

}
