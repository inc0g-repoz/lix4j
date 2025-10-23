package com.github.inc0grepoz.lix4j.unit.exp;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.util.PrimitiveTester;
import com.github.inc0grepoz.lix4j.value.Accessor;

public class OperatorNot extends Operator
{

    public OperatorNot(String name)
    {
        super(name, OperatorType.UNARY_LEFT);
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands)
    {
        return PrimitiveTester.isDefaultValue(operands[0].linkedAccess(ctx, null));
    }

}
