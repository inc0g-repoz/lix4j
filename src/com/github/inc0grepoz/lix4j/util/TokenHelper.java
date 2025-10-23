package com.github.inc0grepoz.lix4j.util;

import java.util.LinkedList;
import java.util.ListIterator;

import com.github.inc0grepoz.lix4j.exception.SyntaxError;

/**
 * A utility class providing methods for token manipulation and processing.
 * Includes functionality for reading enclosed tokens, splitting token sequences,
 * and handling escape sequences in strings.
 * 
 * @author inc0g-repoz
 */
public class TokenHelper
{

    /**
     * Reads tokens enclosed between specified prefix and suffix delimiters.
     * Processes tokens from the beginning of the list and returns the enclosed content.
     * <p>
     * Example:
     * <blockquote><pre>
     * LinkedList<String> tokens = new LinkedList<>(Arrays.asList("(", "a", "b", ")", "c"));
     * LinkedList<String> result = TokenHelper.readEnclosedTokens(tokens, "(", ")");
     * // result contains ["a", "b"], tokens becomes ["c"]
     * </pre></blockquote>
     *
     * @param tokens the linked list of tokens to process
     * @param prefix the opening delimiter (e.g., "(" or "[")
     * @param suffix the closing delimiter (e.g., ")" or "]")
     * @return a new linked list containing the tokens between the prefix and suffix
     * @throws SyntaxError
     *         if the first token doesn't match the prefix or if the sequence is unterminated
     */
    public static LinkedList<String> readEnclosedTokens(LinkedList<String> tokens,
            String prefix, String suffix)
    {
        LinkedList<String> out = new LinkedList<>();
        String token = tokens.poll();
        int level = 1;

        if (!prefix.equals(token))
        {
            throw new SyntaxError("\"" + prefix + "\" expected, but \"" + token + "\" found");
        }

        while (true)
        {
            token = tokens.poll();

            if (token == null)
            {
                throw new SyntaxError("Unterminated sequence in " + prefix + suffix);
            }
            else if (prefix.equals(token))
            {
                level++;
            }
            else if (suffix.equals(token))
            {
                level--;
            }

            if (level == 0)
            {
                break;
            }

            out.add(token);
        }

        return out;
    }

    /**
     * Reads tokens enclosed between specified prefix and suffix delimiters from the end.
     * Processes tokens from the end of the list and returns the enclosed content.
     * <p>
     * Example:
     * <blockquote><pre>
     * LinkedList<String> tokens = new LinkedList<>(Arrays.asList("a", "(", "b", "c", ")"));
     * LinkedList<String> result = TokenHelper.readEnclosedTokensBackwards(tokens, "(", ")");
     * // result contains ["b", "c"], tokens becomes ["a"]
     * </pre></blockquote>
     *
     * @param tokens the linked list of tokens to process
     * @param prefix the opening delimiter (e.g., "(" or "[")
     * @param suffix the closing delimiter (e.g., ")" or "]")
     * @return a new linked list containing the tokens between the prefix and suffix
     * @throws SyntaxError
     *         if the last token doesn't match the suffix or if the sequence is unterminated
     */
    public static LinkedList<String> readEnclosedTokensBackwards(LinkedList<String> tokens,
            String prefix, String suffix)
    {
        LinkedList<String> out = new LinkedList<>();
        String token = tokens.pollLast();
        int level = 1;

        if (!suffix.equals(token))
        {
            throw new SyntaxError("\"" + suffix + "\" expected, but \"" + token + "\" found");
        }

        while (true)
        {
            token = tokens.pollLast();

            if (token == null)
            {
                throw new SyntaxError("Unterminated sequence in " + prefix + suffix);
            }
            else if (suffix.equals(token))
            {
                level++;
            }
            else if (prefix.equals(token))
            {
                level--;
            }

            if (level == 0)
            {
                break;
            }

            out.addFirst(token);
        }

        return out;
    }

    /**
     * Splits tokens by a separator while respecting nested parentheses and square brackets.
     * The splitting only occurs when brackets and parentheses are balanced at level 0.
     * <p>
     * Example:
     * <blockquote><pre>
     * LinkedList<String> tokens = new LinkedList<>(Arrays.asList("a", ",", "b", "(", "c", ",", "d", ")", ",", "e"));
     * LinkedList<LinkedList<String>> result = TokenHelper.splitTokens(tokens, ",");
     * // result contains [["a"], ["b", "(", "c", ",", "d", ")"], ["e"]]
     * </pre></blockquote>
     *
     * @param tokens the linked list of tokens to split
     * @param separator the token to use as a separator
     * @param limit the maximum number of splits to perform
     * @return a list of token lists separated by the specified separator
     */
    public static LinkedList<LinkedList<String>> splitTokens(LinkedList<String> tokens, String separator, int limit)
    {
        int parentheses = 0;
        int squareBrackets = 0;

        LinkedList<LinkedList<String>> separateTokens = new LinkedList<>();

        ListIterator<String> iter = tokens.listIterator();
        LinkedList<String> tokenList = new LinkedList<>();
        String next;

        while (iter.hasNext())
        {
            switch (next = iter.next())
            {
            case "[":
                squareBrackets++;
                break;
            case "]":
                squareBrackets--;
                break;
            case "(":
                parentheses++;
                break;
            case ")":
                parentheses--;
                break;
            }

            if (0 < limit
                    && squareBrackets == 0
                    && parentheses    == 0
                    && next.equals(separator))
            {
                limit--;

                // Flushing the previous tokens
                separateTokens.add(tokenList);
                tokenList = new LinkedList<>();
            }
            else
            {
                // Writing the observed token
                tokenList.add(next);
            }
        }
    
        // Flushing the last sequence
        separateTokens.add(tokenList);

        return separateTokens;
    }

