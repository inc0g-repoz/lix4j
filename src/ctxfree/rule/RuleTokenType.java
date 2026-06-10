package ctxfree.rule;

import ctxfree.lex.AST;
import ctxfree.lex.Lexer;

public class RuleTokenType implements Rule
{

    private final Lexer.TokenType type;

    RuleTokenType(Lexer.TokenType type)
    {
        this.type = type;
    }

    @Override
    public boolean test(AST.Node token)
    {
        return type == token.getTokenType();
    }

}
