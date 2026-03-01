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
        // No other conditions matter, if the variable was not
        // declared in the current line
        if (!declaration)
        {
            return this.instance = instance;
        }

        // We can still assign a value, if it has not been yet
        // (for unassigned variables like "var x;")
        if (this.instance != null && this.instance.getClass() == AccessorUnassigned.class)
        {
            return this.instance = instance;
        }

        // Returning the stored instance, if the variable was
        // assigned in the current line (static)
        return this.instance;
    }

    public Object get()
    {
        return instance;
    }

}
