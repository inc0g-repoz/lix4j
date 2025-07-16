package com.github.inc0grepoz.lix4j.ctxfree;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ast.ASTNodeSection;
import com.github.inc0grepoz.lix4j.ast.ASTNodeTokens;

public class KeywordRule implements GrammarRule {

    private final String keyword;

    KeywordRule(String keyword)
    {
        this.keyword = keyword;
    }

    @Override
    public boolean matches(LinkedList<String> tokens) {
        return tokens.peek() != null && tokens.peek().equals(keyword);
    }

    @Override
    public ASTNode generateNode(ASTNodeSection parent, LinkedList<String> tokens) {
        tokens.poll(); // Consume keyword
        ASTNodeTokens node = new ASTNodeTokens(parent);
        node.getTokens().add(keyword);
        return node;
    }

}
