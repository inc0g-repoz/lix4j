package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.StringJoiner;

import com.github.inc0grepoz.lix4j.Script;
import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.ctx.Identifier;
import com.github.inc0grepoz.lix4j.ctx.Lookup;
import com.github.inc0grepoz.lix4j.ctx.Namespace;

public class UnitRoot extends UnitSection
{

    private final Script script;

    final Lookup<UnitFunction> functions = new Lookup<>();

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

    public UnitFunction getFunctionAsChild(Identifier identifier, int paramCount)
    {
        UnitFunction fn;
        Identifier fid;

        for (Unit child: childs)
        {
            if (child instanceof UnitFunction)
            {
                fn = ((UnitFunction) child);
                fid = fn.identifier;

                if (fid.equals(identifier) && fn.paramNames.size() == paramCount)
                {
                    return fn;
                }
            }
        }

        return null;
    }

    public UnitFunction lookupFunction(LinkedList<String> namespaceParts, String name,
            Namespace namespaceGlobal, Namespace namespaceCurrent, int paramCount)
    {
        Entry<Identifier, UnitFunction> result = functions.findEntry(namespaceParts, name,
                namespaceGlobal, namespaceCurrent, fn -> fn.paramNames.size() == paramCount);
        return result == null ? null : result.getValue();
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
