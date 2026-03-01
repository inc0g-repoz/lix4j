package com.github.inc0grepoz.lix4j.ctxfree;

import java.util.LinkedList;
import java.util.Optional;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ast.ASTNodeSection;
import com.github.inc0grepoz.lix4j.ast.NodeBreakerType;
import com.github.inc0grepoz.lix4j.exception.SyntaxError;

public class BlockRule implements GrammarRule
{

    private final Grammar[] innerRules;

    public BlockRule(Grammar... innerRules)
    {
        this.innerRules = innerRules;
    }

    @Override
    public boolean matches(LinkedList<String> tokens)
    {
        return tokens.peek() != null && tokens.peek().equals("{");
    }

    @Override
    public ASTNode generateNode(ASTNodeSection parent, LinkedList<String> tokens)
    {
        tokens.poll(); // Consume "{"
        ASTNodeSection block = new ASTNodeSection(parent);
        block.setNodeBreakerType(NodeBreakerType.BLOCK);

        while (!tokens.isEmpty() && !tokens.peek().equals("}"))
        {
            boolean matched = false;
            for (Grammar rule: innerRules)
            {
                Optional<ASTNode> node = rule.parse(block, tokens);
                if (node.isPresent())
                {
                    matched = true;
                    break;
                }
            }
            if (!matched)
            {
                throw new SyntaxError("Invalid syntax inside block");
            }
        }

        if (tokens.isEmpty())
        {
            throw new SyntaxError("Unterminated block");
        }
        tokens.poll(); // Consume "}"
        return block;
    }

}
