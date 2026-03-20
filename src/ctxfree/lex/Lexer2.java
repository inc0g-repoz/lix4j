package ctxfree.lex;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.ListIterator;

import com.github.inc0grepoz.lix4j.exception.SyntaxError;
import com.github.inc0grepoz.lix4j.id.Identifier;
import com.github.inc0grepoz.lix4j.id.Namespace;
import com.github.inc0grepoz.lix4j.util.Keyword;
import com.github.inc0grepoz.lix4j.util.PrimitiveConverter;
import com.github.inc0grepoz.lix4j.util.TokenHelper;

import ctxfree.ast.AST2;

public class Lexer2
{

    private static final String NUMERIC_SUFFIXES = "dflDFL";

    public static LinkedList<Token> lex(Reader in)
    throws IOException
    {
        StringBuilder buffer = new StringBuilder();
        LinkedList<Token> out = new LinkedList<>();
        boolean nsd = false; // namespace parts delimiter
        int[] line = { 1 };
        int[] chs = new int[2]; // chs[0] = current, chs[1] = lookahead
        chs[1] = in.read(); // initial read

        while (true)
        {
            // Shift: current becomes previous lookahead, read new lookahead
            read(in, chs);

            if (chs[0] == -1) break; // EOF
            if (isWhitespace(chs[0])) continue; // skip whitespace
            if (chs[0] == '\n') {
                line[0]++;
                continue;
            }

            // Handle comments
            if (chs[0] == '/' && chs[1] == '/')
            {
                commentSingleLine(in, chs);
                continue;
            }
            else if (chs[0] == '/' && chs[1] == '*')
            {
                commentMultiLine(in, chs, line);
                continue;
            }

            buffer.setLength(0); // cleared in every condition below anyway

            // String literal
            if (chs[0] == '"')
            {
                string(in, chs, buffer);
                expectIdentifier(nsd, buffer);

                String unescaped = TokenHelper.unescape(buffer.toString());
                out.add(new Token(line, TokenType.LITERAL_STRING, unescaped));
                continue;
            }

            // Character literal
            if (chs[0] == '\'')
            {
                character(in, chs, buffer);
                expectIdentifier(nsd, buffer);

                // After unescape, the string must be exactly one character
                String unescaped = TokenHelper.unescape(buffer.toString());
                if (unescaped.length() != 1)
                {
                    throw new SyntaxError("Invalid character literal");
                }

                out.add(new Token(line, TokenType.LITERAL_CHAR, unescaped.charAt(0)));
                continue;
            }

            // Number literal (starts with digit or dot)
            if (isDigit(chs[0]) || (chs[0] == '.' && isDigit(chs[1])))
            {
                number(in, chs, buffer);
                expectIdentifier(nsd, buffer);

                Number n = parseNumber(buffer.toString());
                out.add(new Token(line, TokenType.LITERAL_NUMBER, PrimitiveConverter.narrow(n)));
                continue;
            }

            // Hexadecimal number (0x...)
            if (chs[0] == '0' && (chs[1] == 'x' || chs[1] == 'X'))
            {
                numberHex(in, chs, buffer);
                expectIdentifier(nsd, buffer);

                int n = Integer.parseInt(buffer.toString(), 16);
                out.add(new Token(line, TokenType.LITERAL_NUMBER, PrimitiveConverter.narrow(n)));
                continue;
            }

            // Special characters (operators, punctuation)
            if (isSpecialChar(chs[0]))
            {
                buffer.append(chs[0]);
                expectIdentifier(nsd, buffer);

                if (isSpecialChar(chs[1]) && !isBracket(chs[0])) // two-character composite
                {
                    out.add(new Token(line, TokenType.SPECIAL_COMPOSITE,
                            new String(new char[] { (char) chs[0], (char) chs[1] })));

                    if (chs[0] == ':' && chs[1] == ':')
                    {
                        nsd = true; // for namespaced identifiers
                    }

                    read(in, chs); // consume the second colon
                }
                else
                {
                    out.add(new Token(line, TokenType.SPECIAL_CHARACTER, (char) chs[0]));
                }
                continue;
            }

            // Fallback to identifier or keyword
            otherToken(in, chs, buffer);
            String word = buffer.toString();
            Keyword kw = Keyword.fromString(word);

            if (kw != null)
            {
                expectIdentifier(nsd, buffer);

                if (kw == Keyword.NULL)
                {
                    out.add(new Token(line, TokenType.LITERAL_NULL, null));
                }
                else if (kw == Keyword.TRUE)
                {
                    out.add(new Token(line, TokenType.LITERAL_BOOLEAN, true));
                }
                else if (kw == Keyword.FALSE)
                {
                    out.add(new Token(line, TokenType.LITERAL_BOOLEAN, false));
                }
                else
                {
                    out.add(new Token(line, TokenType.KEYWORD, kw));
                }
            }
            else
            {
                namespacedIdentifier(in, chs, word, out, nsd, line);
                nsd = false;
            }
        }

        return out;
    }

    private static void read(Reader in, int[] chs)
    throws IOException
    {
        chs[0] = chs[1];
        chs[1] = in.read();
    }

