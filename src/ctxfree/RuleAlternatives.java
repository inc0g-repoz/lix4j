package ctxfree;

import java.util.List;

import com.github.inc0grepoz.lix4j.ast.ASTNode;

class RuleAlternatives implements Rule
{

    private final List<Rule> alternatives;

    RuleAlternatives(List<Rule> alternatives)
    {
        this.alternatives = alternatives;
    }

    @Override
    public boolean matches(ASTNode node)
    {
        return alternatives.stream().anyMatch(rule -> rule.matches(node));
    }

}
