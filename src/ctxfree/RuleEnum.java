package ctxfree;

import com.github.inc0grepoz.lix4j.ast.ASTNode;

enum RuleEnum
{

    LIST_ALL_THE_RULES_HERE(null),
    ;

    private final Rule rule;

    RuleEnum(Rule rule)
    {
        this.rule = rule;
    }

    public Rule getRule()
    {
        return rule;
    }

    public boolean isMatching(ASTNode node)
    {
        return false;
    }

}
