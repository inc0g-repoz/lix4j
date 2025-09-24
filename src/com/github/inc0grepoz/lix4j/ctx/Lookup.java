package com.github.inc0grepoz.lix4j.ctx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * A table for searching objects by identifiers with namespaces.
 * 
 * @param <T> the type of elements stored by a table
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

    /**
     * Finds an entry by parts of the namespace used by the identifier.
     * Starts searching from the current namespace for relative paths
     * or from the global namespace for abstract paths (starting with
     * a "::" separator).
     * 
     * @param namespaceParts   the parts of nested namespaces
     * @param name             the name used by the identifier
     * @param namespaceGlobal  the global namespace
     * @param namespaceCurrent the current namespace
     * @param predicate        the predicate to test values
     * @return a found entry or {@code null}
     */
    public Entry<Identifier, T> findEntry(LinkedList<String> namespaceParts, String name,
            Namespace namespaceGlobal, Namespace namespaceCurrent, Predicate<T> predicate)
    {
        Namespace level, n;

        // Determining the namespace to start searching from
        if (namespaceParts.isEmpty())
        {
            // A relative path with no namespace specified – starting from the current
            level = namespaceCurrent;
        }
        else
        {
            if (namespaceParts.peek().equals(Namespace.ABSOLUTE_MARKER))
            {
                // An absolute path - starting from the global
                level = namespaceGlobal;

                // Cloning the list to not mutate the original
                namespaceParts = (LinkedList<String>) namespaceParts.clone();
                namespaceParts.poll(); // Removing the absolute path marker
            }
            else
            {
                // A relative path specified - starting from the current
                level = namespaceCurrent;
            }
        }

        Identifier id;

        // Looking through namespaces starting from the bottom level
        // until the the top level is reached (global)
        while (level != null)
        {
            // Trying to find the target namespace in the current level
            n = level.findWithin(namespaceParts);

            // If found the namespace, looking for an identifier
            if (n != null)
            {
                for (Map.Entry<Identifier, T> entry: identifiable.entrySet())
                {
                    id = entry.getKey();

                    // Comparing by a pointer to the namespace and a name
                    if (id.getNamespace() == n
                            && id.getName().equals(name)
                            && predicate.test(entry.getValue()))
                    {
                        return entry;
                    }
                }
            }

            level = level.getParent(); // Going to the parent namespace
        }

        return null; // Nothing found
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

    public void forEach(BiConsumer<Identifier, T> action)
    {
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
