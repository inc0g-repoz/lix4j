package com.github.inc0grepoz.lix4j.unit;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.unit.expression.ExpressionResolver;
import com.github.inc0grepoz.lix4j.util.ControlFlow;
import com.github.inc0grepoz.lix4j.value.Accessor;

public class UnitOperation extends Unit
{

    static UnitOperation compile(CompileTimeContext ctx, ASTNode node, UnitSection parent)
    {
        return new UnitOperation(parent, ExpressionResolver.resolve(ctx, parent, node.getTokens()));
    }

    private Accessor accessor;

    UnitOperation(UnitSection parent, Accessor accessor) {
        super(parent);
        this.accessor = accessor;
    }

    @Override
    public String toString()
    {
        return accessor.toString();
    }

    @Override
    Object execute(ExecutionContext context)
    {
        accessor.linkedAccess(context, null);
        return ControlFlow.KEEP_EXECUTING;
    }

}
