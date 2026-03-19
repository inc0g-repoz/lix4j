package ctxfree.ast;

import java.util.LinkedList;
import java.util.stream.Collectors;

import com.github.inc0grepoz.lix4j.util.AnsiColor;
import com.github.inc0grepoz.lix4j.util.Lexer.Token;

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
    public static NodeSection generate(LinkedList<Token> input)
    {
        NodeSection root = new NodeSection(null, NodeType.ROOT);
        generateSection(root, input);
        return root;
    }

    // Recursively writes a section of an AST
    private static void generateSection(NodeSection to, LinkedList<Token> in)
    {
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
            Token tk = in.peek();
            String token = tk.getString();

            if (token.equals(breaker))
            {
                in.poll();
                return;
            }

            if (token.equals("{") || token.equals("(") || token.equals("["))
            {
                in.poll();

                NodeType type;
                switch (token) {
                    case "{": type = NodeType.CURLY; break;
                    case "(": type = NodeType.PAREN; break;
                    case "[": type = NodeType.SQUARE; break;
                    default: continue;
                }

                NodeSection section = new NodeSection(to, type);
                to.getChildren().add(section);
                generateSection(section, in);
                continue;
            }

            // For any other token (including ';') create a separate token node
            NodeToken tokenNode = new NodeToken(to, in.poll());
            to.getChildren().add(tokenNode);
        }
    }

    /**
     * Represents a type of an abstract syntax tree node.
     * 
     * @author inc0g-repoz
     */
    public static enum NodeType
    {

        /** The root of an abstract syntax tree. **/
        ROOT,

        /** Stores other nodes in curly brackets. **/
        CURLY,

        /** Stores other nodes in parentheses. **/
        PAREN,

        /** Stores other nodes in square brackets. **/
        SQUARE,

        /** Represents a single token. **/
        TOKEN;

    }

    /**
     * Represents a node in an abstract syntax tree.
     * May contain child nodes or a token.
     * 
     * @author inc0g-repoz
     */
    public static abstract class Node
    {

        protected final NodeSection parent;
        protected final NodeType type;

        Node(NodeSection parent, NodeType type)
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
        public NodeSection getParent()
        {
            return parent;
        }

        /**
         * Returns the type of this abstract syntax tree node.
         * 
         * @return a {@code NodeType} value
         */
        public NodeType getType()
        {
            return type;
        }

        /**
         * Returns {@code true}, if the node implementation
         * can store child nodes, or {@code false}, if it's
         * meant for a token.
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
         *         store a token
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
        public Token getToken()
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
     * that contains a single token.
     * 
     * @author inc0g-repoz
     */
    public static class NodeToken extends Node
    {

        private final Token token;

        NodeToken(NodeSection parent, Token token)
        {
            super(parent, NodeType.TOKEN);
            this.token = token;
        }

        @Override
        public Token getToken()
        {
            return token;
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
            return indentation + token.getString();
        }

    }

    /**
     * Represents a node of an abstract syntax tree
     * that contains other nodes.
     * 
     * @author inc0g-repoz
     */
    public static class NodeSection extends Node
    {

        private final LinkedList<Node> children = new LinkedList<>();
        private final AnsiColor color = AnsiColor.randomDarkBright();

        NodeSection(NodeSection parent, NodeType type)
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
