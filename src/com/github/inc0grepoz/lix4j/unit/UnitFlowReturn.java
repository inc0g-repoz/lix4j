package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.unit.expression.ExpressionResolver;
import com.github.inc0grepoz.lix4j.value.Accessor;

public class UnitFlowReturn extends Unit
{

    static UnitFlowReturn compile(CompileTimeContext ctx, ASTNode node, UnitSection parent)
    {
        LinkedList<String> tokens = node.getTokens();
        tokens.poll(); // return

        Accessor rva = tokens.isEmpty()
                ? Accessor.VOID
                : ExpressionResolver.resolve(ctx, parent, tokens);

        return new UnitFlowReturn(parent, rva);
    }

    private final Accessor accessor;

    UnitFlowReturn(UnitSection parent, Accessor accessor)
    {
        super(parent);
        this.accessor = accessor;
    }

    @Override
    public String toString()
    {
        return "return " + accessor;
    }

    @Override
    Object execute(ExecutionContext context)
    {
        return accessor.linkedAccess(context, null);
    }

}
