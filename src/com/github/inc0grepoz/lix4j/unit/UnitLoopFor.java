package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.exception.SyntaxError;
import com.github.inc0grepoz.lix4j.unit.expression.ExpressionResolver;
import com.github.inc0grepoz.lix4j.util.ControlFlow;
import com.github.inc0grepoz.lix4j.util.PrimitiveTester;
import com.github.inc0grepoz.lix4j.util.TokenHelper;
import com.github.inc0grepoz.lix4j.value.Accessor;

public class UnitLoopFor extends UnitSection
{

    static UnitLoopFor compile(CompileTimeContext ctx, ASTNode node, UnitSection section)
    {
        node.getTokens().poll(); // for

        LinkedList<String> tempTokens = TokenHelper.readEnclosedTokens(node.getTokens(), "(", ")");
        TokenHelper.openParentheses(tempTokens);
        LinkedList<LinkedList<String>> split = TokenHelper.splitTokens(tempTokens, ";");

        if (split.size() != 3)
        {
            throw new SyntaxError("Bad for-loop syntax");
        }

        Accessor parameter = (tempTokens = split.poll()).isEmpty() ? null
                : ExpressionResolver.resolve(ctx, section, tempTokens);
        Accessor condition = (tempTokens = split.poll()).isEmpty() ? null
                : ExpressionResolver.resolve(ctx, section, tempTokens);
        Accessor increment = (tempTokens = split.poll()).isEmpty() ? null
                : ExpressionResolver.resolve(ctx, section, tempTokens);

        UnitLoopFor unit = new UnitLoopFor(section, ctx, parameter, condition, increment);
        ScriptCompiler.appendSectionUnits(ctx, node, unit);

        return unit;
    }

    final Accessor parameter, condition, increment;

    UnitLoopFor(UnitSection parent, CompileTimeContext ctx,
            Accessor parameter, Accessor condition, Accessor increment)
    {
        super(parent, ctx);
        this.parameter = parameter;
        this.condition = condition;
        this.increment = increment;
    }

    @Override
    public String toString()
    {
        return "for (" + parameter + "; " + condition + "; " + increment + ") " + super.toString();
    }

    @Override
    Object execute(ExecutionContext context)
    {
        for (parameter(context); condition(context); increment(context))
        {
            Object rv = super.execute(context);

            if (rv == ControlFlow.BREAK)
            {
                break;
            }

            if (rv == ControlFlow.CONTINUE)
            {
                continue;
            }

            if (rv != ControlFlow.KEEP_EXECUTING)
            {
                return rv;
            }
        }

        return ControlFlow.KEEP_EXECUTING;
    }

    private void parameter(ExecutionContext context)
    {
        if (parameter != null)
        {
            parameter.linkedAccess(context, null);
        }
    }

    private boolean condition(ExecutionContext context)
    {
        return condition == null || !PrimitiveTester.isDefaultValue(condition.linkedAccess(context, null));
    }

    private void increment(ExecutionContext context)
    {
        if (increment != null)
        {
            increment.access(context, null);
        }
    }

}