    private static void commentSingleLine(Reader in, int[] chs)
    throws IOException
    {
        // consume the two slashes already seen: chs[0]='/', chs[1]='/'
        // read until newline or EOF
        while (true)
        {
            read(in, chs);
            if (chs[1] == -1 || chs[1] == '\n') break;
        }
    }

    private static void commentMultiLine(Reader in, int[] chs, int[] line)
    throws IOException
    {
        // chs[0]='/', chs[1]='*'
        read(in, chs); // consume the '*'
        while (true)
        {
            if (chs[0] == '\n') line[0]++;
            if (chs[0] == -1) throw new SyntaxError("Unterminated multi-line comment");
            if (chs[0] == '*' && chs[1] == '/')
            {
                // found closing */
                read(in, chs); // consume the '/' and next lookahead
                break;
            }
            read(in, chs);
        }
    }

    private static void string(Reader in, int[] chs, StringBuilder buffer)
    throws IOException
    {
        // chs[0] = '"' (opening quote)
        while (true)
        {
            read(in, chs);
            if (chs[0] == -1) throw new SyntaxError("Unterminated string");
            if (chs[0] == '"') break; // closing quote found
            buffer.append((char) chs[0]);
            if (chs[0] == '\\')
            {
                read(in, chs);
                if (chs[0] == -1) throw new SyntaxError("Unterminated string escape");
                buffer.append((char) chs[0]);
            }
        }
    }

    private static void character(Reader in, int[] chs, StringBuilder buffer)
    throws IOException
    {
        // chs[0] = '\'' (opening quote)
        // read characters until closing quote, handling escapes
        while (true)
        {
            read(in, chs);
            if (chs[0] == -1) throw new SyntaxError("Unterminated character literal");
            if (chs[0] == '\'' && (buffer.length() == 0 || buffer.charAt(buffer.length() - 1) != '\\'))
            {
                // closing quote found
                read(in, chs); // consume the quote and set lookahead
                break;
            }
            buffer.append((char) chs[0]);
        }
    }

    private static void number(Reader in, int[] chs, StringBuilder buffer)
    throws IOException
    {
        boolean dotSeen = (chs[0] == '.');
        boolean exponentSeen = false;
        buffer.append((char) chs[0]);

        while (true)
        {
            int next = chs[1];
            if (next == -1) break;
            char nc = (char) next;
            if (isDigit(nc))
            {
                read(in, chs); // digit, consume it
                buffer.append(nc);
            }
            else if (nc == '.' && !dotSeen && !exponentSeen)
            {
                dotSeen = true;
                read(in, chs);
                buffer.append(nc);
            }
            else if ((nc == 'e' || nc == 'E') && !exponentSeen)
            {
                exponentSeen = true;
                read(in, chs);
                buffer.append(nc);
                // optional sign after exponent
                if (chs[1] == '+' || chs[1] == '-')
                {
                    read(in, chs);
                    buffer.append((char) chs[0]);
                }
            }
            else if (NUMERIC_SUFFIXES.indexOf(nc) != -1 && !dotSeen && !exponentSeen)
            {
                read(in, chs); // integer suffix
                buffer.append(nc); // suffixes are usually single character; we stop here
                break;
            }
            else break; // not a part of number, stop
        }
    }

    private static void numberHex(Reader in, int[] chs, StringBuilder buffer)
    throws IOException
    {
        // chs[0] = '0', chs[1] = 'x' or 'X'
        read(in, chs); // move past 'x'

        // read hexadecimal digits
        while (true)
        {
            int next = chs[1];
            if (next == -1) break;
            char nc = (char) next;
            if (isDigit(nc) || (nc >= 'a' && nc <= 'f') || (nc >= 'A' && nc <= 'F'))
            {
                read(in, chs);
                buffer.append(nc);
            }
            else break;
        }
    }

    private static void otherToken(Reader in, int[] chs, StringBuilder buffer)
    throws IOException
    {
        buffer.append((char) chs[0]);
        while (true)
        {
            if (chs[1] == -1) break;
            char nc = (char) chs[1];
            if (!isSpecialChar(nc) && !isWhitespace(nc))
            {
                read(in, chs);
                buffer.append(nc);
            }
            else break;
        }
    }

    private static void namespacedIdentifier(Reader in, int[] chs,
        String word, LinkedList<Token> out, boolean delimiter, int[] line)
    throws IOException
    {
        if (!delimiter)
        {
            out.add(new Token(line, TokenType.IDENTIFIER, new LexedIdentifier(word)));
            return;
        }

        ListIterator<Token> iter = out.listIterator(out.size());
        iter.previous();
        iter.remove();

        if (iter.hasPrevious())
        {
            Token token = iter.previous();
            if (token.getTokenType() == TokenType.IDENTIFIER)
            {
                LexedIdentifier id = token.getLexedIdentifier();
                id.add(word);
                return;
            }
        }

        LexedIdentifier ltid = new LexedIdentifier(Namespace.ABSOLUTE_MARKER);
        ltid.add(word);
        out.add(new Token(line, TokenType.IDENTIFIER, ltid));
    }

