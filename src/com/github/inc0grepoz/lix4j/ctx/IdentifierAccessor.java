package com.github.inc0grepoz.lix4j.ctx;

import com.github.inc0grepoz.lix4j.util.Namespace;

public class IdentifierAccessor extends Identifier
{

    public static IdentifierAccessor of(String namespaceDeclared, String namespaceLocation, String name)
    {
        return new IdentifierAccessor(namespaceDeclared, namespaceLocation, name);
    }

    final String namespaceDeclared;

    private IdentifierAccessor(String namespaceDeclared, String namespaceLocation, String name)
    {
        super(namespaceLocation, name);
        this.namespaceDeclared = namespaceDeclared;
    }

    public String getNamespaceDeclared()
    {
        return namespaceDeclared;
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
            return oid.hash == hash ? oid.namespace.equals(namespace)
                || oid.namespace.equals(namespaceDeclared) : false;
        }

        if (clazz == IdentifierAccessor.class)
        {
            // Compare two namespaces of each identifier
            IdentifierAccessor oid = (IdentifierAccessor) obj;
            return oid.hash == hash
                 ? oid.namespace == Namespace.GLOBAL && namespaceDeclared == Namespace.GLOBAL
                || namespace == Namespace.GLOBAL && oid.namespaceDeclared == Namespace.GLOBAL
                || oid.namespace.equals(namespace)
                 : false;
        }

        return false;
    }

}
