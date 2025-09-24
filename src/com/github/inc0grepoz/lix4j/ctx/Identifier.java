package com.github.inc0grepoz.lix4j.ctx;

public class Identifier
{

    public static Identifier of(Namespace namespace, String name, int data)
    {
        return new Identifier(namespace, name, data);
    }

    public static Identifier of(Namespace namespace, String name)
    {
        return new Identifier(namespace, name, 0);
    }

    final Namespace namespace;
    final String name;
    final int data, hash;

    Identifier(Namespace namespace, String name, int data)
    {
        this.namespace = namespace;
        this.name = name;
        this.data = data;

        hash = calculateHash(data);
    }

    public Identifier withData(int data)
    {
        return new Identifier(namespace, name, data);
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
        return namespace + "::" + (data == 0 ? name : name + "$" + data);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj == null) return false;

        return obj.getClass() == Identifier.class
            && obj.hashCode() == hash;
    }

    @Override
    public int hashCode()
    {
        return hash;
    }

    private int calculateHash(int data)
    {
        return 961 * namespace.hashCode() + 31 * name.hashCode() + data;
    }

}
