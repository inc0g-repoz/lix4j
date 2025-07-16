package com.github.inc0grepoz.lix4j.unit;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import com.github.inc0grepoz.lix4j.value.AccessorVariable;

public class CompileTimeVarpool
{

    private static final Object NO_KEY = new Object();

    private final ArrayDeque<Map<String, Object>> stack = new ArrayDeque<>();

    /**
     * Sets the specified variable in the context section.
     * 
     * @param the variable name
     * @param the variable value
     * @return the variable value
     */
    public Object set(String name, AccessorVariable value)
    {
        if (stack.isEmpty())
        {
            throw new IllegalStateException("No active section to set variable in");
        }

        // Redefining in another layer, if defined
        for (Map<String, Object> layer: stack)
        {
            if (layer.containsKey(name))
            {
                layer.put(name, value);
                return value;
            }
        }

        // Define at the last layer
        stack.peek().put(name, value);
        return value;
    }

    /**
     * Returns the variable by the specified name, if exists.
     * 
     * @param the variable name
     * @return the variable value
     * @throws RuntimeException
     *         if no variable is mapped to the specified name
     */
    public AccessorVariable get(String name)
    {
        Object o;

        for (Map<String, Object> layer: stack)
        {
            // The used map implementation may support null values
            if ((o = layer.getOrDefault(name, NO_KEY)) != NO_KEY)
            {
                return (AccessorVariable) o;
            }
        }

        return null;
    }

    /**
     * Enters a new context section and returns this instance.
     * 
     * @return this instance
     */
    public CompileTimeVarpool enterSection()
    {
        stack.push(new HashMap<>());
        return this;
    }

    /**
     * Exits the current context section and returns this instance.
     * 
     * @return this instance
     */
    public CompileTimeVarpool exitSection()
    {
        if (stack.isEmpty())
        {
            throw new IllegalStateException("No active section to exit");
        }

        stack.pop();
        return this;
    }

}
