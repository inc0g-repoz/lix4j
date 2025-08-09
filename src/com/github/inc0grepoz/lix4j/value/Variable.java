package com.github.inc0grepoz.lix4j.value;

public class Variable
{

    private final String namespace, name;

    private Object instance;

    Variable(String namespace, String name)
    {
        this.namespace = namespace;
        this.name = name;

        instance = new AccessorUnassigned(namespace, name);
    }

    @Override
    public String toString()
    {
        return name;
    }

    public String getNamespace()
    {
        return namespace;
    }

    public String getName()
    {
        return name;
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
