package com.github.inc0grepoz.lix4j.value;

public class Variable
{

    static final Object UNASSIGNED = new Object() {

        @Override
        public String toString()
        {
            return "unassigned";
        }

    };

    private final String name;

    private Object instance = UNASSIGNED;

    Variable(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public String getName()
    {
        return name;
    }

    public Object set(Object instance)
    {
        return this.instance = instance;
    }

    public Object get()
    {
        if (instance == UNASSIGNED)
        {
            throw new RuntimeException(name + " is unassigned");
        }

        return instance;
    }

}
