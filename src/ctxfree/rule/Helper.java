package ctxfree.rule;

import com.github.inc0grepoz.lix4j.util.Keyword;

import ctxfree.lex.Lexer;

public class Helper
{

    public static Rule literal()
    {
        return new RuleTokenLiteralAny();
    }

    public static Rule bool()
    {
        return new RuleTokenType(Lexer.TokenType.LITERAL_BOOLEAN);
    }

    public static Rule character()
    {
        return new RuleTokenType(Lexer.TokenType.LITERAL_CHAR);
    }

    public static Rule string()
    {
        return new RuleTokenType(Lexer.TokenType.LITERAL_STRING);
    }

    public static Rule number()
    {
        return new RuleTokenType(Lexer.TokenType.LITERAL_NUMBER);
    }

    public static Rule keyword(Keyword keyword)
    {
        return new RuleTokenKeyword(keyword);
    }

    private Helper()
    {
        throw new UnsupportedOperationException();
    }

}
