package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.unit.expression.ExpressionResolver;
import com.github.inc0grepoz.lix4j.util.FlowControl;
import com.github.inc0grepoz.lix4j.util.PrimitiveTester;
import com.github.inc0grepoz.lix4j.util.TokenHelper;
import com.github.inc0grepoz.lix4j.value.Accessor;

public class UnitConditionIf extends UnitSection
{

    static UnitConditionIf compile(CompileTimeContext ctx, ASTNode node, UnitSection section)
    {
        node.getTokens().poll(); // if

        LinkedList<String> conditionTokens = TokenHelper.readEnclosedTokens(node.getTokens(), "(", ")");
        Accessor condition = ExpressionResolver.resolve(ctx, section, conditionTokens);

        UnitConditionIf unit = new UnitConditionIf(section, condition);
        ScriptCompiler.appendSectionUnits(ctx, node, unit);

        LinkedList<ASTNode> parentNodes = node.getParent().getChildNodes();
        if (!parentNodes.isEmpty())
        {
            if (parentNodes.peek().getTokens().peek().equals("else"))
            {
                unit.otherwise = UnitConditionElse.compile(ctx, parentNodes.poll(), section);
            }
        }

        return unit;
    }

    final Accessor condition;

    UnitConditionElse otherwise;

    UnitConditionIf(UnitSection parent, Accessor condition)
    {
        super(parent);
        this.condition = condition;
    }

    @Override
    public String toString()
    {
        return "if (" + condition + ") " + super.toString() + (otherwise == null ? "" : " " + otherwise);
    }

    @Override
    Object execute(ExecutionContext context)
    {
        return PrimitiveTester.isDefaultValue(condition.linkedAccess(context, null))
                ? (otherwise == null ? FlowControl.KEEP_EXECUTING : otherwise.execute(context))
                : super.execute(context);
    }

}
