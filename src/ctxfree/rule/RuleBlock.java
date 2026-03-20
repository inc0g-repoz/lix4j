package ctxfree.rule;

import ctxfree.ast.AST2;

class RuleBlock extends Rule
{

    @Override
    public Rule clone()
    {
        return new RuleBlock();
    }

    @Override
    boolean matchAlternative(AST2.Node node)
    {
        return node.isGroup() && node.getGroupType() == AST2.GroupType.CURLY;
    }

}
