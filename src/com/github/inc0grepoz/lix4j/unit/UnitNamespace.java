package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ast.NodeBreakerType;
import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.ctx.Namespace;
import com.github.inc0grepoz.lix4j.exception.SyntaxError;

public class UnitNamespace extends Unit
{

    static UnitNamespace compile(CompileTimeContext ctx, ASTNode node, UnitSection parent)
    {
        LinkedList<String> tokens = node.getTokens();
        tokens.poll(); // namespace

        if (tokens.size() != 1)
        {
            throw new SyntaxError("Namespaces cannot contain special symbols");
        }

        Namespace current = ctx.getNamespace();
        Namespace namespace = current.nest(tokens.poll());

        if (node.getNodeBreakerType() == NodeBreakerType.STATEMENT)
        {
            if (!parent.isRoot())
            {
                throw new SyntaxError("Cannot use namespaces inside a section");
            }

            if (!current.isGlobal())
            {
                throw new SyntaxError("Cannot use non-block namespace \"" + namespace + "\" inside another namespace");
            }

            ctx.setNamespace(namespace);
        }
        else
        {
            ctx.setNamespace(namespace);
            ScriptCompiler.compileSection(ctx, node.getParent().getChildNodes().poll(), parent);
            ctx.setNamespace(current);
        }

        return null;
    }

    private UnitNamespace(UnitSection parent)
    {
        super(null, null);

        throw new UnsupportedOperationException();
    }

}
