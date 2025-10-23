package com.github.inc0grepoz.lix4j.unit.exp;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.util.PrimitiveTester;
import com.github.inc0grepoz.lix4j.value.Accessor;

public class OperatorTernary extends Operator
{

    public OperatorTernary(String name)
    {
        super(name, OperatorType.TERNARY);
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands)
    {
        // This is important to keep the computation below lazy
        return PrimitiveTester.isDefaultValue(operands[0].linkedAccess(ctx, null))
                ? operands[2].linkedAccess(ctx, null)
                : operands[1].linkedAccess(ctx, null);
    }

}
