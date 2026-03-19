package com.github.inc0grepoz.lix4j.ast;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.exception.SyntaxError;
import com.github.inc0grepoz.lix4j.util.Lexer.Token;

/**
 * Represents an abstract syntax tree read from lexer output.
 * 
 * @author inc0g-repoz
 */
public class AST extends ASTNodeSection
{

    /**
     * Returns a new {@code AST} generated from the specified input.
     * 
     * @param input the lexer output
     * @return a new generated {@code AST}
     */
    public static AST generateTree(LinkedList<Token> input)
    {
        AST root = new AST();
        ASTNodeSection curSection = root;
        ASTNodeTokens curTokens = null;

        Token nextToken;
        String nextTokenString;
        int braces = 0;

        while (!input.isEmpty())
        {
            nextToken = input.poll();
            nextTokenString = nextToken.getString();

            switch (nextTokenString)
            {
            case "(":
                if (curTokens == null)
                {
                    curTokens = new ASTNodeTokens(curSection);
                }
                curTokens.getTokens().add(nextTokenString);
                braces++;
                break;
            case ")":
                if (curTokens == null)
                {
                    curTokens = new ASTNodeTokens(curSection);
                }
                curTokens.getTokens().add(nextTokenString);
                braces--;
                break;
            case "{":
                if (curTokens != null)
                {
                    curTokens.setNodeBreakerType(NodeBreakerType.BLOCK);
                }
                curSection = new ASTNodeSection(curSection);
                curTokens = null; // entering a new section
                break;
            case "}":
                curSection = curSection.getParent();
                curTokens = null; // exiting a section
                break;
            case ";":
                if (braces == 0)
                {
                    curTokens = null; // ending a statement
                }
                else
                {
                    if (curTokens == null)
                    {
                        curTokens = new ASTNodeTokens(curSection);
                    }
                    curTokens.getTokens().add(nextTokenString);
                }
                break;
            default:
                if (curTokens == null)
                {
                    curTokens = new ASTNodeTokens(curSection);
                }
                curTokens.getTokens().add(nextTokenString);
            }
        }

        if (root != curSection)
        {
            throw new SyntaxError("Unterminated curly braces");
        }

        if (braces != 0)
        {
            throw new SyntaxError("Unterminated parentheses");
        }

        return root;
    }

    // Creates an empty AST
    private AST()
    {
        super(null);
    }

}
