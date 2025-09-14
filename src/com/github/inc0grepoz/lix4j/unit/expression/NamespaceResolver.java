package com.github.inc0grepoz.lix4j.unit.expression;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.ctx.Namespace;

public class NamespaceResolver
{

    public static LinkedList<String> resolveDeclaredNamespacePath(CompileTimeContext ctx, LinkedList<String> tokens) {
        LinkedList<String> path = new LinkedList<>();

        // Handling absolute paths (leading separator)
        if (tokens.peek().equals(Namespace.SEPARATOR)) {
            tokens.poll();
            path.add(Namespace.ABSOLUTE_MARKER);
        }

        // Handling pairs [part, separator]
        while (tokens.size() > 2) {
            String part = tokens.poll();
            String nextToken = tokens.peek();

            if (!nextToken.equals(Namespace.SEPARATOR)) break;

            path.add(part);
            tokens.poll(); // Removing a separator
        }

        return path;
    }

    public static Namespace resolveDeclaredNamespace(CompileTimeContext ctx, LinkedList<String> tokens)
    {
        if (tokens.isEmpty()) return ctx.getScript().getGlobalNamespace();

        if (tokens.peek().equals(Namespace.SEPARATOR))
        {
            tokens.poll(); // Removing the leading separator
            return resolvePath(tokens, ctx.getScript().getGlobalNamespace());
        }

        return resolvePath(tokens, ctx.getNamespace());
    }

    public static Namespace resolveLocationNamespace(CompileTimeContext ctx, LinkedList<String> tokens)
    {
        if (tokens.isEmpty()) return ctx.getNamespace();

        if (tokens.peek().equals(Namespace.SEPARATOR))
        {
            tokens.poll(); // Removing the leading separator
            return resolvePath(tokens, ctx.getScript().getGlobalNamespace());
        }

        return resolvePath(tokens, ctx.getNamespace());
    }

    private static Namespace resolvePath(LinkedList<String> tokens, Namespace base)
    {
        Namespace current = base;

        // Only handle pairs [part, separator]
        while (tokens.size() > 2)
        {
            // Checking the next token after the current
            String nextToken = tokens.get(1);
            if (!nextToken.equals(Namespace.SEPARATOR)) break;

            // Retrieving a namespace name
            String part = tokens.poll();

            // Removing a separator
            tokens.poll();

            // Nesting the current namespace
            current = current.nest(part);
        }

        return current;
    }

}
