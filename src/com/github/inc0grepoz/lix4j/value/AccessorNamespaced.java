package com.github.inc0grepoz.lix4j.value;

abstract class AccessorNamespaced extends Accessor
{

    protected final String namespace, name;

    AccessorNamespaced(String namespace, String name)
    {
        this.namespace = namespace;
        this.name = name;
    }

    public String getNamespace()
    {
        return namespace;
    }

    public String getName()
    {
        return name;
    }

}
