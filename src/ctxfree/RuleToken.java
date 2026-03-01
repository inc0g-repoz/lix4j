package ctxfree;

import com.github.inc0grepoz.lix4j.ast.ASTNode;

interface RuleToken extends Rule {
    default boolean matches(ASTNode node) {
        if (!node.hasTokens()) return false;
        if (node.getTokens().size() != 1) return false;
        return matchToken(node.getTokens().peek());
    }
    boolean matchToken(String token);
}