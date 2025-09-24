package com.github.inc0grepoz.lix4j.ctx;

import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import com.github.inc0grepoz.lix4j.unit.Modifier;
import com.github.inc0grepoz.lix4j.unit.expression.NamespaceResolver;
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

    private final Namespace globalNamespace = new Namespace(null, "");

    public CompileTimeVarpool()
    {
        super(new ArrayDeque<>());
    }

    public Namespace getGlobalNamespace()
    {
        return globalNamespace;
    }

    public AccessorVariable handleVariable(CompileTimeContext ctx, Set<Modifier> modifiers,
            LinkedList<String> namespaceParts, String name)
    {
        Entry<Identifier, AccessorVariable> entry = lookup(ctx, namespaceParts, name);
        AccessorVariable xcsVar;

        // Existing variable
        if (modifiers.isEmpty())
        {
            if (entry == null)
            {
//              System.err.print("New variable ");
//              System.err.print(token);
//              System.err.print(" seems to have no modifiers. This syntax for");
//              System.err.print(" variable declaration is deprecated and may be");
//              System.err.print(" removed in future releases of LIX4J. Use `var`");
//              System.err.print(" for declaring variables.");
//              System.err.println();

                return handleVariable(ctx, EnumSet.of(Modifier.VAR), namespaceParts, name);
            }

            Identifier id = entry.getKey();
            xcsVar = get(id);

            Class<? extends AccessorVariable> clazz = xcsVar.getClass();
            if (clazz == AccessorVariableStack.class)
            {
                xcsVar = AccessorVariableStack.of(id);
            }
            // All statics are pointered
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
            Namespace  ns = NamespaceResolver.resolveLocationNamespace(ctx, namespaceParts);

            // Duplicate variables are disallowed within equal namespaces
            if (entry != null && entry.getKey().getNamespace() == ns)
            {
                throw new RuntimeException("Two or more variables of the same identifier: " + entry.getKey());
            }

            Identifier id = Identifier.of(ns, name);

            if (modifiers.contains(Modifier.STATIC))
            {
                set(id, xcsVar = AccessorVariableStatic.of(id));
            }
            else
            {
                set(id, xcsVar = AccessorVariableStack.of(id));
            }
        }

        return xcsVar;
    }

}
