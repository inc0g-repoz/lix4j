package ctxfree.lex;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.lookup.id.LexedIdentifier;
import com.github.inc0grepoz.lix4j.util.Keyword;

/**
 * Generates abstract syntax trees from lexer output.
 * 
 * @author inc0g-repoz
 */
public class AST
{

    /**
     * Generates an abstract syntax tree from the received input
     * and returns it's root node.
     * 
     * @param input the lexer output
     * @return a root node of a generated abstract syntax tree
     */
    public static TokenGroup generate(LinkedList<Lexer.Token> input)
    {
        TokenGroup root = new TokenGroup(0, GroupType.ROOT);
        generateSection(root, input);
        return root;
    }

    // Recursively writes a section of an AST
    private static void generateSection(TokenGroup to, LinkedList<Lexer.Token> in)
    {
        char breaker;

        switch (to.getGroupType())
        {
        case CURLY: breaker = '}'; break;
        case PAREN: breaker = ')'; break;
        case SQUARE: breaker = ']'; break;
        default: breaker = '\0';
        }

        while (!in.isEmpty())
        {
            Lexer.Token tk = in.peek();

            if (tk.getTokenType() == Lexer.TokenType.SPECIAL_CHARACTER)
            {
                char ch = tk.getChar();

                if (ch == breaker)
                {
                    in.poll();
                    return;
                }

                if (("{([").indexOf(ch) != -1)
                {
                    in.poll();
                    GroupType type;

                    switch (ch)
                    {
                    case '{': type = GroupType.CURLY; break;
                    case '(': type = GroupType.PAREN; break;
                    case '[': type = GroupType.SQUARE; break;
                    default: continue;
                    }

                    TokenGroup group = new TokenGroup(tk.getLine(), type);
                    to.getChildren().add(group);
                    generateSection(group, in);
                    continue;
                }
            }

            to.getChildren().add(in.poll());
        }
    }

    /**
     * Represents a node in an abstract syntax tree.
     * May contain child nodes or a token.
     * 
     * @author inc0g-repoz
     */
    public static interface Node
    {

        boolean isGroup();

        int getLine();

        Object getLiteral();

        LexedIdentifier getLexedIdentifier();

        Keyword getKeyword();

        Number getNumber();

        String getString();

        boolean getBoolean();

        char getChar();

        /**
         * Returns the token type of this node, if it's a token.
         * 
         * @return a token type
         * @throws UnsupportedOperationException
         *         if this node is a token group
         */
        Lexer.TokenType getTokenType();

        /**
         * Returns the group type of this node, if it's a group.
         * 
         * @return a group type
         * @throws UnsupportedOperationException
         *         if this node is a token
         */
        GroupType getGroupType();

        LinkedList<AST.Node> getChildren();

    }

    /**
     * Represents a node of an abstract syntax tree
     * that contains other nodes.
     * 
     * @author inc0g-repoz
     */
    public static class TokenGroup implements Node
    {

        private final int line;
        private final GroupType type;
        private final LinkedList<Node> children = new LinkedList<>();

        TokenGroup(int line, GroupType type)
        {
            this.line = line;
            this.type = type;
        }

        @Override
        public boolean isGroup()
        {
            return true;
        }

        @Override
        public int getLine()
        {
            return line;
        }

        @Override
        public Object getLiteral()
        {
            throw new UnsupportedOperationException("Attempted to get a literal by a token group");
        }

        @Override
        public LexedIdentifier getLexedIdentifier()
        {
            throw new UnsupportedOperationException("Attempted to get an identifier by a token group");
        }

        @Override
        public Keyword getKeyword()
        {
            throw new UnsupportedOperationException("Attempted to get a keyword by a token group");
        }

        @Override
        public Number getNumber()
        {
            throw new UnsupportedOperationException("Attempted to get a number by a token group");
        }

        @Override
        public String getString()
        {
            throw new UnsupportedOperationException("Attempted to get a string by a token group");
        }

        @Override
        public boolean getBoolean()
        {
            throw new UnsupportedOperationException("Attempted to get a boolean by a token group");
        }

        @Override
        public char getChar()
        {
            throw new UnsupportedOperationException("Attempted to get a character by a token group");
        }

        @Override
        public Lexer.TokenType getTokenType()
        {
            throw new UnsupportedOperationException("Attempted to get a token type of a token group");
        }

        @Override
        public GroupType getGroupType()
        {
            return type;
        }

        @Override
        public LinkedList<AST.Node> getChildren()
        {
            return children;
        }

    }

    /**
     * Represents a type of an abstract syntax tree node.
     * 
     * @author inc0g-repoz
     */
    public static enum GroupType
    {

        /** The root of an abstract syntax tree. **/
        ROOT,

        /** Stores other nodes in curly brackets. **/
        CURLY,

        /** Stores other nodes in parentheses. **/
        PAREN,

        /** Stores other nodes in square brackets. **/
        SQUARE,
        ;

    }

    private AST()
    {
        throw new UnsupportedOperationException();
    }

}
