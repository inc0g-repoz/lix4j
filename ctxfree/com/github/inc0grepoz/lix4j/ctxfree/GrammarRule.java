package com.github.inc0grepoz.lix4j.ctxfree;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ast.ASTNodeSection;

public interface GrammarRule
{

    /** Checks if the rule matches the given tokens. */
    boolean matches(LinkedList<String> tokens);

    /** Generates an AST node if the rule matches. */
    ASTNode generateNode(ASTNodeSection parent, LinkedList<String> tokens);

}
