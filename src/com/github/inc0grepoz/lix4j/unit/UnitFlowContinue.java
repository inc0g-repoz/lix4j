package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.exception.SyntaxError;
import com.github.inc0grepoz.lix4j.util.ControlFlow;

public class UnitFlowContinue extends Unit
{

    static UnitFlowContinue compile(CompileTimeContext ctx, ASTNode node, UnitSection parent)
    {
        LinkedList<String> tokens = node.getTokens();
        tokens.poll(); // continue

        if (!tokens.isEmpty())
        {
            throw new SyntaxError("Illegal tokens after a continue statement: " + String.join(" ", tokens));
        }

        return new UnitFlowContinue(parent, ctx);
    }

    UnitFlowContinue(UnitSection parent, CompileTimeContext ctx)
    {
        super(parent, ctx);
    }

    @Override
    public String toString()
    {
        return "continue";
    }

    @Override
    Object execute(ExecutionContext context)
    {
        return ControlFlow.CONTINUE;
    }

}
