package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;

public class UnitConditionElse extends UnitSection
{

    static UnitConditionElse compile(CompileTimeContext ctx, ASTNode node, UnitSection section)
    {
        LinkedList<String> tokens = node.getTokens();
        tokens.poll(); // else

        UnitConditionElse unit = new UnitConditionElse(section, ctx);

        if (tokens.isEmpty())
        {
            ScriptCompiler.appendSectionUnits(ctx, node, unit);
        }
        else
        {
            ScriptCompiler.compileUnit(ctx, node, unit);
        }

        return unit;
    }

    UnitConditionElse(UnitSection parent, CompileTimeContext ctx)
    {
        super(parent, ctx, false);
    }

    @Override
    public String toString()
    {
        return "else " + super.toString();
    }

}
