package ctxfree.rule;

import ctxfree.lex.AST;

public class RuleSetConjunctive extends RuleSet
{

    RuleSetConjunctive(Rule rule)
    {
        super(rule);
    }

    @Override
    public Rule and(Rule rule)
    {
        return alt = new RuleSetConjunctive(rule);
    }

    @Override
    public boolean test(AST.Node node)
    {
        return alt == null
             ? rule.test(node)
             : rule.test(node) && alt.test(node);
    }

}
