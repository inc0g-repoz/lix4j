package com.github.inc0grepoz.lix4j.value;

import com.github.inc0grepoz.lix4j.ctx.Identifier;

abstract class AccessorNamespaced extends Accessor
{

    protected final Identifier identifier;

    AccessorNamespaced(Identifier identifier)
    {
        this.identifier = identifier;
    }

    public Identifier getIdentifier()
    {
        return identifier;
    }

}
