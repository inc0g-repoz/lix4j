package ctxfree.rule;

import ctxfree.lex.AST;

public abstract class Rule
{

    private Rule parent, next, alternative;

    public boolean match(AST.Node node)
    {
        return matchAlternative(node)
             ? (next == null ? true : next.match(node))
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

    protected void executeMappedAction() {} // action mapping

    abstract boolean matchAlternative(AST.Node node);

}
