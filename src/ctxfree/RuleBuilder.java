package ctxfree;

class RuleBuilder {

    public RuleBuilder block()
    {
        return this;
    }

    public RuleBuilder identifier()
    {
        return this;
    }

    public RuleBuilder inParentheses(char left, char right)
    {
        return this;
    }

    public RuleBuilder inParentheses(char left, char right, char sep)
    {
        return this;
    }

    public RuleBuilder keyword(Keyword kw)
    {
        return this;
    }

    public RuleBuilder rule(Rule rule)
    {
        return this;
    }

    public RuleBuilder separator(char ch)
    {
        return this;
    }

    public RuleBuilder separatedBy(char ch)
    {
        return this;
    }

    public RuleBuilder varop()
    {
        return this;
    }

    public RuleBuilder varopDeclare()
    {
        return this;
    }

    public RuleBuilder value()
    {
        return this;
    }

    public RuleBuilder then()
    {
        return null;
    }

    public Rule build()
    {
        return null;
    }

}
