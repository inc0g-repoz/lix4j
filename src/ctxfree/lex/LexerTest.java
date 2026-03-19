package ctxfree.lex;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.ListIterator;

import com.github.inc0grepoz.lix4j.exception.SyntaxError;
import com.github.inc0grepoz.lix4j.id.Identifier;
import com.github.inc0grepoz.lix4j.id.Namespace;
import com.github.inc0grepoz.lix4j.util.Keyword;
import com.github.inc0grepoz.lix4j.util.TokenHelper;

public class LexerTest
{

    private static final String NUMERIC_SUFFIXES = "dflDFL";

    public static LinkedList<LTToken> lex(Reader in)
    throws IOException
    {
        StringBuilder buffer = new StringBuilder();
        LinkedList<LTToken> out = new LinkedList<>();
        int nsd = 0; // namespace parts delimiter
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
                out.add(new LTToken(line, LTTokenType.LITERAL_STRING, unescaped));
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

                out.add(new LTToken(line, LTTokenType.LITERAL_CHAR, unescaped.charAt(0)));
                continue;
            }

            // Number literal (starts with digit or dot)
            if (isDigit(chs[0]) || (chs[0] == '.' && isDigit(chs[1])))
            {
                number(in, chs, buffer);
                expectIdentifier(nsd, buffer);

                Number n = parseNumber(buffer.toString());
                out.add(new LTToken(line, LTTokenType.LITERAL_NUMBER, n));
                continue;
            }

            // Hexadecimal number (0x...)
            if (chs[0] == '0' && (chs[1] == 'x' || chs[1] == 'X'))
            {
                numberHex(in, chs, buffer);
                expectIdentifier(nsd, buffer);

                int hex = Integer.parseInt(buffer.toString(), 16);
                out.add(new LTToken(line, LTTokenType.LITERAL_NUMBER, hex));
                continue;
            }

            // Special characters (operators, punctuation)
            if (isSpecialChar(chs[0]))
            {
                out.add(new LTToken(line, LTTokenType.SPECIAL_CHARACTER, (char) chs[0]));
                if (chs[0] == ':')
                {
                    nsd++; // for namespaced identifiers
                    if (nsd > 2) // maximum of 2
                    {
                        throw new SyntaxError("Unexpected namespace or identifier: " + chs[0]);
                    }
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
                out.add(new LTToken(line, LTTokenType.KEYWORD, kw));
            }
            else
            {
                namespacedIdentifier(in, chs, word, out, nsd == 2, line);
                nsd = 0;
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
        buffer.append((char) chs[0]);
        buffer.append((char) chs[1]);
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
        String word, LinkedList<LTToken> out, boolean delimiter, int[] line)
    throws IOException
    {
        if (!delimiter)
        {
            out.add(new LTToken(line, LTTokenType.IDENTIFIER, new LTIdentifier(word)));
            return;
        }

        ListIterator<LTToken> iter = out.listIterator(out.size());
        iter.previous();
        iter.remove();
        iter.previous();
        iter.remove();

        if (iter.hasPrevious())
        {
            LTToken token = iter.previous();
            if (token.getType() == LTTokenType.IDENTIFIER)
            {
                LTIdentifier id = token.getIdentifier();
                id.add(word);
                return;
            }
        }

        LTIdentifier ltid = new LTIdentifier(Namespace.ABSOLUTE_MARKER);
        ltid.add(word);
        out.add(new LTToken(line, LTTokenType.IDENTIFIER, ltid));
    }

    private static boolean isSpecialChar(int c)
    {
        return ( 33 <= c && c <=  47)  // ! " # $ % & ' ( ) * + , - . /
            || ( 58 <= c && c <=  64)  // : ; < = > ? @
            || ( 91 <= c && c <=  96 && c != 95)  // [ \ ] ^ _ ` (95 is an _)
            || (123 <= c && c <= 126); // { | } ~
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

    private static void expectIdentifier(int namespaceDelimiter, StringBuilder buffer)
    {
        if (namespaceDelimiter > 1)
        {
            throw new SyntaxError("Unexpected namespace or identifier: " + buffer.toString());
        }
    }

    public static enum LTTokenType
    {

        IDENTIFIER,
        KEYWORD,
        LITERAL_CHAR,
        LITERAL_NUMBER,
        LITERAL_STRING,
        SPECIAL_CHARACTER;

    }

    public static class LTToken
    {

        private final int line;
        private final LTTokenType type;
        private final Object item;

        LTToken(int[] line, LTTokenType type, Object item)
        {
            this.line = line[0];
            this.type = type;
            this.item = item;
        }

        @Override
        public String toString()
        {
            return item.toString();
        }

        public int getLine()
        {
            return line;
        }

        public LTTokenType getType()
        {
            return type;
        }

        public LTIdentifier getIdentifier()
        {
            return (LTIdentifier) item;
        }

        public Keyword getKeyword()
        {
            return (Keyword) item;
        }

        public Number getNumber()
        {
            return (Number) item;
        }

        public String getString()
        {
            return (String) item;
        }

        public char getChar()
        {
            return (char) item;
        }

    }

    public static class LTIdentifier
    {

        private final LinkedList<String> parts = new LinkedList<>();
        private String name;

        LTIdentifier(String name)
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

    private LexerTest()
    {
        throw new UnsupportedOperationException();
    }

}
