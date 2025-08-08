package com.github.inc0grepoz.lix4j.value;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.exception.UnassignedVariableError;
import com.github.inc0grepoz.lix4j.unit.UnitFunction;
import com.github.inc0grepoz.lix4j.util.Namespace;

public class AccessorUnassigned extends AccessorNamespaced
{

    private Object proxy;

    public AccessorUnassigned(String namespace, String name)
    {
        super(namespace, name);
    }

    @Override
    public String toString()
    {
        if (proxy == null)
        {
            throwUnassigned();
        }

        return "$" + (namespace == Namespace.GLOBAL ? "" : namespace + "::") + name + (next == null ? "" : "." + next);
    }

    @Override
    public Object access(ExecutionContext ctx, Object src)
    {
        if (proxy == null)
        {
            throwUnassigned();
        }

        return proxy;
    }

    public Object initProxy(UnitFunction fn, Class<?> type)
    {
        if (proxy != null)
        {
            throw new IllegalStateException("Initialized a function proxy twice");
        }

        return this.proxy = fn.createProxy(type);
    }

    public void throwUnassigned()
    {
        throw new UnassignedVariableError("Variable " + namespace + "::" + name + " is unassigned");
    }

}
