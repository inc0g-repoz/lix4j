package com.github.inc0grepoz.lix4j.ctxfree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ast.ASTNodeSection;

public final class Grammar
{

    private final List<GrammarRule> rules;

    private Grammar(List<GrammarRule> rules)
    {
        this.rules = rules;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public Optional<ASTNode> parse(ASTNodeSection parent, LinkedList<String> tokens)
    {
        for (GrammarRule rule: rules)
        {
            if (rule.matches(tokens))
            {
                return Optional.of(rule.generateNode(parent, tokens));
            }
        }
        return Optional.empty();
    }

    public static final class Builder
    {

        private final List<GrammarRule> rules = new ArrayList<>();

        public Builder keyword(String keyword)
        {
            rules.add(new KeywordRule(keyword));
            return this;
        }

        public Builder token(Predicate<String> matcher)
        {
            rules.add(new TokenRule(matcher));
            return this;
        }

        public Builder block(Grammar... innerRules)
        {
            rules.add(new BlockRule(innerRules));
            return this;
        }

        public Builder sequence(Grammar... sequence)
        {
            rules.add(new SequenceRule(sequence));
            return this;
        }

        public Grammar build()
        {
            return new Grammar(rules);
        }

    }

}
