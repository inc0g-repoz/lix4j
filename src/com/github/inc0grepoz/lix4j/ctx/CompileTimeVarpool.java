package com.github.inc0grepoz.lix4j.ctx;

import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.Set;

import com.github.inc0grepoz.lix4j.unit.Modifier;
import com.github.inc0grepoz.lix4j.value.AccessorVariable;
import com.github.inc0grepoz.lix4j.value.AccessorVariableStack;
import com.github.inc0grepoz.lix4j.value.AccessorVariableStatic;
import com.github.inc0grepoz.lix4j.value.Variable;

/**
 * Represents a pool for variables for storing them while
 * the script is being compiled.
 * 
 * @author inc0g-repoz
 */
public class CompileTimeVarpool extends Varpool<AccessorVariable>
{

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

}
