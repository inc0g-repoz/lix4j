package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ast.ASTNode;

public class UnitConditionElse extends UnitSection
{

    static UnitConditionElse compile(CompileTimeContext ctx, ASTNode node, UnitSection section)
    {
        LinkedList<String> tokens = node.getTokens();
        tokens.poll(); // else

        UnitConditionElse unit = new UnitConditionElse(section);

        if (tokens.isEmpty())
        {
            ScriptCompiler.appendSectionUnits(ctx, node, unit);
        }
        else
        {
            ScriptCompiler.compileUnit_r(ctx, node, unit);
        }

        return unit;
    }

    UnitConditionElse(UnitSection parent)
    {
        super(parent, false);
    }

    @Override
    public String toString()
    {
        return "else " + super.toString();
    }

}
