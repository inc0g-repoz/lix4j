package com.github.inc0grepoz.lix4j.value;

abstract class AccessorNamespaced extends AccessorNamed
{

    protected final String namespace;

    AccessorNamespaced(String namespace, String name)
    {
        super(name);
        this.namespace = namespace;
    }

    public String getNamespace()
    {
        return namespace;
    }

}
