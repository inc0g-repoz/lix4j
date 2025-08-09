package com.github.inc0grepoz.lix4j.ctx;

import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import com.github.inc0grepoz.lix4j.unit.Modifier;
import com.github.inc0grepoz.lix4j.value.AccessorVariable;
import com.github.inc0grepoz.lix4j.value.AccessorVariableStack;
import com.github.inc0grepoz.lix4j.value.AccessorVariableStatic;
import com.github.inc0grepoz.lix4j.value.Variable;

public class CompileTimeVarpool extends Varpool
{

    private static final Object NO_KEY = new Object();

    public CompileTimeVarpool()
    {
        super(new ArrayDeque<>());
    }

    public AccessorVariable handleVariable(String namespace, String name, Set<Modifier> modifiers)
    {
        AccessorVariable xcsVar = get(namespace, name);

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

                return handleVariable(namespace, name, EnumSet.of(Modifier.VAR));
            }

            Class<? extends AccessorVariable> clazz = xcsVar.getClass();

            if (clazz == AccessorVariableStack.class)
            {
                xcsVar = AccessorVariableStack.of(namespace, name);
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
                throw new RuntimeException("Two or more variables of the same name: " + name);
            }

            if (modifiers.contains(Modifier.STATIC))
            {
                set(namespace, name, xcsVar = AccessorVariableStatic.of(namespace, name));
            }
            else
            {
                set(namespace, name, xcsVar = AccessorVariableStack.of(namespace, name));
            }
        }

        return xcsVar;
    }

    /**
     * Sets the specified variable in the context section.
     * 
     * @param the variable namespace
     * @param the variable name
     * @param the variable value
     * @return the variable value
     */
    public AccessorVariable set(String namespace, String name, AccessorVariable value)
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
        return (AccessorVariable) value;
    }

    /**
     * Returns the variable by the specified name, if exists.
     * 
     * @param the variable namespace
     * @param the variable name
     * @return the variable value
     * @throws RuntimeException
     *         if no variable is mapped to the specified name
     */
    public AccessorVariable get(String namespace, String name)
    {
        Object o;
        String id = toNamespaced(namespace, name);

        for (Map<String, Object> layer: stack)
        {
            // The used map implementation may support null values
            if ((o = layer.getOrDefault(id, NO_KEY)) != NO_KEY)
            {
                return (AccessorVariable) o;
            }
        }

        return null;
    }

}
