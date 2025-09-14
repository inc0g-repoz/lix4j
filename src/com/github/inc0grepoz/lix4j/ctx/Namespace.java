package com.github.inc0grepoz.lix4j.ctx;

import java.util.HashSet;
import java.util.Iterator;
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
        Namespace n = this, level = this;

        if (parts.isEmpty())
        {
            return this;
        }
        if (parts.peek() == ABSOLUTE_MARKER)
        {
            parts = (LinkedList<String>) parts.clone();
            parts.poll();

            n = n.getGlobal();
        }

        do
        {
            n = n.findWithin(parts);

            if (n != null)
            {
                return n;
            }

            level = level.parent;
        }
        while (level != null);

        return null;
    }

    public Namespace findWithin(LinkedList<String> parts)
    {
        Namespace n = this;
        Iterator<String> iter = parts.iterator();

        while (iter.hasNext())
        {
            n = n.get(iter.next());

            if (n != null)
            {
                return n;
            }
        }

        return null;
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
            list.addLast(n.name);
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
