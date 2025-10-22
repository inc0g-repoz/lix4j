package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.exception.SyntaxError;
import com.github.inc0grepoz.lix4j.util.ControlFlow;

public class UnitFlowBreak extends Unit
{

    static UnitFlowBreak compile(CompileTimeContext ctx, ASTNode node, UnitSection parent)
    {
        LinkedList<String> tokens = node.getTokens();
        tokens.poll(); // break

        if (!tokens.isEmpty())
        {
            throw new SyntaxError("Illegal tokens after a break statement: " + String.join(" ", tokens));
        }

        return new UnitFlowBreak(parent, ctx);
    }

    UnitFlowBreak(UnitSection parent, CompileTimeContext ctx)
    {
        super(parent, ctx);
    }

    @Override
    public String toString()
    {
        return "break";
    }

    @Override
    Object execute(ExecutionContext context)
    {
        return ControlFlow.BREAK;
    }

}
