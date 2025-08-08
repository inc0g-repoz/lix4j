package com.github.inc0grepoz.lix4j.ctx;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public abstract class Varpool
{

    protected final ArrayDeque<Map<String, Object>> stack;

    protected Varpool(ArrayDeque<Map<String, Object>> stack)
    {
        this.stack = stack;
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner("\n");

        for (Map<String, Object> layer: stack)
        {
            layer.forEach((k, v) -> joiner.add(k + " = " + v));
        }

        return joiner.toString();
    }

    /**
     * Enters a new context section and returns this instance.
     * 
     * @return this instance
     */
    public Varpool enterSection()
    {
        stack.push(new HashMap<>());
        return this;
    }

    /**
     * Exits the current context section and returns this instance.
     * 
     * @return this instance
     */
    public Varpool exitSection()
    {
        if (stack.isEmpty())
        {
            throw new IllegalStateException("No active section to exit");
        }

        stack.pop();
        return this;
    }

    public int getLayersCount()
    {
        return stack.size();
    }

    protected String toNamespaced(String namespace, String name)
    {
        return namespace + "::" + name;
    }

}
