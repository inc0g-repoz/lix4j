package com.github.inc0grepoz.lix4j.value;

import com.github.inc0grepoz.lix4j.id.Identifier;

public class Variable
{

    private final Identifier identifier;

    private Object instance;

    Variable(Identifier id)
    {
        identifier = id;
        instance = new AccessorUnassigned(id);
    }

    @Override
    public String toString()
    {
        return identifier.toString();
    }

    public Identifier getIdentifier()
    {
        return identifier;
    }

    public Object set(Object instance, boolean declaration)
    {
        return !declaration || this.instance.getClass() == AccessorUnassigned.class
                ? this.instance = instance : this.instance;
    }

    public Object get()
    {
        return instance;
    }

}
