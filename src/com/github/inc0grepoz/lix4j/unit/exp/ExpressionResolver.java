package com.github.inc0grepoz.lix4j.unit.exp;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;

import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.exception.SyntaxError;
import com.github.inc0grepoz.lix4j.id.Identifier;
import com.github.inc0grepoz.lix4j.unit.Modifier;
import com.github.inc0grepoz.lix4j.unit.UnitSection;
import com.github.inc0grepoz.lix4j.util.Collections;
import com.github.inc0grepoz.lix4j.util.Patterns;
import com.github.inc0grepoz.lix4j.util.TokenHelper;
import com.github.inc0grepoz.lix4j.value.Accessor;
import com.github.inc0grepoz.lix4j.value.AccessorBuilder;
import com.github.inc0grepoz.lix4j.value.AccessorOperator;
import com.github.inc0grepoz.lix4j.value.AccessorValue;

/**
 * Resolves expressions by parsing tokens and converting them into Accessor objects.
 * Handles literals, variables, operators, method calls, field access, and complex expressions.
 * Supports unary, binary, and ternary operators with proper precedence handling.
 * 
 * @author inc0g-repoz
 */
public class ExpressionResolver
{

    /**
     * Resolves a sequence of tokens into an Accessor representing the expression.
     * Processes the tokens by handling parentheses, operators, and access chains.
     * <p>
     * Example:
     * <blockquote><pre>
     * LinkedList<String> tokens = new LinkedList<>(Arrays.asList("a", "+", "b"));
     * Accessor result = ExpressionResolver.resolve(ctx, section, tokens);
     * // Returns an Accessor representing the addition operation a + b
     * </pre></blockquote>
     *
     * @param ctx the compile-time context containing variable and namespace information
     * @param section the unit section where the expression occurs
     * @param tokens the linked list of tokens representing the expression
     * @return an Accessor representing the resolved expression
     * @throws SyntaxError if the expression contains syntax errors or unresolved tokens
     */
    public static Accessor resolve(CompileTimeContext ctx, UnitSection section,
            LinkedList<String> tokens)
    {
        TokenHelper.openParentheses(tokens);

        if (tokens.size() == 1)
        {
            return resolveToken(ctx, section, Collections.emptyLinkedList(), tokens.getFirst(), Collections.emptySet());
        }

        return resolveOperator(ctx, section, tokens);
    }

    /**
     * Resolves a single token into an Accessor.
     * Handles literals (null, boolean, numbers, strings, characters) and variables.
     * <p>
     * Example:
     * <blockquote><pre>
     * Accessor nullAccessor = resolveToken(ctx, section, emptyList(), "null", emptySet());
     * // Returns Accessor.NULL
     * 
     * Accessor numAccessor = resolveToken(ctx, section, emptyList(), "42", emptySet());
     * // Returns AccessorValue containing Integer 42
     * </pre></blockquote>
     *
     * @param ctx the compile-time context
     * @param section the unit section
     * @param unresolvedTargetNamespace the namespace path for unresolved identifiers
     * @param token the single token to resolve
     * @param modifiers the set of modifiers applied to the token
     * @return an Accessor representing the resolved token
     * @throws SyntaxError if the token is invalid or malformed
     */
    private static Accessor resolveToken(CompileTimeContext ctx, UnitSection section,
            LinkedList<String> unresolvedTargetNamespace, String token, Set<Modifier> modifiers)
    {
        if (token == null)
        {
            return Accessor.NULL;
        }

        // Handle reserved tokens
        switch (token)
        {
            case "null":
                return Accessor.NULL;
            case "true":
                return Accessor.TRUE;
            case "false":
                return Accessor.FALSE;
        }

        // Handle string tokens
        if (isStringToken(token))
        {
            String string = TokenHelper.unescape(token.substring(1, token.length() - 1));
            return AccessorValue.of(string);
        }

        // Handle characters
        if (isCharacterToken(token))
        {
            String string = TokenHelper.unescape(token.substring(1, token.length() - 1));

            if (string.length() != 1)
            {
                throw new SyntaxError("Illegal token " + token);
            }

            return AccessorValue.of(string.charAt(0));
        }

        Matcher matcher;

        // Handle numeric tokens
        if ((matcher = Patterns.NUMBER_INT.matcher(token)).matches())
        {
            return AccessorValue.of(Integer.parseInt(matcher.group()));
        }
        if ((matcher = Patterns.NUMBER_LONG.matcher(token)).matches())
        {
            return AccessorValue.of(Long.parseLong(matcher.group(1)));
        }
        if ((matcher = Patterns.NUMBER_FLOAT.matcher(token)).matches())
        {
            return AccessorValue.of(Float.parseFloat(matcher.group(1)));
        }
        if ((matcher = Patterns.NUMBER_DOUBLE.matcher(token)).matches())
        {
            return AccessorValue.of(Double.parseDouble(matcher.group(1)));
        }

        // Default to variable
        return ctx.getVarpool().handleVariable(ctx, modifiers,
                Identifier.unresolved(unresolvedTargetNamespace, ctx.getNamespace(), token));
    }

