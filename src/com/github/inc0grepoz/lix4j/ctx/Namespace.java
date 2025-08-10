package com.github.inc0grepoz.lix4j.ctx;

import java.util.HashSet;
import java.util.Set;

public class Namespace
{

    public static final String SEPARATOR = "::";

    private static int calcHash(Namespace parent, String name)
    {
        int hash = name.hashCode();

        while (parent != null)
        {
            hash = 31 * hash + parent.name.hashCode();
            parent = parent.parent;
        }

        return hash;
    }

    private final Namespace parent;
    private final String name;
    private final Set<Namespace> nested = new HashSet<>();
    private final int hash;

    public Namespace(Namespace parent, String name)
    {
        if (parent != null)
        {
            parent.nested.add(this);
        }

        hash = calcHash(this.parent = parent, this.name = name);
    }

    public Namespace getGlobal()
    {
        return parent == null ? this : parent.getGlobal();
    }

    public Namespace getParent()
    {
        return parent;
    }

    public boolean isGlobal()
    {
        return parent == null;
    }

    public Namespace nest(String name)
    {
        for (Namespace n: nested)
        {
            if (name.equals(n.name))
            {
                return n;
            }
        }

        return new Namespace(this, name);
    }

    @Override
    public String toString()
    {
        return parent == null || parent.isGlobal() ? name : parent + SEPARATOR + name;
    }

    @Override
    public int hashCode()
    {
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;

        if (obj == null || obj.getClass() != Namespace.class)
        {
            return false;
        }

        Namespace ons = (Namespace) obj;
        return ons.hash == hash;
    }

}
