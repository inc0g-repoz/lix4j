package ctxfree;

import com.github.inc0grepoz.lix4j.ast.ASTNode;

interface Rule {
    boolean matches(ASTNode node);
}