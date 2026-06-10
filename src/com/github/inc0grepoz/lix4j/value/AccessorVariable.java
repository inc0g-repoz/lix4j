package com.github.inc0grepoz.lix4j.value;

import com.github.inc0grepoz.lix4j.lookup.id.ResolvedIdentifier;

/**
 * Represents an accessor for variables.
 * 
 * @author inc0g-repoz
 */
public abstract class AccessorVariable extends AccessorNamespaced
{

    AccessorVariable(ResolvedIdentifier id)
    {
        super(id);
    }

    @Override
    public String toString()
    {
        return next == null ? identifier.toString() : identifier + "." + next;
    }

}
