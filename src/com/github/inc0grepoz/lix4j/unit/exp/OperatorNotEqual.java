package com.github.inc0grepoz.lix4j.unit.exp;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.util.PrimitiveTester;
import com.github.inc0grepoz.lix4j.value.Accessor;

public class OperatorNotEqual extends Operator
{

    public OperatorNotEqual(String name)
    {
        super(name, OperatorType.BINARY);
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands)
    {
        Object scd, fst = operands[0].linkedAccess(ctx, null);
        boolean equal = true;

        for (int i = 1; i < operands.length; i++)
        {
            scd = operands[i].linkedAccess(ctx, null);

            if (PrimitiveTester.isPrimitiveType(fst)
                    && PrimitiveTester.isPrimitiveType(scd))
            {
                if (fst instanceof Number && scd instanceof Number)
                {
                    equal = ((Number) fst).doubleValue() == ((Number) scd).doubleValue();
                }
                else
                {
                    equal = fst.equals(scd);
                }
            }
            else
            {
                equal = fst == scd;
            }

            if (equal)
            {
                return false;
            }

            fst = scd;
        }

        return true;
    }

}
