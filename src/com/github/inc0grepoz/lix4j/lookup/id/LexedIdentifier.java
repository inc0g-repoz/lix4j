package com.github.inc0grepoz.lix4j.lookup.id;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.lookup.Namespace;

public class LexedIdentifier implements Identifier
{

    private final LinkedList<String> parts;
    private String name;
    private int data;

    private LexedIdentifier(LinkedList<String> parts, String name, int data)
    {
        this.parts = parts;
        this.name = name;
        this.data = data;
    }

    public LexedIdentifier(String name)
    {
        this(new LinkedList<>(), name, 0);
    }

    @Override
    public String toString()
    {
        return parts.isEmpty() ? name : String.join("::", parts) + "::" + name;
    }

    @Override
    public Namespace getNamespace()
    {
        throw new UnsupportedOperationException("Tried to read unresolved namespace");
    }

    public LinkedList<String> getNamespaceParts()
    {
        return parts;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public int getData()
    {
        return data;
    }

    @Override
    public Identifier withData(int data)
    {
        return new LexedIdentifier(new LinkedList<>(parts), name, data);
    }

    @Override
    public boolean isResolved()
    {
        return false;
    }

    @Override
    public Identifier resolve(Namespace in)
    {
        Namespace ns = in.find(parts);
        return new ResolvedIdentifier(ns, name, data);
    }

    public void add(String name)
    {
        parts.add(this.name);
        this.name = name;
    }

}
