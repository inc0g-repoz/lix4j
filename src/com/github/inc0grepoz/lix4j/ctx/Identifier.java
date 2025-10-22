package com.github.inc0grepoz.lix4j.ctx;

import java.util.LinkedList;

public class Identifier
{

    public static Identifier resolved(Namespace namespace, String name, int data)
    {
        return new Identifier(null, namespace, name, data);
    }

    public static Identifier resolved(Namespace namespace, String name)
    {
        return new Identifier(null, namespace, name, 0);
    }

    public static Identifier unresolved(LinkedList<String> unresolvedTargetNamespace,
            Namespace namespace, String name, int data)
    {
        return new Identifier(unresolvedTargetNamespace, namespace, name, data);
    }

    public static Identifier unresolved(LinkedList<String> unresolvedTargetNamespace,
            Namespace namespace, String name)
    {
        return new Identifier(unresolvedTargetNamespace, namespace, name, 0);
    }

    private final String name;
    private final int data;

    private LinkedList<String> unresolvedTargetNamespace;
    private Namespace namespace;
    private int hash;

    private Identifier(LinkedList<String> unresolvedTargetNamespace, Namespace namespace,
            String name, int data)
    {
        this.unresolvedTargetNamespace = unresolvedTargetNamespace;
        this.namespace = namespace;
        this.name = name;
        this.data = data;

        hash = calculateHash(data, isResolved());
    }

    public LinkedList<String> getUnresolvedTargetNamespace()
    {
        return unresolvedTargetNamespace;
    }

    public Namespace getNamespace()
    {
        return namespace;
    }

    public String getName()
    {
        return name;
    }

    public int getData()
    {
        return data;
    }

    public Identifier withData(int data)
    {
        return new Identifier(unresolvedTargetNamespace, namespace, name, data);
    }

    public boolean isResolved()
    {
        return unresolvedTargetNamespace == null;
    }

    public void resolve(Namespace namespace)
    {
        this.namespace = namespace;
        unresolvedTargetNamespace = null;
        hash = calculateHash(data, true);
    }

    @Override
    public String toString()
    {
        return namespace + Namespace.SEPARATOR + (data == 0 ? name : name + "$" + data);
    }

    @Override
    public boolean equals(Object obj)
    {
        ensureResolved();

        if (obj == this) return true;
        if (obj == null) return false;

        if (obj.getClass() != Identifier.class)
        {
            return false;
        }

        Identifier that = (Identifier) obj;
        return that.isResolved() == isResolved() && that.hash == hash;
    }

    @Override
    public int hashCode()
    {
        return hash;
    }

    private int calculateHash(int data, boolean resolved)
    {
        int h = 961 * namespace.hashCode() + 31 * name.hashCode() + data;

        if (!resolved)
        {
            h += 29791 * unresolvedTargetNamespace.hashCode();
        }

        return h;
    }

    private void ensureResolved()
    {
        if (!isResolved())
        {
            throw new IllegalStateException("Unresolved identifier " + toString());
        }
    }

}
