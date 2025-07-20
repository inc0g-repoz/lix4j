package com.github.inc0grepoz.lix4j.unit.expression;

import java.util.function.BiFunction;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.value.Accessor;

/**
 * Implements a variety of mutating assignment operators.
 * 
 * @author inc0g-repoz
 */
public class OperatorAssignMutate extends Operator
{

    private final BiFunction<Number, Number, Number> lambda;

    public OperatorAssignMutate(String name, BiFunction<Number, Number, Number> lambda)
    {
        super(name, OperatorType.BINARY);
        this.lambda = lambda;
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands)
    {
        if (operands.length != type.getOperandCount())
        {
            throw new RuntimeException("Only can use \"" + name + "\" on " + type.getOperandCount() + " numbers");
        }

        // The right-hand part of the expression is evaluated
        Number rv = (Number) operands[1].linkedAccess(ctx, null);

        // Right-hand is applied to left-hand as done in the regular assignment implementation
        return operands[0].mutate(ctx, null, cv -> lambda.apply((Number) cv, rv));
    }

}
