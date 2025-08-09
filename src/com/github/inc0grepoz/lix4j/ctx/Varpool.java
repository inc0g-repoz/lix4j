package com.github.inc0grepoz.lix4j.ctx;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

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

    protected final ArrayDeque<Map<Identifier, Object>> stack;

    protected Varpool(ArrayDeque<Map<Identifier, Object>> stack)
    {
        this.stack = stack;
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner("\n");

        for (Map<Identifier, Object> scope: stack)
        {
            scope.forEach((k, v) -> joiner.add(k + " = " + v));
        }

        return joiner.toString();
    }

    /**
     * Sets the specified variable in the context section.
     * 
     * @param the variable identifier
     * @param the variable value
     * @return the variable value
     */
    public T set(Identifier identifier, T value)
    {
        if (stack.isEmpty())
        {
            throw new IllegalStateException("No active section to set variable in");
        }

        // Redefining in another layer, if defined
        for (Map<Identifier, Object> scope: stack)
        {
            if (scope.containsKey(identifier))
            {
                scope.put(identifier, value);
                return value;
            }
        }

        // Define at the last layer
        stack.peek().put(identifier, value);
        return value;
    }

    /**
     * Returns the variable by the specified name, if exists.
     * 
     * @param identifier the variable identifier
     * @return the variable value
     * @throws RuntimeException
     *         if no variable is mapped to the specified name
     */
    public T get(Identifier identifier)
    {
        Object o;

        for (Map<Identifier, Object> scope: stack)
        {
            if ((o = scope.getOrDefault(identifier, NO_KEY)) != NO_KEY)
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

}
