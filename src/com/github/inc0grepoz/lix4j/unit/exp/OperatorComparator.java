package com.github.inc0grepoz.lix4j.unit.exp;

import java.util.function.BiFunction;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.value.Accessor;

/**
 * Implements a variety of operators for comparing numbers.
 * 
 * @author inc0g-repoz
 */
public class OperatorComparator extends Operator
{

    private final BiFunction<Number, Number, Boolean> lambda;

    public OperatorComparator(String name, BiFunction<Number, Number, Boolean> lambda)
    {
        super(name, OperatorType.BINARY);
        this.lambda = lambda;
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands)
    {
        // Can't use this type of operator on more operands
        if (operands.length != type.getOperandCount())
        {
            throw new RuntimeException("Only can compare " + type.getOperandCount() + " numbers");
        }

        // Both values are accessed and considered numbers
        Number fst = (Number) operands[0].linkedAccess(ctx, null);
        Number scd = (Number) operands[1].linkedAccess(ctx, null);

        // Comparing numbers with an implemented bi-function
        return lambda.apply(fst, scd);
    }

}
