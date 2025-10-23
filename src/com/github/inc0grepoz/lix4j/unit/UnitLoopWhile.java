package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.unit.exp.ExpressionResolver;
import com.github.inc0grepoz.lix4j.util.ControlFlow;
import com.github.inc0grepoz.lix4j.util.PrimitiveTester;
import com.github.inc0grepoz.lix4j.util.TokenHelper;
import com.github.inc0grepoz.lix4j.value.Accessor;

public class UnitLoopWhile extends UnitSection
{

    static UnitLoopWhile compile(CompileTimeContext ctx, ASTNode node, UnitSection section)
    {
        node.getTokens().poll(); // while

        LinkedList<String> conditionTokens = TokenHelper.readEnclosedTokens(node.getTokens(), "(", ")");
        Accessor condition = ExpressionResolver.resolve(ctx, section, conditionTokens);

        UnitLoopWhile unit = new UnitLoopWhile(section, ctx, condition);
        ScriptCompiler.appendSectionUnits(ctx, node, unit);

        return unit;
    }

    final Accessor condition;

    UnitLoopWhile(UnitSection parent, CompileTimeContext ctx, Accessor condition)
    {
        super(parent, ctx);
        this.condition = condition;
    }

    @Override
    public String toString()
    {
        return "while (" + condition + ") " + super.toString();
    }

    @Override
    Object execute(ExecutionContext context)
    {
        while (condition(context))
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

    private boolean condition(ExecutionContext context)
    {
        return !PrimitiveTester.isDefaultValue(condition.linkedAccess(context, null));
    }

}