    private static boolean isSpecialChar(int c)
    {
        return ( 33 <= c && c <=  47)  // ! " # $ % & ' ( ) * + , - . /
            || ( 58 <= c && c <=  64)  // : ; < = > ? @
            || ( 91 <= c && c <=  96 && c != 95)  // [ \ ] ^ _ ` (95 is an _)
            || (123 <= c && c <= 126); // { | } ~
    }

    private static boolean isBracket(int c)
    {
        return c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}';
    }

    private static boolean isDigit(int c)
    {
        return 48 <= c && c <= 57; // 0..9
    }

    private static boolean isWhitespace(int c)
    {
        return c == ' '   // space
            || c == '\t'  // tab
            || c == '\n'  // line breaker
            || c == '\r'  // carriage return
            || c == '\f'; // form feed
    }

    private static Number parseNumber(String word)
    {
        switch (word.charAt(word.length() - 1))
        {
        case 'd':
        case 'D':
            return Double.parseDouble(word);
        case 'f':
        case 'F':
            return Float.parseFloat(word);
        case 'l':
        case 'L':
            return Long.parseLong(word);
        }
        return word.indexOf('.') != -1
             ? Double.parseDouble(word)
             : Long.parseLong(word);
    }

    private static void expectIdentifier(boolean nsd, StringBuilder buffer)
    {
        if (nsd)
        {
            throw new SyntaxError("Unexpected namespace or identifier: " + buffer.toString());
        }
    }

    public static enum TokenType
    {

        /** Unresolved namespaced identifier. */
        IDENTIFIER,
        /** Reserved keywords, except literals. */
        KEYWORD,
        /** Boolean literals: true or false. **/
        LITERAL_BOOLEAN,
        /** Character in quotes */
        LITERAL_CHAR,
        /** Explicit null-pointer. */
        LITERAL_NULL,
        /** Any number: 1, 0x1, 1d, 1e+1, etc. */
        LITERAL_NUMBER,
        /** String in quotes. */
        LITERAL_STRING,
        /** Special single-character token. */
        SPECIAL_CHARACTER,
        /** Special composite token. */
        SPECIAL_COMPOSITE,
        ;

        public boolean isLiteral()
        {
            switch (this)
            {
            case LITERAL_BOOLEAN:
            case LITERAL_CHAR:
            case LITERAL_NULL:
            case LITERAL_NUMBER:
            case LITERAL_STRING:
                return true;
            default:
                return false;
            }
        }

        @Deprecated
        public boolean isSpecial()
        {
            switch (this)
            {
            case SPECIAL_CHARACTER:
            case SPECIAL_COMPOSITE:
                return true;
            default:
                return false;
            }
        }

    }

    /**
     * Represents a leaf-node of an abstract syntax tree
     * that contains a single token.
     * 
     * @author inc0g-repoz
     */
    public static class Token implements AST2.Node
    {

        private final int line;
        private final TokenType type;
        private final Object item;

        Token(int[] line, TokenType type, Object item)
        {
            this.line = line[0];
            this.type = type;
            this.item = item;
        }

        @Override
        public boolean isGroup()
        {
            return false;
        }

        @Override
        public int getLine()
        {
            return line;
        }

        @Override
        public Object getLiteral()
        {
            if (!type.isLiteral())
            {
                throw new UnsupportedOperationException("Not a literal token");
            }
            return item;
        }

        @Override
        public LexedIdentifier getLexedIdentifier()
        {
            return (LexedIdentifier) item;
        }

        @Override
        public Keyword getKeyword()
        {
            return (Keyword) item;
        }

        @Override
        public Number getNumber()
        {
            return (Number) item;
        }

        @Override
        public String getString()
        {
            return (String) item;
        }

        @Override
        public boolean getBoolean()
        {
            return (boolean) item;
        }

        @Override
        public char getChar()
        {
            return (char) item;
        }

        @Override
        public TokenType getTokenType()
        {
            return type;
        }

        @Override
        public AST2.GroupType getGroupType()
        {
            throw new UnsupportedOperationException("Attempted to get a group type of a single token");
        }

        @Override
        public LinkedList<AST2.Node> getChildren()
        {
            throw new UnsupportedOperationException("Attempted to get children of a single token");
        }

        @Override
        public String toString()
        {
            return item == null ? type.name() : item.toString();
        }

    }

    public static class LexedIdentifier
    {

        private final LinkedList<String> parts = new LinkedList<>();
        private String name;

        LexedIdentifier(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return parts.isEmpty() ? name : String.join("::", parts) + "::" + name;
        }

        void add(String name)
        {
            parts.add(this.name);
            this.name = name;
        }

        Identifier toUnresolvedIdentifier(Namespace namespace, int data)
        {
            return Identifier.unresolved(parts, namespace, name, data);
        }

        Identifier toUnresolvedIdentifier(Namespace namespace)
        {
            return Identifier.unresolved(parts, namespace, name);
        }

    }

    private Lexer2()
    {
        throw new UnsupportedOperationException();
    }

}
