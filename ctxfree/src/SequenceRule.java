package com.github.inc0grepoz.lix4j.ctxfree;

import java.util.LinkedList;
import java.util.Optional;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ast.ASTNodeSection;
import com.github.inc0grepoz.lix4j.exception.SyntaxError;

public class SequenceRule implements GrammarRule
{

    private final Grammar[] sequence;

    public SequenceRule(Grammar... sequence)
    {
        this.sequence = sequence;
    }

    @Override
    public boolean matches(LinkedList<String> tokens)
    {
        LinkedList<String> copy = new LinkedList<>(tokens);
        for (Grammar rule: sequence)
        {
            if (rule.parse(null, copy).isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public ASTNode generateNode(ASTNodeSection parent, LinkedList<String> tokens)
    {
        ASTNodeSection container = new ASTNodeSection(parent);
        for (Grammar rule: sequence)
        {
            Optional<ASTNode> node = rule.parse(container, tokens);
            if (node.isEmpty())
            {
                throw new SyntaxError("Expected sequence rule");
            }
        }
        return container;
    }

}
