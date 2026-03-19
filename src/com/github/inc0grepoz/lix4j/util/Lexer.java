package com.github.inc0grepoz.lix4j.util;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.github.inc0grepoz.lix4j.exception.SyntaxError;

/**
 * A lexical analyzer that converts character input streams into tokens.
 * Handles comments, strings, numbers, and special characters while respecting
 * escape sequences and quote boundaries.
 * <p>
 * The lexer processes input character by character, grouping them into tokens
 * based on special character boundaries and maintaining context for strings
 * and comments.
 * 
 * @author inc0g-repoz
 */
public class Lexer
{

    private static final String CHAR_DIGIT = ".0123456789";
    private static final String CHAR_BRACES = "()[]{}";
    private static final String CHAR_SPECIAL = CHAR_BRACES + "\r\t\n ~!@#$%^&*-=+:;'\"\\|/<>,.?";
    private static final String CHAR_NEVER_TRAIL = CHAR_BRACES + "!;.";
    private static final Pattern PATTERN_NUMBER = Pattern.compile("(\\d*\\.)?(\\d+)");

    /**
     * Reads tokens from a character input stream, processing the entire input.
     * Handles comments (both single-line and multi-line), strings with escape sequences,
     * numbers, and special characters while maintaining proper token boundaries.
     * <p>
     * Example:
     * <blockquote><pre>
     * String input = "var x = 42; // This is a comment\n" +
     *                "str = \"Hello\\nWorld\"; /* multi-line * /";
     * Reader reader = new StringReader(input);
     * LinkedList<String> tokens = Lexer.readTokens(reader);
     * // tokens: ["var", "x", "=", "42", ";", "str", "=", "\"Hello\\nWorld\"", ";"]
     * </pre></blockquote>
     *
     * @param in the character input stream to read from
     * @return a linked list of tokens extracted from the input
     * @throws IOException if an I/O error occurs while reading from the input stream
     * @throws SyntaxError if unterminated quotes or multi-line comments are detected
     */
    public static LinkedList<Token> readTokens(Reader in) throws IOException
    {
        StringBuilder builder = new StringBuilder();
        LinkedList<Token> tokens = new LinkedList<>();

        int line = 1;
        int i;                       // raw character
        char ch, chp = '\0';         // current and previous characters
        char q = '\0';               // quotes used: '\0', '\'', '"'
        boolean lastSpecial = false; // last token was a special character
        boolean escapeNext = false;  // '\' tokens for strings

        /*
         * 0 - not commenting
         * 1 - single-line
         * 2 - multi-line
         */
        byte cmt = 0;

        // Reading the whole input
        while ((i = in.read()) != -1)
        {
            ch = (char) i;

            if (ch == '\r')
            {
                continue; // carriage return
            }

            // Comment end
            if (cmt != 0)
            {
                if (cmt == 2 && ch == '/' && chp == '*'
                        || cmt == 1 && ch == '\n')
                {
                    cmt = 0;
                    chp = '\0';
                }
                else
                {
                    chp = ch;
                }

                continue;
            }

            // Writing non-special characters
            if (CHAR_SPECIAL.indexOf(ch) == -1)
            {
                if (lastSpecial)
                {
                    flushBuffer(builder, tokens, line);
                }

                escapeNext = false;
                lastSpecial = false;
                builder.append(chp = ch);

                continue;
            }

            // Escaping the next character, if necessary
            if (ch == '\\' && q != '\0')
            {
                escapeNext = !escapeNext;
                builder.append(chp = ch);
                continue;
            }

            // Quotes
            if (ch == '\'' || ch == '"')
            {
                if (escapeNext)
                {
                    escapeNext = false;
                    builder.append(ch);
                    continue;
                }
                else if (q == '\0')
                {
                    q = ch;
                    lastSpecial = false; // not resolved along with special symbols

                    // Writing the last token before opening
                    flushBuffer(builder, tokens, line);
                }
                else if (q == ch)
                {
                    q = '\0';
                }

                // String tokens are enclosed in quotes
                builder.append(chp = ch);
                continue;
            }

            // Whitespaces
            if (Character.isWhitespace(ch))
            {
                if (ch == '\n')
                {
                    line++;
                }
                if (q == '\0') // no quotes
                {
                    flushBuffer(builder, tokens, line);
                }
                else
                {
                    builder.append(chp = ch);
                }
                continue;
            }

            // Comment start
            if (ch == '*' && chp == '/' && q == '\0')
            {
                cmt = 2;
                chp = '\0';
                builder.setLength(0);
                continue;
            }

            // Comment start
            if (ch == '/' && chp == '/' && q == '\0')
            {
                cmt = 1;
                builder.setLength(0);
                continue;
            }

            if (q != '\0')
            {
                builder.append(chp = ch);
                continue;
            }

            // Numbers are whole tokens and literals are written with the
            // non-special characters
            if (CHAR_DIGIT.indexOf(ch) != -1)
            {
                if (CHAR_DIGIT.indexOf(chp) != -1);
                {
                    // Distinguishing dots between variables and method calls
                    if (PATTERN_NUMBER.matcher(builder.toString()).matches())
                    {
                        builder.append(chp = ch);
                        continue;
                    }
                }
            }

            // Some special symbols may trail
            if (!lastSpecial
                    || CHAR_NEVER_TRAIL.indexOf(ch) != -1
                    || CHAR_BRACES.indexOf(chp) != -1)
            {
                flushBuffer(builder, tokens, line);
            }

            lastSpecial = true;
            builder.append(chp = ch);
        }

        // Reading the last token
        flushBuffer(builder, tokens, line);

        // Validate all quotes and multi-line comments terminated
        if (q != '\0') throw new SyntaxError("Unterminated quote detected");
        if (cmt == 2)  throw new SyntaxError("Unterminated multi-line comment detected");

        return tokens;
    }

    /**
     * Flushes the current buffer content to the tokens list if the buffer is not empty.
     * Clears the buffer after adding its content to the tokens list.
     * <p>
     * Example:
     * <blockquote><pre>
     * StringBuilder builder = new StringBuilder("hello");
     * List<String> tokens = new ArrayList<>();
     * flushBuffer(builder, tokens);
     * // tokens now contains ["hello"], builder is empty
     * </pre></blockquote>
     *
     * @param builder the string builder containing accumulated characters
     * @param tokens the list to which the buffer content should be added
     * @param line the line where the token appears
     */
    private static void flushBuffer(StringBuilder builder, List<Token> tokens, int line)
    {
        if (builder.length() != 0)
        {
            tokens.add(new Token(line, builder.toString()));
            builder.setLength(0);
        }
    }

    /**
     * Represents an element of a lexed token. May be
     * a string, number, keyword or a special symbol.
     * 
     * @author inc0g-repoz
     */
    public static class Token
    {

        private final int line;
        private final String string;

        private Token(int line, String string)
        {
            this.line = line;
            this.string = string;
        }

        /**
         * Returns the line number of where this token
         * appeared in the parsed text.
         * 
         * @return the line number
         */
        public int getLine()
        {
            return line;
        }

        /**
         * Returns the {@code String} stored by this
         * token as it was in the parsed text.
         * 
         * @return the {@code String} representation
         */
        public String getString()
        {
            return string;
        }

    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always thrown when called
     */
    private Lexer()
    {
        throw new UnsupportedOperationException("Utility class");
    }

}
