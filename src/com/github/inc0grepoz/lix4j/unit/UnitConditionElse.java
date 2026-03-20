package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.exception.SyntaxError;

import ctxfree.TokenHelper;
import ctxfree.ast.AST2;

public class UnitConditionElse extends UnitSection
{

    static UnitConditionElse compile(CompileTimeContext ctx, AST2.Node group, UnitSection section)
    {
        LinkedList<AST2.Node> children = group.getChildren();
        children.remove(); // else

        UnitConditionElse unit = new UnitConditionElse(section, ctx);

        AST2.Node after = children.peek();
        if (after.isGroup())
        {
            if (after.getGroupType() == AST2.GroupType.CURLY)
            {
                LinkedList<AST2.Node> elseCurly = after.getChildren();
                // resolve...
            }
            else
            {
                SyntaxError.unexpectedTokenGroupType(after);
            }
        }
        else
        {
            LinkedList<AST2.Node> elseOneLiner = TokenHelper.readUntilLineBreaker(children);
            // resolve
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