    /**
     * Resolves operator expressions by checking for unary, binary, and ternary operators.
     * Processes operators according to their type and precedence.
     * <p>
     * Example:
     * <blockquote><pre>
     * LinkedList<String> tokens = new LinkedList<>(Arrays.asList("!", "true"));
     * Accessor result = resolveOperator(ctx, section, tokens);
     * // Returns Accessor representing !true operation
     * </pre></blockquote>
     *
     * @param ctx the compile-time context
     * @param section the unit section
     * @param tokens the tokens to process for operators
     * @return an Accessor representing the operator expression
     */
    private static Accessor resolveOperator(CompileTimeContext ctx, UnitSection section, LinkedList<String> tokens)
    {
        for (Operator operator: ctx.getScript().getOperators())
        {
            switch (operator.getType())
            {
            case UNARY_LEFT:
                if (operator.getName().equals(tokens.peekFirst()))
                {
                    tokens.pollFirst();
                    return AccessorOperator.of(operator, resolve(ctx, section, tokens));
                }
                break;
            case UNARY_RIGHT:
                if (operator.getName().equals(tokens.peekLast()))
                {
                    tokens.pollLast();
                    return AccessorOperator.of(operator, resolve(ctx, section, tokens));
                }
                break;
            case BINARY:
                if (tokens.contains(operator.getName()))
                {
                    LinkedList<LinkedList<String>> separateTokens = TokenHelper.splitTokens(tokens, operator.getName());
                    if (separateTokens.size() > 1)
                    {
                        Accessor[] operands = new Accessor[separateTokens.size()];
                        for (int i = 0; i < operands.length; i++)
                        {
                            operands[i] = resolve(ctx, section, separateTokens.poll());
                        }
                        return AccessorOperator.of(operator, operands);
                    }
                }
                break;
            case TERNARY:
                if (tokens.contains("?") && tokens.contains(":"))
                {
                    LinkedList<LinkedList<String>> separateTokens = TokenHelper.splitTokens(tokens, "?", 1);
                    separateTokens.addAll(TokenHelper.splitTokens(separateTokens.pollLast(), ":", 1));
                    if (separateTokens.size() == 3)
                    {
                        Accessor[] operands = new Accessor[separateTokens.size()];
                        for (int i = 0; i < operands.length; i++)
                        {
                            operands[i] = resolve(ctx, section, separateTokens.poll());
                        }
                        return AccessorOperator.of(operator, operands);
                    }
                }
                break;
            }
        }

        return resolveLinkedAccessor(ctx, section, tokens);
    }

    /**
     * Resolves linked accessor expressions containing dots for member access.
     * Handles field access, method calls, and array indexing in chain expressions.
     * <p>
     * Example:
     * <blockquote><pre>
     * LinkedList<String> tokens = new LinkedList<>(Arrays.asList("obj", ".", "field", ".", "method", "(", ")", "[", "0", "]"));
     * Accessor result = resolveLinkedAccessor(ctx, section, tokens);
     * // Returns Accessor representing obj.field.method()[0]
     * </pre></blockquote>
     *
     * @param ctx the compile-time context
     * @param section the unit section
     * @param tokens the tokens representing the access chain
     * @return an Accessor representing the linked access expression
     */
    private static Accessor resolveLinkedAccessor(CompileTimeContext ctx, UnitSection section,
            LinkedList<String> tokens)
    {
        LinkedList<LinkedList<String>> splitTokens = TokenHelper.splitTokens(tokens, ".");
        AccessorBuilder builder = Accessor.builder();
        Accessor index = null;

        while (!splitTokens.isEmpty())
        {
            LinkedList<String> nextTokenList = splitTokens.poll();
            index = resolveIndex(ctx, section, nextTokenList);

            if (isValueToken(nextTokenList))
            {
                handleValueToken(ctx, section, builder, nextTokenList, index);
            }
            else if (isParameterizedToken(nextTokenList))
            {
                handleParameterizedToken(ctx, section, builder, nextTokenList, index);
            }
            else
            {
                handleNonParameterizedToken(ctx, section, builder, nextTokenList, index);
            }
        }

        return builder.build();
    }

