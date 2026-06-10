package ctxfree.rule;

import java.util.function.Consumer;

import ctxfree.lex.AST;

public interface Rule
{

    boolean test(AST.Node node);

    default Rule and(Rule rule)
    {
        return (new RuleSetConjunctive(this)).and(rule);
    }

    default Rule or(Rule rule)
    {
        return (new RuleSetDisjunctive(this)).or(rule);
    }

    default Rule edit(Consumer<Rule> lambda)
    {
        lambda.accept(this);
        return this;
    }

}
