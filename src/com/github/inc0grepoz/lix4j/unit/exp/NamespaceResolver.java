package com.github.inc0grepoz.lix4j.unit.exp;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.id.Namespace;

public class NamespaceResolver
{

    public static LinkedList<String> resolveTargetNamespacePath(LinkedList<String> tokens)
    {
        LinkedList<String> path = new LinkedList<>();

        if (tokens.size() < 2)
        {
            return path;
        }

        // Handling absolute paths (leading separator)
        if (tokens.peek().equals(Namespace.SEPARATOR))
        {
            tokens.poll();
            path.add(Namespace.ABSOLUTE_MARKER);
        }

        // Handling pairs [part, separator]
        while (tokens.size() > 2)
        {
            // Checking the next token after the current
            String nextToken = tokens.get(1);
            if (!nextToken.equals(Namespace.SEPARATOR)) break;

            // Retrieving a namespace name
            String part = tokens.poll();

            // Removing a separator
            tokens.poll();

            // Adding a namespace name to the path
            path.add(part);
        }

        return path;
    }

}
