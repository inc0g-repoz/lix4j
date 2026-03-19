package ctxfree.rule;

import ctxfree.ast.AST.Node;


public class RuleTokenChar extends Rule
{

    @Override
    public Rule clone()
    {
        return new RuleTokenChar();
    }

    @Override
    boolean matchImpl(Node node)
    {
        String token = node.getToken().getString();
        return token.charAt(0) == '\'' && token.charAt(token.length() - 1) == '\'';
    }

}
