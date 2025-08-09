package com.github.inc0grepoz.lix4j.ctx;

import com.github.inc0grepoz.lix4j.util.Namespace;

public class Identifier
{

    private final String namespace, name;
    private final int hash;

    public Identifier(String namespace, String name)
    {
        this.namespace = namespace;
        this.name = name;

        hash = 31 * namespace.hashCode() + name.hashCode();
    }

    public String getNamespace()
    {
        return namespace;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return namespace == Namespace.GLOBAL ? name : namespace + "::" + name;
    }

    @Override
    public int hashCode()
    {
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        Identifier oid = (Identifier) obj;
        return oid.hash == hash;
    }

}
