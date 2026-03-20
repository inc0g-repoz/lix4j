package ctxfree.rule;

import ctxfree.ast.AST2;

public abstract class Rule
{

    private Rule parent, next, alternative;

    public boolean match(AST2.Node node)
    {
        boolean alts = matchAlternative(node) ? true
                     : alternative == null    ? false
                     : alternative.matchAlternative(node);
        return matchAlternative(node) ? true
             : alternative == null ? false
             : alternative.matchAlternative(node);
    }

    public Rule and(Rule rule)
    {
        next = rule;
        next.parent = parent;
        return next;
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

    abstract boolean matchAlternative(AST2.Node node);

}
