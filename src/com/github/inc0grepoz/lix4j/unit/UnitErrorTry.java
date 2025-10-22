package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.util.ControlFlow;

public class UnitErrorTry extends UnitSection
{

    static UnitErrorTry compile(CompileTimeContext ctx, ASTNode node, UnitSection section)
    {
        node.getTokens().poll(); // try

        UnitErrorTry unit = new UnitErrorTry(section, ctx);
        ScriptCompiler.appendSectionUnits(ctx, node, unit);

        LinkedList<ASTNode> parentNodes = node.getParent().getChildNodes();
        if (!parentNodes.isEmpty())
        {
            if (parentNodes.peek().getTokens().peek().equals("catch"))
            {
                unit.otherwise = UnitErrorCatch.compile(ctx, parentNodes.poll(), section);
            }
        }

        return unit;
    }

    UnitErrorCatch otherwise;

    UnitErrorTry(UnitSection parent, CompileTimeContext ctx)
    {
        super(parent, ctx);
    }

    @Override
    public String toString()
    {
        return "try " + super.toString() + (otherwise == null ? "" : " " + otherwise);
    }

    @Override
    Object execute(ExecutionContext context)
    {
        try
        {
            return super.execute(context);
        }
        catch (Throwable t)
        {
            if (otherwise == null)
            {
                return ControlFlow.KEEP_EXECUTING;
            }

            otherwise.getError().mutate(context, null, t);
            return otherwise.execute(context);
        }
    }

}
