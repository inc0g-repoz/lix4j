package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.unit.exp.ExpressionResolver;
import com.github.inc0grepoz.lix4j.util.TokenHelper;
import com.github.inc0grepoz.lix4j.value.Accessor;

public class UnitErrorCatch extends UnitSection
{

    static UnitErrorCatch compile(CompileTimeContext ctx, ASTNode node, UnitSection section)
    {
        LinkedList<String> tokens = node.getTokens();
        tokens.poll(); // catch

        LinkedList<String> errorTokens = TokenHelper.readEnclosedTokens(node.getTokens(), "(", ")");
        Accessor error = ExpressionResolver.resolve(ctx, section, errorTokens);

        UnitErrorCatch unit = new UnitErrorCatch(section, ctx, error);

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

    final Accessor error;

    UnitErrorCatch(UnitSection parent, CompileTimeContext ctx, Accessor error)
    {
        super(parent, ctx, false);
        this.error = error;
    }

    @Override
    public String toString()
    {
        return "catch (" + error + ") " + super.toString();
    }

    public Accessor getError()
    {
        return error;
    }

}
