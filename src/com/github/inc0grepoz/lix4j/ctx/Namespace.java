package com.github.inc0grepoz.lix4j.ctx;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

import com.github.inc0grepoz.lix4j.exception.SyntaxError;

@SuppressWarnings("unchecked")
public class Namespace
{

    public static final String SEPARATOR = "::";
    public static final String GLOBAL_NAME = "";
    public static final String ABSOLUTE_MARKER = "ABSOLUTE";

    private final Namespace parent;
    private final String name;
    private final Set<Namespace> nested = new HashSet<>();
    private final int hash, level;

    public Namespace(Namespace parent, String name)
    {
        this.parent = parent;
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.hash = calculateHash();

        if (parent != null)
        {
            if (parent.nested.stream().anyMatch(n -> n.name.equals(name)))
            {
                throw new IllegalArgumentException("Namespace already exists: " + name);
            }

            parent.nested.add(this);
            level = parent.level + 1;
        }
        else
        {
            level = 0;
        }
    }

    public Namespace getParent()
    {
        return parent;
    }

    private Namespace getGlobal()
    {
        return parent == null ? this : parent.getGlobal();
    }

    public boolean isGlobal()
    {
        return parent == null;
    }

    public String getName()
    {
        return name;
    }

    /**
     * Finds a direct nested namespace by a name.
     *
     * @param name the namespace name
     * @return a found namespace or {@code null}
     */
    private Namespace get(String name)
    {
        for (Namespace n: nested)
        {
            if (n.name.equals(name))
            {
                return n;
            }
        }

        return null;
    }

    public Namespace nest(String name)
    {
        Namespace n = get(name);
        return n == null ? new Namespace(this, name) : n;
    }

    public Namespace nestAll(LinkedList<String> parts)
    {
        if (parts.isEmpty())
        {
            return this;
        }

        Namespace n = this;

        for (String name: parts)
        {
            if (name == ABSOLUTE_MARKER)
            {
                if (n.isGlobal())
                {
                    n = getGlobal();
                }
                else
                {
                    throw new SyntaxError("Absolute namespace marker can only be in the path prefix");
                }

                n = n.nest(name);
            }
        }

        return n;
    }

    public Namespace find(LinkedList<String> parts)
    {
        if (parts.isEmpty())
        {
            return this;
        }

        Namespace n = this, level = this;

        if (parts.peek() == ABSOLUTE_MARKER)
        {
            parts = (LinkedList<String>) parts.clone();
            parts.poll();

            n = n.getGlobal();
        }

        do
        {
            n = level.findWithin(parts);

            if (n != null)
            {
                return n;
            }

            level = level.parent;
        }
        while (level != null);

        return null;
    }

    /**
     * Finds a nested namespace by a full path. Goes through the whole
     * hierarchy of namespace until it finds the target namespace.
     *
     * @param parts the parts of the path to a certain namespace
     * @return the found namespace or {@code null}
     */
    public Namespace findWithin(LinkedList<String> parts)
    {
        Namespace current = this;

        // Sequentially walking through all the parts.
        // If some part is not found, null is returned.
        for (String part: parts) {
            if ((current = current.get(part)) == null) return null;
        }

        return current; // Returning the namespace found
    }

    @Override
    public String toString()
    {
        return parent == null ? "GLOBAL" : parent + SEPARATOR + name;
    }

    public LinkedList<String> toLinkedList()
    {
        LinkedList<String> list = new LinkedList<>();
        Namespace n = this;

        while (n.parent != null)
        {
            list.addFirst(n.name);
            n = n.parent;
        }

        return list;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;

        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }

        Namespace other = (Namespace) obj;

        return Objects.equals(name, other.name) 
               && Objects.equals(parent, other.parent);
    }

    @Override
    public int hashCode()
    {
        return hash;
    }

    private int calculateHash()
    {
        int hash = name.hashCode();
        Namespace current = parent;

        while (current != null)
        {
            hash = 31 * hash + current.name.hashCode();
            current = current.parent;
        }

        return hash;
    }

}
