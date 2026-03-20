package ctxfree.rule;

import ctxfree.ast.AST2;
import ctxfree.lex.Lexer2;

public class RuleTokenLiteral extends RuleToken
{

    private final Lexer2.TokenType type;

    RuleTokenLiteral(Lexer2.TokenType type)
    {
        this.type = type;
    }

    @Override
    boolean matchToken(AST2.Node token)
    {
        return type == token.getTokenType();
    }

}
