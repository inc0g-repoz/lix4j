package com.github.inc0grepoz.lix4j.unit;

import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import com.github.inc0grepoz.lix4j.value.AccessorVariable;
import com.github.inc0grepoz.lix4j.value.AccessorVariableStack;
import com.github.inc0grepoz.lix4j.value.AccessorVariableStatic;
import com.github.inc0grepoz.lix4j.value.Variable;

public class CompileTimeVarpool
{

    private static final Object NO_KEY = new Object();

    private final ArrayDeque<Map<String, Object>> stack = new ArrayDeque<>();

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner("\n");

        for (Map<String, Object> layer: stack)
        {
            layer.forEach((k, v) -> joiner.add(k + " = " + v));
        }

        return joiner.toString();
    }

    public AccessorVariable handleVariable(String token, Set<Modifier> modifiers)
    {
        AccessorVariable xcsVar = get(token);

        // Existing variable
        if (modifiers.isEmpty())
        {
            if (xcsVar == null)
            {
//              System.err.print("New variable ");
//              System.err.print(token);
//              System.err.print(" seems to have no modifiers. This syntax for");
//              System.err.print(" variable declaration is deprecated and may be");
//              System.err.print(" removed in future releases of LIX4J. Use `var`");
//              System.err.print(" for declaring variables.");
//              System.err.println();

                return handleVariable(token, EnumSet.of(Modifier.VAR));
            }

            Class<? extends AccessorVariable> clazz = xcsVar.getClass();

            if (clazz == AccessorVariableStack.class)
            {
                xcsVar = AccessorVariableStack.of(token);
            }
            else if (clazz == AccessorVariableStatic.class)
            {
                Variable ptr = ((AccessorVariableStatic) xcsVar).getVariable();
                xcsVar = AccessorVariableStatic.of(ptr);
            }
            else
            {
                throw new RuntimeException("Unknown variable class: " + clazz);
            }
        }
        // Fresh variable
        else
        {
            if (xcsVar != null)
            {
                throw new RuntimeException("Two or more variables of the same name: " + token);
            }

            if (modifiers.contains(Modifier.STATIC))
            {
                set(token, xcsVar = AccessorVariableStatic.of(token));
            }
            else
            {
                set(token, xcsVar = AccessorVariableStack.of(token));
            }
        }

        return xcsVar;
    }

    /**
     * Sets the specified variable in the context section.
     * 
     * @param the variable name
     * @param the variable value
     * @return the variable value
     */
    public AccessorVariable set(String name, AccessorVariable value)
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
        return (AccessorVariable) value;
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

    public int getLayersCount()
    {
        return stack.size();
    }

}
