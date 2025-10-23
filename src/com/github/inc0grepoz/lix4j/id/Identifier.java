package com.github.inc0grepoz.lix4j.id;

import java.util.LinkedList;

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
public class Identifier
{

    /**
     * Creates a resolved identifier with the specified namespace, name, and data.
     * Resolved identifiers have their target namespace already determined.
     * <p>
     * Example:
     * <blockquote><pre>
     * Identifier id = Identifier.resolved(mathNamespace, "max", 2);
     * // Creates identifier for math::max$2
     * </pre></blockquote>
     *
     * @param namespace the resolved target namespace
     * @param name the identifier name
     * @param data additional data for disambiguation (e.g., parameter count)
     * @return a new resolved identifier
     */
    public static Identifier resolved(Namespace namespace, String name, int data)
    {
        return new Identifier(null, namespace, name, data);
    }

    /**
     * Creates a resolved identifier with the specified namespace and name, with zero data.
     *
     * @param namespace the resolved target namespace
     * @param name the identifier name
     * @return a new resolved identifier with data = 0
     */
    public static Identifier resolved(Namespace namespace, String name)
    {
        return new Identifier(null, namespace, name, 0);
    }

    /**
     * Creates an unresolved identifier with a target namespace path to be resolved later.
     * Unresolved identifiers contain a path that will be used to find the target namespace.
     * <p>
     * Example:
     * <blockquote><pre>
     * LinkedList<String> targetPath = new LinkedList<>(Arrays.asList("math", "utils"));
     * Identifier id = Identifier.unresolved(targetPath, currentNamespace, "calculate", 3);
     * // Identifier will be resolved later when the target namespace math::utils is found
     * </pre></blockquote>
     *
     * @param unresolvedTargetNamespace the path to the target namespace
     * @param namespace the current namespace context
     * @param name the identifier name
     * @param data additional data for disambiguation
     * @return a new unresolved identifier
     */
    public static Identifier unresolved(LinkedList<String> unresolvedTargetNamespace,
            Namespace namespace, String name, int data)
    {
        return new Identifier(unresolvedTargetNamespace, namespace, name, data);
    }

    /**
     * Creates an unresolved identifier with zero data.
     *
     * @param unresolvedTargetNamespace the path to the target namespace
     * @param namespace the current namespace context
     * @param name the identifier name
     * @return a new unresolved identifier with data = 0
     */
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

    /**
     * Gets the unresolved target namespace path for this identifier.
     * Only available for unresolved identifiers.
     *
     * @return the linked list representing the target namespace path, or null if resolved
     */
    public LinkedList<String> getUnresolvedTargetNamespace()
    {
        return unresolvedTargetNamespace;
    }

    /**
     * Gets the namespace associated with this identifier.
     * For resolved identifiers, this is the target namespace.
     * For unresolved identifiers, this is the current namespace context.
     *
     * @return the namespace associated with this identifier
     */
    public Namespace getNamespace()
    {
        return namespace;
    }

    /**
     * Gets the name of this identifier.
     *
     * @return the identifier name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the additional data associated with this identifier.
     * Typically used for disambiguation (e.g., function parameter count).
     *
     * @return the identifier data
     */
    public int getData()
    {
        return data;
    }

    /**
     * Creates a new identifier with the same properties but different data.
     * <p>
     * Example:
     * <blockquote><pre>
     * Identifier original = Identifier.resolved(ns, "func", 0);
     * Identifier withData = original.withData(2);
     * // Creates new identifier for same function but with parameter count 2
     * </pre></blockquote>
     *
     * @param data the new data value
     * @return a new identifier with the specified data
     */
    public Identifier withData(int data)
    {
        return new Identifier(unresolvedTargetNamespace, namespace, name, data);
    }

    /**
     * Checks if this identifier is resolved (has a determined target namespace).
     *
     * @return true if the identifier is resolved, false if unresolved
     */
    public boolean isResolved()
    {
        return unresolvedTargetNamespace == null;
    }

    /**
     * Resolves this identifier by setting its target namespace and clearing the unresolved path.
     * Converts an unresolved identifier to a resolved one.
     * <p>
     * Example:
     * <blockquote><pre>
     * Identifier unresolved = Identifier.unresolved(targetPath, currentNs, "function");
     * Namespace targetNs = currentNs.find(targetPath);
     * unresolved.resolve(targetNs);
     * // Identifier is now resolved with targetNs as its namespace
     * </pre></blockquote>
     *
     * @param namespace the resolved target namespace
     * @throws IllegalStateException if the identifier is already resolved
     */
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
