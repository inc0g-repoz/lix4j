package ctxfree.rule;

import ctxfree.lex.AST;

public class RuleTokenLiteralAny extends RuleToken
{

    @Override
    boolean matchToken(AST.Node token)
    {
        return token.getTokenType().isLiteral();
    }

    @Override
    void executeMappedAction() {
        
    }

}
