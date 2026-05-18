package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.exception.SyntaxError;

import ctxfree.TokenHelper;
import ctxfree.lex.AST;

public class UnitConditionElse extends UnitSection
{

    static UnitConditionElse compile(CompileTimeContext ctx, AST.Node group, UnitSection section)
    {
        LinkedList<AST.Node> children = group.getChildren();
        children.remove(); // else

        UnitConditionElse unit = new UnitConditionElse(section, ctx);

        AST.Node after = children.peek();
        if (after.isGroup())
        {
            if (after.getGroupType() == AST.GroupType.CURLY)
            {
                LinkedList<AST.Node> elseCurly = after.getChildren();
                // resolve...
            }
            else
            {
                SyntaxError.unexpectedTokenGroupType(after);
            }
        }
        else
        {
            LinkedList<AST.Node> elseOneLiner = TokenHelper.readUntilLineBreaker(children);
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
