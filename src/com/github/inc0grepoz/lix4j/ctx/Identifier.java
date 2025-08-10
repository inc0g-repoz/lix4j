package com.github.inc0grepoz.lix4j.ctx;

import com.github.inc0grepoz.lix4j.util.Namespace;

public class Identifier
{

    public static Identifier of(String namespace, String name)
    {
        return new Identifier(namespace, name);
    }

    final String namespace, name;
    final int hash;

    Identifier(String namespace, String name)
    {
        this.namespace = namespace;
        this.name = name;

        hash = name.hashCode();
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
        if (obj == this) return true;
        if (obj == null) return false;

        Class<?> clazz = obj.getClass();

        if (clazz == Identifier.class)
        {
            // Compare both identifiers namespaces
            Identifier oid = (Identifier) obj;
            return oid.hash == hash ? oid.namespace.equals(namespace) : false;
        }

        if (clazz == IdentifierAccessor.class)
        {
            // Compare two namespaces of each identifier
            IdentifierAccessor oid = (IdentifierAccessor) obj;
            return oid.hash == hash ? oid.namespace.equals(namespace)
                || oid.namespaceDeclared.equals(namespace) : false;
        }

        return false;
    }

}
