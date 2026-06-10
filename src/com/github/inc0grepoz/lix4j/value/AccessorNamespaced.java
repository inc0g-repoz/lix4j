package com.github.inc0grepoz.lix4j.value;

import com.github.inc0grepoz.lix4j.lookup.id.Identifier;
import com.github.inc0grepoz.lix4j.lookup.id.ResolvedIdentifier;

abstract class AccessorNamespaced extends Accessor
{

    protected final Identifier identifier;

    AccessorNamespaced(ResolvedIdentifier identifier)
    {
        this.identifier = identifier;
    }

    public Identifier getIdentifier()
    {
        return identifier;
    }

}
