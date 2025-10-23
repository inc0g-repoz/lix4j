package com.github.inc0grepoz.lix4j.value;

import com.github.inc0grepoz.lix4j.id.Identifier;

/**
 * Represents an accessor for variables.
 * 
 * @author inc0g-repoz
 */
public abstract class AccessorVariable extends AccessorNamespaced
{

    AccessorVariable(Identifier id)
    {
        super(id);
    }

    @Override
    public String toString()
    {
        return next == null ? identifier.toString() : identifier + "." + next;
    }

}
