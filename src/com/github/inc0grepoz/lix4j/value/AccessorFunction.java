package com.github.inc0grepoz.lix4j.value;

import java.util.StringJoiner;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.ctx.Identifier;
import com.github.inc0grepoz.lix4j.unit.UnitFunction;

class AccessorFunction extends AccessorNamespaced
{

    private final Accessor[] params;

    private UnitFunction cachedFunction;

    AccessorFunction(Identifier identifier, Accessor[] params)
    {
        super(identifier);
        this.params = params;
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner(", ", "(", ")");

        for (int i = 0; i < params.length; i++)
        {
            joiner.add(params[i].toString());
        }

        return identifier + joiner.toString() + (next == null ? "" : "." + next);
    }

    @Override
    public Object access(ExecutionContext ctx, Object src)
    {
        Object[] paramArr;

        if (params == null || params.length == 0)
        {
            paramArr = new Object[0];
        }
        else
        {
            paramArr = new Object[params.length];

            for (int i = 0; i < params.length; i++)
            {
                if (params[i] == Accessor.NULL)
                {
                    paramArr[i] = null;
                    continue;
                }

                if (params[i].getClass() == AccessorUnassigned.class)
                {
                    // Must be a function reference, resolved below.
                    // Function proxies require the amount of arguments,
                    // so they have to be resolved after compilation.
                    AccessorNamespaced namespaced = (AccessorNamespaced) params[i];
                    paramArr[i] = params[i] = new AccessorUnassigned(namespaced.getIdentifier());
                }

                paramArr[i] = params[i].linkedAccess(ctx, null);
            }
        }

        if (cachedFunction == null)
        {
            cachedFunction = ctx.getScript().getFunction(
                    identifier.getNamespace().toLinkedList(),
                    identifier.getName(),
                    identifier.getNamespace(),
                    paramArr.length);
        }

        if (cachedFunction == null)
        {
            throw new NullPointerException("Unknown function " + identifier + " with " + params.length + " parameter(-s)");
        }
        else
        {
            return cachedFunction.call(paramArr);
        }
    }

}
