package ctxfree.rule;

import ctxfree.ast.AST;

abstract class RuleToken extends Rule
{

    @Override
    boolean matchImpl(AST.Node node)
    {
        if (node.getType() != AST.NodeType.TOKEN) return false;
        return matchToken(node.getToken().getString());
    }

    abstract boolean matchToken(String token);

}
