package com.github.inc0grepoz.lix4j.value;

import com.github.inc0grepoz.lix4j.util.Namespace;

/**
 * Represents an accessor for variables.
 * 
 * @author inc0g-repoz
 */
public abstract class AccessorVariable extends AccessorNamespaced
{

    AccessorVariable(String namespace, String name)
    {
        super(namespace, name);
    }

    @Override
    public String toString()
    {
        return "$" + (namespace == Namespace.GLOBAL ? "" : namespace + "::") + name + (next == null ? "" : "." + next);
    }

}
