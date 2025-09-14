package com.github.inc0grepoz.lix4j.ctx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

/**
 * An aggregate in which you can do lookups of instances
 * by namespaced identifiers.
 * 
 * @param <T> the instance type to store
 */
@SuppressWarnings("unchecked")
public class Lookup<T> implements Cloneable, Iterable<Entry<Identifier, T>>
{

    private final Map<Identifier, T> identifiable;

    public Lookup()
    {
        identifiable = new HashMap<>();
    }

    private Lookup(Map<Identifier, T> identifiable)
    {
        (this.identifiable = new HashMap<>()).putAll(identifiable);
    }

    public Entry<Identifier, T> findEntry(CompileTimeContext ctx, LinkedList<String> nsParts, String name)
    {
        Namespace level, n;

        // Determining, where to start searching
        if (nsParts.isEmpty())
        {
            level = ctx.getNamespace();
        }
        else
        {
            if (nsParts.peek() == Namespace.ABSOLUTE_MARKER)
            {
                level = ctx.getScript().getGlobalNamespace();

                // Only clone when mutability is needed
                nsParts = (LinkedList<String>) nsParts.clone();
                nsParts.poll();
            }
            else
            {
                level = ctx.getNamespace();
            }
        }

        Identifier id;

        // Looking for namespaces and values in them
        while (level != null)
        {
            n = level.findWithin(nsParts);

            // If a namespace exists by that path, search it
            if (n != null)
            {
                for (Map.Entry<Identifier, T> entry: identifiable.entrySet())
                {
                    id = entry.getKey();

                    // Same namespace share the same pointers
                    if (id.getNamespace() == n && id.getName().equals(name))
                    {
                        return entry;
                    }
                }
            }

            level = level.getParent(); // Ascending
        }

        return null;
    }

    public boolean containsKey(Identifier id)
    {
        return identifiable.containsKey(id);
    }

    public Object get(Identifier identifier, Object defaultValue)
    {
        Object rv = identifiable.get(identifier);

        if (rv != null || identifiable.containsKey(identifier))
        {
            return rv;
        }

        return defaultValue;
    }

    public T set(Identifier identifier, T value)
    {
        identifiable.put(identifier, value);
        return value;
    }

    public void forEach(BiConsumer<Identifier, T> action) {
        identifiable.forEach(action);
    }

    @Override
    public Lookup<T> clone()
    {
        return new Lookup<>(identifiable);
    }

    @Override
    public Iterator<Entry<Identifier, T>> iterator()
    {
        return identifiable.entrySet().iterator();
    }

}
