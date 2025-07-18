package com.github.inc0grepoz.lix4j.unit.expression;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.inc0grepoz.lix4j.exception.SyntaxError;
import com.github.inc0grepoz.lix4j.unit.CompileTimeContext;
import com.github.inc0grepoz.lix4j.unit.Modifier;
import com.github.inc0grepoz.lix4j.unit.UnitSection;
import com.github.inc0grepoz.lix4j.util.TokenHelper;
import com.github.inc0grepoz.lix4j.value.Accessor;
import com.github.inc0grepoz.lix4j.value.AccessorBuilder;
import com.github.inc0grepoz.lix4j.value.AccessorOperator;
import com.github.inc0grepoz.lix4j.value.AccessorValue;

public class ExpressionResolver
{

//  private static final Pattern PATTERN_STRING = Pattern.compile("^\".+\"$");
//  private static final Pattern PATTERN_SPECIAL = Pattern.compile("[~!@#$%^&*()-=+\\[\\]{}:;'\"\\|/<>,.?]+");
    private static final Pattern PATTERN_NUMBER_INT = Pattern.compile("\\d+");
    private static final Pattern PATTERN_NUMBER_LONG = Pattern.compile("(\\d+)[Ll]");
    private static final Pattern PATTERN_NUMBER_FLOAT = Pattern.compile("(\\d*\\.?\\d+)[Ff]");
    private static final Pattern PATTERN_NUMBER_DOUBLE = Pattern.compile("(\\d*\\.?\\d+)[Dd]?");

    public static Accessor resolve(CompileTimeContext ctx, UnitSection section,
            LinkedList<String> tokens)
    {
        TokenHelper.openParentheses(tokens);

        if (tokens.size() == 1)
        {
            return resolveToken(ctx, section, tokens.getFirst(), Collections.emptySet());
        }

        return resolveOperator(ctx, section, tokens);
    }

    private static Accessor resolveToken(CompileTimeContext ctx, UnitSection section,
            String token, Set<Modifier> modifiers)
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
            case "this":
                return Accessor.THIS;
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
        if ((matcher = PATTERN_NUMBER_INT.matcher(token)).matches())
        {
            return AccessorValue.of(Integer.parseInt(matcher.group()));
        }
        if ((matcher = PATTERN_NUMBER_LONG.matcher(token)).matches())
        {
            return AccessorValue.of(Long.parseLong(matcher.group(1)));
        }
        if ((matcher = PATTERN_NUMBER_FLOAT.matcher(token)).matches())
        {
            return AccessorValue.of(Float.parseFloat(matcher.group(1)));
        }
        if ((matcher = PATTERN_NUMBER_DOUBLE.matcher(token)).matches())
        {
            return AccessorValue.of(Double.parseDouble(matcher.group(1)));
        }

        // Default to variable
        return ctx.getVarpool().handleVariable(token, modifiers);
    }

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
                handleFieldToken(ctx, section, builder, nextTokenList, index);
            }
        }

        return builder.build();
    }

    private static Accessor resolveIndex(CompileTimeContext ctx, UnitSection section,
            LinkedList<String> tokens)
    {
        if (tokens.peekLast() != null && tokens.peekLast().equals("]"))
        {
            return resolve(ctx, section, TokenHelper.readEnclosedTokensBackwards(tokens, "[", "]"));
        }

        return null;
    }

    private static boolean isValueToken(LinkedList<String> tokens)
    {
        return tokens.peekFirst() != null && tokens.peekFirst().equals("(");
    }

    private static boolean isParameterizedToken(LinkedList<String> tokens)
    {
        return tokens.peekLast() != null && tokens.peekLast().equals(")");
    }

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

    private static void handleParameterizedToken(CompileTimeContext ctx, UnitSection section,
            AccessorBuilder builder, LinkedList<String> tokens, Accessor index)
    {
        String name = tokens.poll();
        TokenHelper.openParentheses(tokens);

        Accessor[] accessors = tokens.isEmpty()
                ? new Accessor[0]
                : resolveParameters(ctx, section, TokenHelper.splitTokens(tokens, ","));

        if (builder.isEmpty())
        {
            builder.function(name, accessors);
            builder.index(index);
        }
        else
        {
            builder.method(name, accessors);
            builder.index(index);
        }
    }

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

    private static void handleFieldToken(CompileTimeContext ctx, UnitSection section,
            AccessorBuilder builder, LinkedList<String> tokens, Accessor index)
    {
        Set<Modifier> modifiers = readAllModifiers(tokens);

        if (tokens.size() > 1)
        {
            throw new SyntaxError("Unresolved expression: " + String.join(" ", tokens));
        }

        if (builder.isEmpty())
        {
            builder.accessor(resolveToken(ctx, section, tokens.poll(), modifiers));
            builder.index(index);
        }
        else
        {
            builder.field(tokens.poll());
            builder.index(index);
        }
    }

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

    private static boolean isStringToken(String token)
    {
        return token.charAt(0) == '"' && token.charAt(token.length() - 1) == '"';
    }

    private static boolean isCharacterToken(String token)
    {
        return token.charAt(0) == '\'' && token.charAt(token.length() - 1) == '\'';
    }

    private ExpressionResolver()
    {
        throw new UnsupportedOperationException("Utility class");
    }

}