    /**
     * Splits tokens by a separator with no limit on the number of splits.
     * Works identically to {@link #splitTokens(LinkedList, String, int)} with unlimited splits.
     * <p>
     * Example:
     * <blockquote><pre>
     * LinkedList<String> tokens = new LinkedList<>(Arrays.asList("a", ",", "b", ",", "c"));
     * LinkedList<LinkedList<String>> result = TokenHelper.splitTokens(tokens, ",");
     * // result contains [["a"], ["b"], ["c"]]
     * </pre></blockquote>
     *
     * @param tokens the linked list of tokens to split
     * @param separator the token to use as a separator
     * @return a list of token lists separated by the specified separator
     */
    public static LinkedList<LinkedList<String>> splitTokens(LinkedList<String> tokens, String separator)
    {
        return splitTokens(tokens, separator, Integer.MAX_VALUE);
    }

    /**
     * Removes outer parentheses from a token list if they form a balanced pair.
     * Only removes parentheses when all inner parentheses are properly balanced.
     * <p>
     * Example:
     * <blockquote><pre>
     * LinkedList<String> tokens = new LinkedList<>(Arrays.asList("(", "a", "+", "b", ")"));
     * LinkedList<String> result = TokenHelper.openParentheses(tokens);
     * // result contains ["a", "+", "b"]
     * 
     * LinkedList<String> tokens2 = new LinkedList<>(Arrays.asList("(", "a", "(", "b", ")", ")"));
     * LinkedList<String> result2 = TokenHelper.openParentheses(tokens2);
     * // result2 contains ["a", "(", "b", ")"]
     * </pre></blockquote>
     *
     * @param tokens the linked list of tokens to process
     * @return the token list with outer parentheses removed if applicable
     */
    public static LinkedList<String> openParentheses(LinkedList<String> tokens)
    {
        int balance;

        while (tokens.size() > 1
                && tokens.getFirst().equals("(")
                && tokens.getLast().equals(")"))
        {
            tokens.removeFirst();
            tokens.removeLast();
            balance = 0;

            for (String token: tokens)
            {
                switch (token)
                {
                case "(":
                    balance++;
                    break;
                case ")":
                    balance--;
                    break;
                }

                if (balance < 0)
                {
                    tokens.addFirst("(");
                    tokens.addLast(")");

                    return tokens;
                }
            }
        }

        return tokens;
    }

    /**
     * Converts escape sequences in a string to their corresponding characters.
     * Supports common escape sequences like \n, \t, \", \\, and Unicode escapes.
     * <p>
     * Example:
     * <blockquote><pre>
     * String input = "Hello\\nWorld\\t!";
     * String result = TokenHelper.unescape(input);
     * // result equals "Hello\nWorld\t!"
     * 
     * String input2 = "\\u0041pple"; // \u0041 is 'A'
     * String result2 = TokenHelper.unescape(input2);
     * // result2 equals "Apple"
     * </pre></blockquote>
     *
     * @param string the string containing escape sequences
     * @return the string with escape sequences converted to actual characters
     */
    public static String unescape(String string)
    {
        StringBuilder builder = new StringBuilder(string.length());

        for (int i = 0; i < string.length(); i++)
        {
            char ch = string.charAt(i);
            if (ch == '\\' && i + 1 < string.length())
            {
                switch (string.charAt(i + 1))
                {
                case '\\':
                    builder.append('\\');
                    i++;
                    break;
                case '\"':
                    builder.append('"');
                    i++;
                    break;
                case '\'':
                    builder.append('\'');
                    i++;
                    break;
                case 't':
                    builder.append('\t');
                    i++;
                    break;
                case 'b':
                    builder.append('\b');
                    i++;
                    break;
                case 'n':
                    builder.append('\n');
                    i++;
                    break;
                case 'r':
                    builder.append('\r');
                    i++;
                    break;
                case 'f':
                    builder.append('\f');
                    i++;
                    break;
                case 'u': // Unicode escape
                    if (i + 5 < string.length())
                    {
                        String hex = string.substring(i + 2, i + 6);
                        builder.append((char) Integer.parseInt(hex, 16));
                        i += 5;
                    }
                    break;
                default:
                    builder.append(ch);
                }
            }
            else
            {
                builder.append(ch);
            }
        }

        return builder.toString();
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always thrown when called
     */
    private TokenHelper()
    {
        throw new UnsupportedOperationException("Utility class");
    }

}
