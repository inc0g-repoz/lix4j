package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.exception.SyntaxError;

public class UnitNamespace extends Unit
{

    static UnitNamespace compile(CompileTimeContext ctx, ASTNode node, UnitSection parent)
    {
        LinkedList<String> tokens = node.getTokens();
        tokens.poll(); // namespace

        if (tokens.size() != 1)
        {
            throw new SyntaxError("Namespaces cannot contain whitespaces");
        }

        if (!parent.isRoot())
        {
            throw new SyntaxError("Cannot use namespaces inside a section");
        }

        String namespace = tokens.poll();
        ctx.setNamespace(namespace);

        return null;
    }

    private UnitNamespace(UnitSection parent)
    {
        super(null);

        throw new UnsupportedOperationException();
    }

}
