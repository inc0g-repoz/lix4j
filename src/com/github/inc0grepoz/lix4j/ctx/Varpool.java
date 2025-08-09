package com.github.inc0grepoz.lix4j.ctx;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import com.github.inc0grepoz.lix4j.util.Namespace;

/**
 * Represents an abstract pool for variables. Stores a stack
 * of scopes with variables in their scopes and lets the engine
 * navigate through them.
 * 
 * @author inc0g-repoz
 */
@SuppressWarnings("unchecked")
public abstract class Varpool<T>
{

    private static final Object NO_KEY = new Object();

    protected final ArrayDeque<Map<String, Object>> stack;

    protected Varpool(ArrayDeque<Map<String, Object>> stack)
    {
        this.stack = stack;
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner("\n");

        for (Map<String, Object> scope: stack)
        {
            scope.forEach((k, v) -> joiner.add(k + " = " + v));
        }

        return joiner.toString();
    }

    /**
     * Sets the specified variable in the context section.
     * 
     * @param the variable namespace
     * @param the variable name
     * @param the variable value
     * @return the variable value
     */
    public T set(String namespace, String name, T value)
    {
        if (stack.isEmpty())
        {
            throw new IllegalStateException("No active section to set variable in");
        }

        String id = toNamespaced(namespace, name);

        // Redefining in another layer, if defined
        for (Map<String, Object> scope: stack)
        {
            if (scope.containsKey(id))
            {
                scope.put(id, value);
                return value;
            }
        }

        // Define at the last layer
        stack.peek().put(id, value);
        return value;
    }

    /**
     * Returns the variable by the specified name, if exists.
     * 
     * @param namespace the variable namespace
     * @param name the variable name
     * @return the variable value
     * @throws RuntimeException
     *         if no variable is mapped to the specified name
     */
    public T get(String namespace, String name)
    {
        Object o;
        String id = toNamespaced(namespace, name);
        String gid = toNamespaced(Namespace.GLOBAL, name);

        for (Map<String, Object> scope: stack)
        {
            if ((o = scope.getOrDefault(id, NO_KEY)) != NO_KEY)
            {
                return (T) o;
            }

            if ((o = scope.getOrDefault(gid, NO_KEY)) != NO_KEY)
            {
                return (T) o;
            }
        }

        return null;
    }

    /**
     * Enters a new context section and returns this instance.
     * 
     * @return this instance
     */
    public Varpool<T> enterSection()
    {
        stack.push(new HashMap<>());
        return this;
    }

    /**
     * Exits the current context section and returns this instance.
     * 
     * @return this instance
     */
    public Varpool<T> exitSection()
    {
        if (stack.isEmpty())
        {
            throw new IllegalStateException("No active section to exit");
        }

        stack.pop();
        return this;
    }

    public int getLayersCount()
    {
        return stack.size();
    }

    protected String toNamespaced(String namespace, String name)
    {
        return namespace + "::" + name;
    }

}
