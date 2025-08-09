package com.github.inc0grepoz.lix4j.ctx;

import com.github.inc0grepoz.lix4j.util.Namespace;

public class Identifier
{

    private final String namespace, name;

    Identifier(String namespace, String name)
    {
        this.namespace = namespace;
        this.name = name;
    }

    @Override
    public String toString()
    {
        return namespace + "::" + name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Identifier)
        {
            Identifier id = (Identifier) obj;

            return (/* id.namespace == Namespace.GLOBAL || */ id.namespace.equals(namespace))
                    && id.name.equals(name);
        }

        return false;
    }

}
