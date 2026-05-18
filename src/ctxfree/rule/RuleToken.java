package ctxfree.rule;

import ctxfree.lex.AST;

public abstract class RuleToken extends Rule
{

    @Override
    boolean matchAlternative(AST.Node node)
    {
        return node.isGroup() ? false : matchToken(node);
    }

    abstract boolean matchToken(AST.Node token);

}
