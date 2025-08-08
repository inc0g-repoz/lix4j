package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.exception.SyntaxError;
import com.github.inc0grepoz.lix4j.unit.expression.ExpressionResolver;
import com.github.inc0grepoz.lix4j.util.ControlFlow;
import com.github.inc0grepoz.lix4j.util.PrimitiveTester;
import com.github.inc0grepoz.lix4j.value.Accessor;

public class UnitLoopDo extends UnitSection
{

    static UnitLoopDo compile(CompileTimeContext ctx, ASTNode node, UnitSection section)
    {
        node.getTokens().poll(); // do

        UnitLoopDo unit = new UnitLoopDo(section);
        ScriptCompiler.appendSectionUnits(ctx, node, unit);

        LinkedList<ASTNode> parentNodes = node.getParent().getChildNodes();
        if (!parentNodes.isEmpty() || !parentNodes.peek().getTokens().peek().equals("while"))
        {
            LinkedList<String> conditionTokens = parentNodes.poll().getTokens();
            if (!conditionTokens.poll().equals("while"))
            {
                throw new SyntaxError("\"while\" expected after a \"do\" block");
            }

            unit.condition = ExpressionResolver.resolve(ctx, section, conditionTokens);
        }

        return unit;
    }

    Accessor condition;

    UnitLoopDo(UnitSection parent)
    {
        super(parent);
    }

    @Override
    public String toString()
    {
        return "do " + super.toString() + " while (" + condition + ")";
    }

    @Override
    Object execute(ExecutionContext context)
    {
        do
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
        while (condition(context));

        return ControlFlow.KEEP_EXECUTING;
    }

    private boolean condition(ExecutionContext context)
    {
        return !PrimitiveTester.isDefaultValue(condition.linkedAccess(context, null));
    }

}
