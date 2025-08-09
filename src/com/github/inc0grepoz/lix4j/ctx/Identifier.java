package com.github.inc0grepoz.lix4j.ctx;

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
        return namespace + "::" + name;
    }

    @Override
    public int hashCode()
    {
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        return this == obj || obj != null
                && getClass() == obj.getClass()
                && ((Identifier) obj).hash == hash;
    }

}
