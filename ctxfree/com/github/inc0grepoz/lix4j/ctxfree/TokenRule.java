package com.github.inc0grepoz.lix4j.ctxfree;

import java.util.LinkedList;
import java.util.function.Predicate;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ast.ASTNodeSection;
import com.github.inc0grepoz.lix4j.ast.ASTNodeTokens;

public class TokenRule implements GrammarRule
{

    private final Predicate<String> matcher;

    TokenRule(Predicate<String> matcher)
    {
        this.matcher = matcher;
    }

    @Override
    public boolean matches(LinkedList<String> tokens)
    {
        return tokens.peek() != null && matcher.test(tokens.peek());
    }

    @Override
    public ASTNode generateNode(ASTNodeSection parent, LinkedList<String> tokens)
    {
        String token = tokens.poll();
        ASTNodeTokens node = new ASTNodeTokens(parent);
        node.getTokens().add(token);
        return node;
    }

}