    /**
     * Resolves array indexing expressions within token lists.
     * Extracts and processes the index expression from square brackets.
     * <p>
     * Example:
     * <blockquote><pre>
     * LinkedList<String> tokens = new LinkedList<>(Arrays.asList("arr", "[", "i", "+", "1", "]"));
     * Accessor index = resolveIndex(ctx, section, tokens);
     * // Returns Accessor representing i + 1
     * </pre></blockquote>
     *
     * @param ctx the compile-time context
     * @param section the unit section
     * @param tokens the tokens potentially containing an index expression
     * @return an Accessor representing the index expression, or null if no index present
     */
    private static Accessor resolveIndex(CompileTimeContext ctx, UnitSection section,
            LinkedList<String> tokens)
    {
        if (tokens.peekLast() != null && tokens.peekLast().equals("]"))
        {
            return resolve(ctx, section, TokenHelper.readEnclosedTokensBackwards(tokens, "[", "]"));
        }

        return null;
    }

    /**
     * Checks if tokens represent a value expression (enclosed in parentheses).
     *
     * @param tokens the tokens to check
     * @return true if the tokens start with "("
     */
    private static boolean isValueToken(LinkedList<String> tokens)
    {
        return tokens.peekFirst() != null && tokens.peekFirst().equals("(");
    }

    /**
     * Checks if tokens represent a parameterized expression (end with ")").
     *
     * @param tokens the tokens to check
     * @return true if the tokens end with ")"
     */
    private static boolean isParameterizedToken(LinkedList<String> tokens)
    {
        return tokens.peekLast() != null && tokens.peekLast().equals(")");
    }

    /**
     * Handles value tokens (expressions in parentheses) during accessor building.
     *
     * @param ctx the compile-time context
     * @param section the unit section
     * @param builder the accessor builder to modify
     * @param tokens the value tokens to process
     * @param index the index accessor if present
     * @throws SyntaxError if value token appears in invalid position
     */
    private static void handleValueToken(CompileTimeContext ctx, UnitSection section,
            AccessorBuilder builder, LinkedList<String> tokens, Accessor index)
    {
        if (builder.isEmpty())
        {
            builder.accessor(resolve(ctx, section, tokens));
            builder.index(index);
        }
        else
        {
            throw new SyntaxError("Illegal member name: " + String.join(" ", tokens));
        }
    }

    /**
     * Handles parameterized tokens (function/method calls) during accessor building.
     * Processes function calls with parameters.
     * <p>
     * Example:
     * <blockquote><pre>
     * LinkedList<String> tokens = new LinkedList<>(Arrays.asList("func", "(", "arg1", ",", "arg2", ")"));
     * handleParameterizedToken(ctx, section, builder, tokens, null);
     * // Adds function call to builder with two arguments
     * </pre></blockquote>
     *
     * @param ctx the compile-time context
     * @param section the unit section
     * @param builder the accessor builder to modify
     * @param tokens the parameterized tokens to process
     * @param index the index accessor if present
     */
    private static void handleParameterizedToken(CompileTimeContext ctx, UnitSection section,
            AccessorBuilder builder, LinkedList<String> tokens, Accessor index)
    {
        LinkedList<String> targetNamespace = NamespaceResolver.resolveTargetNamespacePath(tokens);
        String name = tokens.poll();
        TokenHelper.openParentheses(tokens);

        Accessor[] accessors = tokens.isEmpty()
                ? new Accessor[0]
                : resolveParameters(ctx, section, TokenHelper.splitTokens(tokens, ","));

        if (builder.isEmpty())
        {
            Identifier id = Identifier.unresolved(targetNamespace,
                    ctx.getNamespace(), name, accessors.length);
            builder.function(id, accessors);
            builder.index(index);
        }
        else
        {
            builder.method(name, accessors);
            builder.index(index);
        }
    }

