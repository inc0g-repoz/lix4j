package com.github.inc0grepoz.lix4j.unit;

import java.util.StringJoiner;

import com.github.inc0grepoz.lix4j.Script;
import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.util.Namespace;

public class UnitRoot extends UnitSection
{

    private final Script script;

    public UnitRoot(Script script)
    {
        super(null);
        this.script = script;
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner("\n");
        childs.forEach(child -> joiner.add(child.toString()));
        return joiner.toString();
    }

    public UnitFunction getFunction(String namespace, String name, int paramCount)
    {
        UnitFunction fn;

        for (Unit child: childs)
        {
            if (child instanceof UnitFunction)
            {
                fn = ((UnitFunction) child);

                if ((fn.namespace == Namespace.GLOBAL || fn.namespace.equals(namespace))
                        && fn.name.equals(name) && fn.paramNames.size() == paramCount)
                {
                    return fn;
                }
            }
        }

        return null;
    }

    public void init(ExecutionContext globalContext)
    {
        // Functions are expected to be defined globally
        childs.forEach(u -> u.execute(globalContext));
    }

    Script getScript()
    {
        return script;
    }

}
