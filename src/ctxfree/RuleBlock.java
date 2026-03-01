package ctxfree;

import com.github.inc0grepoz.lix4j.ast.ASTNode;

class RuleBlock implements Rule
{

    @Override
    public boolean matches(ASTNode node)
    {
        return node.hasChildNodes();
    }

}
