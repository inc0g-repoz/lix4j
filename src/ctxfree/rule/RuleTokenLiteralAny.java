package ctxfree.rule;

import ctxfree.lex.AST;

public class RuleTokenLiteralAny implements Rule
{

    @Override
    public boolean test(AST.Node token)
    {
        return token.getTokenType().isLiteral();
    }

}
