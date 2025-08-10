package com.github.inc0grepoz.lix4j.ctx;

public class IdentifierAccessor extends Identifier
{

    public static IdentifierAccessor of(Namespace namespaceDeclared, Namespace namespaceLocation, String name)
    {
        return new IdentifierAccessor(namespaceDeclared, namespaceLocation, name);
    }

    final Namespace namespaceDeclared;

    private IdentifierAccessor(Namespace namespaceDeclared, Namespace namespaceLocation, String name)
    {
        super(namespaceLocation, name);
        this.namespaceDeclared = namespaceDeclared;
    }

    public Namespace getNamespaceDeclared()
    {
        return namespaceDeclared;
    }

    @Override
    public String toString()
    {
        return !namespaceDeclared.isGlobal() ? namespaceDeclared + "::" + name
             : !namespace.isGlobal() ? namespace + "::" + name
             : name;
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
            if (oid.hash != hash)
            {
                return false;
            }

            return oid.namespace.isGlobal() && namespaceDeclared.isGlobal()
                || namespace.isGlobal() && oid.namespaceDeclared.isGlobal()
                || oid.namespace.equals(namespace);
        }

        return false;
    }

}
