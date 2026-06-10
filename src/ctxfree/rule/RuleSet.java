package ctxfree.rule;

public abstract class RuleSet implements Rule
{

    protected Rule rule, alt;

    RuleSet(Rule rule)
    {
        this.rule = rule;
    }

}
