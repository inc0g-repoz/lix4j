package com.github.inc0grepoz.lix4j.unit;

import java.io.IOException;
import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.exception.SyntaxError;
import com.github.inc0grepoz.lix4j.unit.expression.ExpressionResolver;
import com.github.inc0grepoz.lix4j.value.Accessor;

public class UnitInclude extends Unit
{

    static UnitInclude compile(CompileTimeContext ctx, ASTNode node, UnitSection parent)
    {
        LinkedList<String> tokens = node.getTokens();
        tokens.poll(); // include

        if (tokens.size() != 1)
        {
            throw new SyntaxError("Include statements should be followed by valid filepaths");
        }

        Accessor fpa = ExpressionResolver.resolve(ctx, parent, tokens);
        String filepath = (String) fpa.linkedAccess(null, null);

        try
        {
            ctx.getScript().include(filepath);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not include " + filepath);
        }

        return null;
    }

    private UnitInclude(UnitSection parent)
    {
        super(null);

        throw new UnsupportedOperationException();
    }

}
