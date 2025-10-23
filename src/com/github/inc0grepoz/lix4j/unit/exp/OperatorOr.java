package com.github.inc0grepoz.lix4j.unit.exp;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.util.PrimitiveTester;
import com.github.inc0grepoz.lix4j.value.Accessor;

/**
 * Implements a logical summation operator with left-to-right evaluation.
 * Supports chained execution.
 * <p>
 * Example: 
 * <blockquote><pre>
 *     a || b && c || !d
 * </pre></blockquote>
 * 
 * @author inc0g-repoz
 */
public class OperatorOr extends Operator
{

    public OperatorOr(String name)
    {
        super(name, OperatorType.BINARY);
    }

    @Override
    public Object evaluate(ExecutionContext ctx, Accessor[] operands)
    {
        // Looking for a "true" value
        for (int i = 0; i < operands.length; i++)
        {
            // Same as in C++, a value is considered false, if it's not true
            // when the type is boolean of course, but also if it's a default
            // value, e.g. "null" for objects or "0" for numbers
            if (!PrimitiveTester.isDefaultValue(operands[i].linkedAccess(ctx, null)))
            {
                return true; // the whole expression is true
            }
        }

        return false; // the whole expression is false
    }

}
