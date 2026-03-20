package ctxfree.rule;

import com.github.inc0grepoz.lix4j.util.Keyword;

import ctxfree.ast.AST2;
import ctxfree.lex.Lexer2;

public class RuleTokenKeyword extends RuleToken
{

    private final Keyword keyword;

    RuleTokenKeyword(Keyword keyword)
    {
        this.keyword = keyword;
    }

    @Override
    boolean matchToken(AST2.Node token)
    {
        return token.getTokenType() == Lexer2.TokenType.KEYWORD
            && token.getKeyword() == keyword;
    }

}
