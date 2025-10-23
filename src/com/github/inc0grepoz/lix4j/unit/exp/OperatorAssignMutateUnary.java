package com.github.inc0grepoz.lix4j.unit.exp;

import java.util.function.Function;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.value.Accessor;

/**
 * Implements a variety of unary operators for mutating assignment.
 * 
 * @author inc0g-repoz
 */
public class OperatorAssignMutateUnary extends Operator
{

    private final Function<Number, Number> lambda;

    public OperatorAssignMutateUnary(String name, OperatorType type, Function<Number, Number> lambda)
    {
        super(name, type);
        this.lambda = lambda;
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands)
    {
        // As most of the unary operators may be used from the left or
        // the right side of an operand, there's two different ways of
        // handling them. The most straightforward way for left-handed
        // unary operators is to mutate a value and then return it.
        if (type == OperatorType.UNARY_LEFT)
        {
            return operands[0].mutate(ctx, null, cv -> lambda.apply((Number) cv));
        }
        // The value here has to be returned first, and then it can be
        // mutated
        else
        {
            // A reference can't be applied within a lambda expression,
            // so a pointer should be used
            Object[] rv_ptr = { null };

            // The old value is saved beforehand
            operands[0].mutate(ctx, null, (rv) -> {
                return lambda.apply((Number) (rv_ptr[0] = rv));
            });

            return rv_ptr[0]; // and returned after
        }
    }

}
