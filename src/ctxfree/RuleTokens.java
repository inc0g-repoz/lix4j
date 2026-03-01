package ctxfree;

import java.util.List;

import com.github.inc0grepoz.lix4j.ast.ASTNode;

interface RuleTokens extends Rule {
    default boolean matches(ASTNode node) {
        if (!node.hasTokens()) return false;
        if (node.getTokens().isEmpty()) return false;
        return matchTokens(node.getTokens());
    }
    boolean matchTokens(List<String> tokens);
}