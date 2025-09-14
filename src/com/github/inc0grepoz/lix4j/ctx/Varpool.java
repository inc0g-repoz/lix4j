package com.github.inc0grepoz.lix4j.ctx;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Map.Entry;
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

    protected final ArrayDeque<Lookup<T>> stack;

    protected Varpool(ArrayDeque<Lookup<T>> stack)
    {
        this.stack = stack;
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner("\n");

        for (Lookup<T> scope: stack)
        {
            scope.forEach((k, v) -> joiner.add(k + " = " + v));
        }

        return joiner.toString();
    }

    public Entry<Identifier, T> lookup(CompileTimeContext ctx, LinkedList<String> nsParts, String name)
    {
        Entry<Identifier, T> entry;

        for (Lookup<T> scope: stack)
        {
            if ((entry = scope.findEntry(ctx, nsParts, name)) != null)
            {
                return entry;
            }
        }

        return null;
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
        for (Lookup<T> scope: stack)
        {
            if (scope.containsKey(identifier))
            {
                scope.set(identifier, value);
                return value;
            }
        }

        // Define at the last layer
        stack.peek().set(identifier, value);
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

        for (Lookup<T> scope: stack)
        {
            if ((o = scope.get(identifier, NO_KEY)) != NO_KEY)
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
        stack.push(new Lookup<>());
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
