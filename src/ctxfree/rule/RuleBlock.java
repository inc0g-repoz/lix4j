package ctxfree.rule;

import ctxfree.lex.AST;

class RuleBlock extends Rule
{

    @Override
    public Rule clone()
    {
        return new RuleBlock();
    }

    @Override
    boolean matchAlternative(AST.Node node)
    {
        return node.isGroup() && node.getGroupType() == AST.GroupType.CURLY;
    }

}
