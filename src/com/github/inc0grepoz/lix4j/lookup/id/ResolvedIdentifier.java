package com.github.inc0grepoz.lix4j.lookup.id;

import com.github.inc0grepoz.lix4j.lookup.Namespace;

/**
 * Represents an identifier in the namespace system, which can be either resolved or unresolved.
 * Identifiers combine a namespace, name, and optional data for disambiguation.
 * Supports resolution from unresolved to resolved state for namespace lookup.
 * <p>
 * Example:
 * <blockquote><pre>
 * // Create a resolved identifier
 * Namespace mathNs = new Namespace(null, "math");
 * Identifier resolvedId = Identifier.resolved(mathNs, "sqrt");
 * 
 * // Create an unresolved identifier
 * LinkedList<String> targetPath = new LinkedList<>(Arrays.asList("math", "utils"));
 * Identifier unresolvedId = Identifier.unresolved(targetPath, currentNs, "calculate");
 * unresolvedId.resolve(targetNamespace); // Resolve when target namespace is found
 * </pre></blockquote>
 * 
 * @author inc0g-repoz
 */
public final class ResolvedIdentifier implements Identifier
{

    private final Namespace namespace;
    private final String name;
    private final int data, hash;

    ResolvedIdentifier(Namespace namespace, String name, int data)
    {
        this.namespace = namespace;
        this.name = name;
        this.data = data;

        hash = 961 * namespace.hashCode() + 31 * name.hashCode() + data;
    }

    @Override
    public Namespace getNamespace()
    {
        return namespace;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public int getData()
    {
        return data;
    }

    @Override
    public ResolvedIdentifier withData(int data)
    {
        return new ResolvedIdentifier(namespace, name, data);
    }

    @Override
    public ResolvedIdentifier resolve(Namespace namespace)
    {
        throw new RuntimeException("Already resolved");
    }

    @Override
    public boolean isResolved()
    {
        return true;
    }

    @Override
    public String toString()
    {
        return namespace + Namespace.SEPARATOR + (data == 0 ? name : name + "$" + data);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj == null) return false;

        if (obj.getClass() != ResolvedIdentifier.class)
        {
            return false;
        }

        return ((ResolvedIdentifier) obj).hash == hash;
    }

    @Override
    public int hashCode()
    {
        return hash;
    }

}
