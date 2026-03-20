package ctxfree.rule;

import ctxfree.ast.AST2;

public class RuleTokenLiteralAny extends RuleToken
{

    @Override
    boolean matchToken(AST2.Node token)
    {
        return token.getTokenType().isLiteral();
    }

}
