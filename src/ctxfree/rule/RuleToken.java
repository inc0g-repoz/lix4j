package ctxfree.rule;

import ctxfree.ast.AST2;

public abstract class RuleToken extends Rule
{

    @Override
    boolean matchAlternative(AST2.Node node)
    {
        return node.isGroup() ? false : matchToken(node);
    }

    abstract boolean matchToken(AST2.Node token);

}
