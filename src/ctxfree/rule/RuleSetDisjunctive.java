package ctxfree.rule;

import ctxfree.lex.AST;

public class RuleSetDisjunctive extends RuleSet
{

    RuleSetDisjunctive(Rule rule)
    {
        super(rule);
    }

    @Override
    public Rule or(Rule rule)
    {
        return alt = new RuleSetDisjunctive(rule);
    }

    @Override
    public boolean test(AST.Node node)
    {
        return alt == null
             ? rule.test(node)
             : rule.test(node) || alt.test(node);
    }

}
