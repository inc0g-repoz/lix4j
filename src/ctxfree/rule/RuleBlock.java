package ctxfree.rule;

import ctxfree.ast.AST;

class RuleBlock extends Rule
{

    @Override
    public Rule clone()
    {
        return new RuleBlock();
    }

    @Override
    boolean matchImpl(AST.Node node)
    {
        return node.getType() == AST.NodeType.CURLY;
    }

}
