package ctxfree.rule;

import ctxfree.lex.AST;
import ctxfree.lex.AST.Node;

class RuleBlock implements Rule
{

    @Override
    public boolean test(Node node)
    {
        return node.isGroup() && node.getGroupType() == AST.GroupType.CURLY;
    }

}