    /**
     * Resolves parameter lists for function/method calls.
     * Converts parameter token lists into Accessor arrays.
     *
     * @param ctx the compile-time context
     * @param section the unit section
     * @param paramList the list of parameter token lists
     * @return an array of Accessors representing the resolved parameters
     */
    private static Accessor[] resolveParameters(CompileTimeContext ctx, UnitSection section,
            LinkedList<LinkedList<String>> paramList)
    {
        Accessor[] accessors = new Accessor[paramList.size()];

        for (int i = 0; i < accessors.length; i++)
        {
            accessors[i] = resolve(ctx, section, paramList.poll());
        }

        return accessors;
    }

    /**
     * Handles non-parameterized tokens (fields, variables) during accessor building.
     * Processes simple identifiers and field access.
     *
     * @param ctx the compile-time context
     * @param section the unit section
     * @param builder the accessor builder to modify
     * @param tokens the non-parameterized tokens to process
     * @param index the index accessor if present
     * @throws SyntaxError if tokens contain unresolved expressions
     */
    private static void handleNonParameterizedToken(CompileTimeContext ctx, UnitSection section,
            AccessorBuilder builder, LinkedList<String> tokens, Accessor index)
    {
        Set<Modifier> modifiers = readAllModifiers(tokens);
        LinkedList<String> targetNamespace = NamespaceResolver.resolveTargetNamespacePath(tokens);

        if (tokens.size() > 1)
        {
            throw new SyntaxError("Unresolved expression: " + String.join(" ", tokens));
        }

        String name = tokens.poll();

        if (builder.isEmpty())
        {
            builder.accessor(resolveToken(ctx, section, targetNamespace, name, modifiers));
            builder.index(index);
        }
        else
        {
            builder.field(name);
            builder.index(index);
        }
    }

    /**
     * Reads and consumes all modifier tokens from the beginning of the token list.
     * Removes modifier tokens from the list and returns them as a set.
     * <p>
     * Example:
     * <blockquote><pre>
     * LinkedList<String> tokens = new LinkedList<>(Arrays.asList("static", "var", "variable"));
     * Set<Modifier> modifiers = readAllModifiers(tokens);
     * // modifiers contains [STATIC, VAR], tokens becomes ["variable"]
     * </pre></blockquote>
     *
     * @param tokens the tokens to scan for modifiers
     * @return a set of Modifier values found in the tokens
     * @throws SyntaxError if the same modifier is used multiple times
     */
    private static Set<Modifier> readAllModifiers(LinkedList<String> tokens)
    {
        Modifier[] values = Modifier.values();
        Set<Modifier> set = EnumSet.noneOf(Modifier.class);

        outer:
        while (!tokens.isEmpty())
        {
            String token = tokens.peek();

            for (int i = 0; i < values.length; i++)
            {
                if (!values[i].getKeyword().equals(token))
                {
                    continue;
                }

                if (!set.add(values[i]))
                {
                    throw new SyntaxError("Used `" + values[i] + "` multiple times");
                }

                tokens.poll(); // consume the token
                continue outer; // check next token
            }

            break; // no modifier matches
        }

        return set; 
    }

    /**
     * Checks if a token represents a string literal.
     *
     * @param token the token to check
     * @return true if the token is enclosed in double quotes
     */
    private static boolean isStringToken(String token)
    {
        return token.charAt(0) == '"' && token.charAt(token.length() - 1) == '"';
    }

    /**
     * Checks if a token represents a character literal.
     *
     * @param token the token to check
     * @return true if the token is enclosed in single quotes
     */
    private static boolean isCharacterToken(String token)
    {
        return token.charAt(0) == '\'' && token.charAt(token.length() - 1) == '\'';
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always thrown when called
     */
    private ExpressionResolver()
    {
        throw new UnsupportedOperationException("Utility class");
    }

}
