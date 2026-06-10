package com.github.inc0grepoz.lix4j.lookup;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

import com.github.inc0grepoz.lix4j.exception.SyntaxError;

/**
 * Represents a namespace in a hierarchical naming system.
 * Supports nested namespaces, absolute/relative paths, and global namespace management.
 * Namespaces are organized in a tree structure with the global namespace at the root.
 * <p>
 * Example:
 * <blockquote><pre>
 * Namespace global = new Namespace(null, "");
 * Namespace math = global.nest("math");
 * Namespace utils = math.nest("utils");
 * // Creates namespace hierarchy: global::math::utils
 * </pre></blockquote>
 * 
 * @author inc0g-repoz
 */
@SuppressWarnings("unchecked")
public class Namespace
{

    public static final String SEPARATOR = "::";
    public static final String GLOBAL_NAME = "";
    public static final String ABSOLUTE_MARKER = "ABSOLUTE";

    private final Namespace parent;
    private final String name;
    private final Set<Namespace> nested = new HashSet<>();
    private final int hash, level;

    public Namespace(Namespace parent, String name)
    {
        this.parent = parent;
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.hash = calculateHash();

        if (parent != null)
        {
            if (parent.nested.stream().anyMatch(n -> n.name.equals(name)))
            {
                throw new IllegalArgumentException("Namespace already exists: " + name);
            }

            parent.nested.add(this);
            level = parent.level + 1;
        }
        else
        {
            level = 0;
        }
    }

    /**
     * Gets the parent namespace of this namespace.
     *
     * @return the parent namespace, or null if this is the global namespace
     */
    public Namespace getParent()
    {
        return parent;
    }

    /**
     * Returns the top level namespace in the hierarchy where
     * this namespace is located.
     *
     * @return the top level namespace
     */
    private Namespace getGlobal()
    {
        return parent == null ? this : parent.getGlobal();
    }

    /**
     * Checks if this namespace is the global namespace.
     *
     * @return true if this namespace has no parent (is the root namespace)
     */
    public boolean isGlobal()
    {
        return parent == null;
    }

    /**
     * Gets the name of this namespace.
     *
     * @return the name of this namespace
     */
    public String getName()
    {
        return name;
    }

    /**
     * Finds a direct nested namespace by a name.
     *
     * @param name the namespace name
     * @return a found namespace or {@code null}
     */
    private Namespace get(String name)
    {
        for (Namespace n: nested)
        {
            if (n.name.equals(name))
            {
                return n;
            }
        }

        return null;
    }

    /**
     * Creates a new nested namespace with the specified name, or returns existing one.
     * If a namespace with the given name already exists as a direct child, returns it.
     * Otherwise, creates and returns a new nested namespace.
     * <p>
     * Example:
     * <blockquote><pre>
     * Namespace parent = new Namespace(null, "parent");
     * Namespace child1 = parent.nest("child");
     * Namespace child2 = parent.nest("child"); // Returns existing child1
     * // child1 == child2
     * </pre></blockquote>
     *
     * @param name the name for the nested namespace
     * @return the existing or newly created nested namespace
     */
    public Namespace nest(String name)
    {
        Namespace n = get(name);
        return n == null ? new Namespace(this, name) : n;
    }

    /**
     * Creates or navigates through a hierarchy of nested namespaces based on the path parts.
     * Processes each part sequentially, creating namespaces as needed.
     * <p>
     * Example:
     * <blockquote><pre>
     * Namespace global = new Namespace(null, "");
     * LinkedList<String> path = new LinkedList<>(Arrays.asList("a", "b", "c"));
     * Namespace result = global.nestAll(path);
     * // Creates hierarchy: global::a::b::c
     * </pre></blockquote>
     *
     * @param parts the linked list of namespace names to nest
     * @return the innermost namespace in the created hierarchy
     * @throws SyntaxError if absolute namespace marker is used incorrectly
     */
    public Namespace nestAll(LinkedList<String> parts)
    {
        if (parts.isEmpty())
        {
            return this;
        }

        Namespace n = this;

        for (String name: parts)
        {
            if (name == ABSOLUTE_MARKER)
            {
                if (n.isGlobal())
                {
                    n = getGlobal();
                }
                else
                {
                    throw new SyntaxError("Absolute namespace marker can only be in the path prefix");
                }

                n = n.nest(name);
            }
        }

        return n;
    }

