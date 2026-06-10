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
public interface Identifier {

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
    public static ResolvedIdentifier resolved(Namespace namespace, String name, int data)
    {
        return new ResolvedIdentifier(namespace, name, data);
    }

    /**
     * Creates a resolved identifier with the specified namespace and name, with zero data.
     *
     * @param namespace the resolved target namespace
     * @param name the identifier name
     * @return a new resolved identifier with data = 0
     */
    public static ResolvedIdentifier resolved(Namespace namespace, String name)
    {
        return new ResolvedIdentifier(namespace, name, 0);
    }

    /**
     * Gets the namespace associated with this identifier.
     * For resolved identifiers, this is the target namespace.
     * For unresolved identifiers, this is the current namespace context.
     *
     * @return the namespace associated with this identifier
     */
    Namespace getNamespace();

    /**
     * Gets the name of this identifier.
     *
     * @return the identifier name
     */
    String getName();

    /**
     * Gets the additional data associated with this identifier.
     * Typically used for disambiguation (e.g., function parameter count).
     *
     * @return the identifier data
     */
    int getData();

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
    Identifier withData(int data);

    /**
     * Checks if this identifier is resolved (has a determined target namespace).
     *
     * @return true if the identifier is resolved, false if unresolved
     */
    boolean isResolved();

    Identifier resolve(Namespace namespace);

}
