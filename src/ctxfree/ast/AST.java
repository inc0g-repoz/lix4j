package ctxfree.ast;

import java.util.LinkedList;
import java.util.stream.Collectors;

import com.github.inc0grepoz.lix4j.util.AnsiColor;

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
    public static ASTNodeSection generate(LinkedList<String> input)
    {
        ASTNodeSection root = new ASTNodeSection(null, ASTNodeType.ROOT);
        generateSection(root, input);
        return root;
    }

    // Recursively writes a section of an AST
    private static void generateSection(ASTNodeSection to, LinkedList<String> in)
    {
        ASTNodeTokens tokens = null;
        String breaker;

        switch (to.getType())
        {
        case CURLY: breaker = "}"; break;
        case PAREN: breaker = ")"; break;
        case SQUARE: breaker = "]"; break;
        default: breaker = null;
        }

        while (!in.isEmpty())
        {
            String token = in.peek();

            if (token.equals(breaker))
            {
                in.poll();
                return;
            }

            if (token.equals(";"))
            {
                tokens = null;
                in.poll();
                continue;
            }

            if (token.equals("{") || token.equals("(") || token.equals("["))
            {
                tokens = null;
                in.poll();

                ASTNodeType type;
                switch (token) {
                    case "{": type = ASTNodeType.CURLY; break;
                    case "(": type = ASTNodeType.PAREN; break;
                    case "[": type = ASTNodeType.SQUARE; break;
                    default: continue;
                }

                ASTNodeSection section = new ASTNodeSection(to, type);
                to.getChildren().add(section);
                generateSection(section, in);
                continue;
            }

            if (tokens == null)
            {
                tokens = new ASTNodeTokens(to);
                to.getChildren().add(tokens);
            }

            tokens.getTokens().add(in.poll());
        }
    }

    /**
     * Represents a type of an abstract syntax tree node.
     * 
     * @author inc0g-repoz
     */
    public static enum ASTNodeType
    {

        /** The root of an abstract syntax tree. **/
        ROOT,

        /** Stores other nodes in curly brackets. **/
        CURLY,

        /** Stores other nodes in parentheses. **/
        PAREN,

        /** Stores other nodes in square brackets. **/
        SQUARE,

        /** Stores tokens. **/
        TOKENS;

    }

    /**
     * Represents a node in an abstract syntax tree.
     * May contain child nodes or tokens.
     * 
     * @author inc0g-repoz
     */
    public static abstract class Node
    {

        protected final ASTNodeSection parent;
        protected final ASTNodeType type;

        Node(ASTNodeSection parent, ASTNodeType type)
        {
            this.parent = parent;
            this.type = type;
        }

        /**
         * Returns the parent {@code ASTNodeSection},
         * if exists, or {@code null} otherwise.
         * 
         * @return the parent node section
         */
        public ASTNodeSection getParent()
        {
            return parent;
        }

        /**
         * Returns the type of this abstract syntax tree node.
         * 
         * @return a {@code NodeType} value
         */
        public ASTNodeType getType()
        {
            return type;
        }

        /**
         * Returns {@code true}, if the node implementation
         * can store child nodes, or {@code false}, if it's
         * meant for tokens.
         * 
         * @return whether the node implementation can store
         *         child nodes
         */
        public abstract boolean isSection();

        /**
         * Returns the linked list of child nodes, if
         * the node implementation stores them.
         * 
         * @return a linked list of child nodes
         * @throws UnsupportedOperationException
         *         if the node implementation can only
         *         store tokens
         */
        public LinkedList<Node> getChildren()
        {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns the linked list of stored tokens, if
         * the node implementation stores them.
         * 
         * @return a linked list of tokens
         * @throws UnsupportedOperationException
         *         if the node implementation can only
         *         store child nodes
         */
        public LinkedList<String> getTokens()
        {
            throw new UnsupportedOperationException();
        }

        // Used for printing the AST
        int indentation()
        {
            Node node = parent;
            int i = -1;

            for (; node != null; i++)
            {
                node = node.parent;
            }

            return i;
        }

    }

    /**
     * Represents a node of an abstract syntax tree
     * that contains tokens.
     * 
     * @author inc0g-repoz
     */
    public static class ASTNodeTokens extends Node
    {

        private final LinkedList<String> tokens = new LinkedList<>();

        ASTNodeTokens(ASTNodeSection parent)
        {
            super(parent, ASTNodeType.TOKENS);
        }

        @Override
        public LinkedList<String> getTokens()
        {
            return tokens;
        }

        @Override
        public boolean isSection()
        {
            return false;
        }

        @Override
        public String toString()
        {
            String indentation = new String(new char[indentation()]).replace("\0", "    ");
            return indentation + tokens.stream().collect(Collectors.joining(" "));
        }

    }

    /**
     * Represents a node of an abstract syntax tree
     * that contains other nodes.
     * 
     * @author inc0g-repoz
     */
    public static class ASTNodeSection extends Node
    {

        private final LinkedList<Node> children = new LinkedList<>();
        private final AnsiColor color = AnsiColor.randomDarkBright();

        ASTNodeSection(ASTNodeSection parent, ASTNodeType type)
        {
            super(parent, type);
        }

        @Override
        public LinkedList<Node> getChildren()
        {
            return children;
        }

        @Override
        public boolean isSection()
        {
            return true;
        }

        @Override
        public String toString()
        {
            String childs = this.children.stream().map(Node::toString).collect(Collectors.joining("\n"));
            return color + childs + (parent == null ? AnsiColor.RESET : parent.color);
        }

    }

    private AST()
    {
        throw new UnsupportedOperationException();
    }

}