    /**
     * Finds a namespace by following the specified path parts.
     * Searches recursively through parent namespaces if not found in current context.
     * Supports absolute paths using the ABSOLUTE_MARKER.
     * <p>
     * Example:
     * <blockquote><pre>
     * Namespace global = new Namespace(null, "");
     * Namespace a = global.nest("a");
     * Namespace b = a.nest("b");
     * 
     * LinkedList<String> path1 = new LinkedList<>(Arrays.asList("a", "b"));
     * Namespace found1 = global.find(path1); // Returns b
     * 
     * LinkedList<String> path2 = new LinkedList<>(Arrays.asList("b"));
     * Namespace found2 = a.find(path2); // Returns b (searches upward)
     * </pre></blockquote>
     *
     * @param parts the path parts to follow
     * @return the found namespace, or null if not found
     */
    public Namespace find(LinkedList<String> parts)
    {
        if (parts.isEmpty())
        {
            return this;
        }

        Namespace n = this, level = this;

        if (parts.peek() == ABSOLUTE_MARKER)
        {
            parts = (LinkedList<String>) parts.clone();
            parts.poll();

            n = n.getGlobal();
        }

        do
        {
            n = level.findWithin(parts);

            if (n != null)
            {
                return n;
            }

            level = level.parent;
        }
        while (level != null);

        return null;
    }

    /**
     * Finds a namespace within the current namespace hierarchy without searching parents.
     * Only searches downward from the current namespace.
     * <p>
     * Example:
     * <blockquote><pre>
     * Namespace global = new Namespace(null, "");
     * Namespace a = global.nest("a");
     * Namespace b = a.nest("b");
     * 
     * LinkedList<String> path = new LinkedList<>(Arrays.asList("a", "b"));
     * Namespace found = global.findWithin(path); // Returns b
     * Namespace notFound = b.findWithin(path); // Returns null (searches downward only)
     * </pre></blockquote>
     *
     * @param parts the path parts to follow within current hierarchy
     * @return the found namespace, or null if not found
     */
    public Namespace findWithin(LinkedList<String> parts)
    {
        Namespace current = this;

        // Sequentially walking through all the parts.
        // If some part is not found, null is returned.
        for (String part: parts) {
            if ((current = current.get(part)) == null) return null;
        }

        return current; // Returning the namespace found
    }

    @Override
    public String toString()
    {
        return parent == null ? "GLOBAL" : parent + SEPARATOR + name;
    }

    /**
     * Converts the namespace path to a linked list of names.
     * The list starts with the top-level namespace name and ends
     * with this namespace's name.
     * <p>
     * Example:
     * <blockquote><pre>
     * Namespace global = new Namespace(null, "");
     * Namespace math = global.nest("math");
     * Namespace utils = math.nest("utils");
     * LinkedList<String> path = utils.toLinkedList();
     * // path contains ["math", "utils"]
     * </pre></blockquote>
     *
     * @return a linked list representing the namespace hierarchy
     */
    public LinkedList<String> toLinkedList()
    {
        LinkedList<String> list = new LinkedList<>();
        Namespace n = this;

        while (n.parent != null)
        {
            list.addFirst(n.name);
            n = n.parent;
        }

        return list;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;

        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }

        Namespace other = (Namespace) obj;

        return Objects.equals(name, other.name) 
               && Objects.equals(parent, other.parent);
    }

    @Override
    public int hashCode()
    {
        return hash;
    }

    /**
     * Calculates the hash code for this instance.
     * 
     * @return a hash code
     */
    private int calculateHash()
    {
        int hash = name.hashCode();
        Namespace current = parent;

        while (current != null)
        {
            hash = 31 * hash + current.name.hashCode();
            current = current.parent;
        }

        return hash;
    }

}
