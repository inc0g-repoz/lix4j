package com.github.inc0grepoz.lix4j.lookup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.github.inc0grepoz.lix4j.lookup.id.ResolvedIdentifier;

/**
 * A table for searching objects by identifiers with namespaces.
 * 
 * @param <T> the type of elements stored by a table
 */
@SuppressWarnings("unchecked")
public class Lookup<T> implements Cloneable, Iterable<Entry<ResolvedIdentifier, T>>
{

    private final Map<ResolvedIdentifier, T> identifiable;

    public Lookup()
    {
        identifiable = new HashMap<>();
    }

    private Lookup(Map<ResolvedIdentifier, T> identifiable)
    {
        (this.identifiable = new HashMap<>()).putAll(identifiable);
    }

    /**
     * Finds an entry by parts of the namespace used by the identifier.
     * Starts searching from the current namespace for relative paths
     * or from the global namespace for abstract paths (starting with
     * a "::" separator).
     * 
     * @param identifier       the identifier
     * @param namespaceGlobal  the global namespace
     * @param predicate        the predicate to test values
     * @return a found entry or {@code null}
     */
    public Entry<ResolvedIdentifier, T> findEntry(ResolvedIdentifier identifier, Namespace namespaceGlobal,
            Predicate<T> predicate)
    {
        if (identifier.isResolved())
        {
            T value = identifiable.get(identifier);
            return value == null ? null : new LookupEntry<T>(identifier, value);
        }

        LinkedList<String> target = identifier.getUnresolvedTargetNamespace();
        Namespace level, n = identifier.getNamespace();
        String name = identifier.getName();

        // Determining the namespace to start searching from
        if (target.isEmpty())
        {
            // A relative path with no namespace specified - starting from the current
            level = n;
        }
        else
        {
            if (target.peek().equals(Namespace.ABSOLUTE_MARKER)) // equals?
            {
                // An absolute path - starting from the global
                level = namespaceGlobal;

                // Cloning the list to not mutate the original
                target = (LinkedList<String>) target.clone();
                target.poll(); // Removing the absolute path marker
            }
            else
            {
                // A relative path specified - starting from the current
                level = n;
            }
        }

        ResolvedIdentifier idi;

        // Looking through namespaces starting from the bottom level
        // until the the top level is reached (global)
        while (level != null)
        {
            // Trying to find the target namespace in the current level
            n = level.findWithin(target);

            // If found the namespace, looking for an identifier
            if (n != null)
            {
                for (Map.Entry<ResolvedIdentifier, T> entry: identifiable.entrySet())
                {
                    idi = entry.getKey();

                    if (idi.getNamespace() == n
                            && idi.getName().equals(name)
                            && predicate.test(entry.getValue()))
                    {
                        identifier.resolve(idi.getNamespace());
                        return entry;
                    }
                }
            }

            level = level.getParent(); // Going to the parent namespace
        }

        return null; // Nothing found
    }

    public boolean containsKey(ResolvedIdentifier id)
    {
        return identifiable.containsKey(id);
    }

    public Object get(ResolvedIdentifier identifier, Object defaultValue)
    {
        Object rv = identifiable.get(identifier);

        if (rv != null || identifiable.containsKey(identifier))
        {
            return rv;
        }

        return defaultValue;
    }

    public T set(ResolvedIdentifier identifier, T value)
    {
        identifiable.put(identifier, value);
        return value;
    }

    public void forEach(BiConsumer<ResolvedIdentifier, T> action)
    {
        identifiable.forEach(action);
    }

    @Override
    public Lookup<T> clone()
    {
        return new Lookup<>(identifiable);
    }

    @Override
    public Iterator<Entry<ResolvedIdentifier, T>> iterator()
    {
        return identifiable.entrySet().iterator();
    }

    public static class LookupEntry<T> implements Entry<ResolvedIdentifier, T>
    {

        private final ResolvedIdentifier key;
        private final T value;

        LookupEntry(ResolvedIdentifier key, T value)
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public ResolvedIdentifier getKey()
        {
            return key;
        }

        @Override
        public T getValue()
        {
            return value;
        }

        @Override
        public T setValue(T value)
        {
            throw new UnsupportedOperationException("Immutable entry: " + key);
        }

    }

}
