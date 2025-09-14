package com.github.inc0grepoz.lix4j.ctx;

public class Identifier
{

    public static Identifier of(Namespace namespace, String name)
    {
        return new Identifier(namespace, name);
    }

    final Namespace namespace;
    final String name;
    final int hash;

    Identifier(Namespace namespace, String name)
    {
        this.namespace = namespace;
        this.name = name;

        hash = name.hashCode();
    }

    public Namespace getNamespace()
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
        return namespace.isGlobal() ? name : namespace + "::" + name;
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
        if (obj == null) return false;

        Class<?> clazz = obj.getClass();

        if (clazz == Identifier.class)
        {
            Identifier oid = (Identifier) obj;
            return oid.hash == hash ? oid.namespace == namespace : false;
        }

        return false;
    }

}
