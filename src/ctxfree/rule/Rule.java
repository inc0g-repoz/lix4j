package ctxfree.rule;

import ctxfree.ast.AST;

public abstract class Rule implements Cloneable
{

    private Rule parent, alternative;

    public boolean match(AST.Node node)
    {
        return matchImpl(node) ? true
             : alternative == null ? false
             : alternative.matchImpl(node);
    }

    public Rule or(Rule rule)
    {
        alternative = rule;
        alternative.parent = parent;
        return alternative;
    }

    public Rule then()
    {
        return parent;
    }

    @Override
    public abstract Rule clone();

    abstract boolean matchImpl(AST.Node node);

}
