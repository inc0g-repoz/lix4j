package com.github.inc0grepoz.lix4j.ctx;

import java.util.ArrayDeque;
import java.util.Map;

import com.github.inc0grepoz.lix4j.value.AccessorUnassigned;

/**
 * Represents a non-static pool for variables.
 * 
 * @author inc0g-repoz
 */
public class ExecutionVarpool extends Varpool implements Cloneable
{

    private static final Object NO_KEY = new Object();

    /**
     * Creates a new non-static pool.
     */
    public ExecutionVarpool()
    {
        this(new ArrayDeque<>());
    }

    private ExecutionVarpool(ArrayDeque<Map<String, Object>> stack)
    {
        super(stack);
    }

    /**
     * Sets the specified variable in the context section.
     * 
     * @param the variable namespace
     * @param the variable name
     * @param the variable value
     * @return the variable value
     */
    public Object set(String namespace, String name, Object value)
    {
        if (stack.isEmpty())
        {
            throw new IllegalStateException("No active section to set variable in");
        }

        String id = toNamespaced(namespace, name);

        // Redefining in another layer, if defined
        for (Map<String, Object> layer: stack)
        {
            if (layer.containsKey(id))
            {
                layer.put(id, value);
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
    public Object get(String namespace, String name)
    {
        Object o;
        String id = toNamespaced(namespace, name);

        for (Map<String, Object> layer: stack)
        {
            // The used map implementation may support null values
            if ((o = layer.getOrDefault(id, NO_KEY)) != NO_KEY)
            {
                return o;
            }
        }

        return new AccessorUnassigned(namespace, name);
    }

    @Override
    public ExecutionVarpool clone()
    {
        return new ExecutionVarpool(stack.clone());
    }

}
