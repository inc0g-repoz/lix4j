package ctxfree.rule;

import com.github.inc0grepoz.lix4j.util.Keyword;

import ctxfree.lex.AST;
import ctxfree.lex.Lexer;

public class RuleTokenKeyword implements Rule
{

    private final Keyword keyword;

    RuleTokenKeyword(Keyword keyword)
    {
        this.keyword = keyword;
    }

    @Override
    public boolean test(AST.Node token)
    {
        return token.getTokenType() == Lexer.TokenType.KEYWORD
            && token.getKeyword() == keyword;
    }

}
