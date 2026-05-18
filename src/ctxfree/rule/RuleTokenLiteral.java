package ctxfree.rule;

import ctxfree.lex.AST;
import ctxfree.lex.Lexer;

public class RuleTokenLiteral extends RuleToken
{

    private final Lexer.TokenType type;

    RuleTokenLiteral(Lexer.TokenType type)
    {
        this.type = type;
    }

    @Override
    boolean matchToken(AST.Node token)
    {
        return type == token.getTokenType();
    }

}
