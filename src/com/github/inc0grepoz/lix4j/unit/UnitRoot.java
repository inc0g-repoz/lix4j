package com.github.inc0grepoz.lix4j.unit;

import java.util.Map.Entry;
import java.util.StringJoiner;

import com.github.inc0grepoz.lix4j.Script;
import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.lookup.Lookup;
import com.github.inc0grepoz.lix4j.lookup.Namespace;
import com.github.inc0grepoz.lix4j.lookup.id.ResolvedIdentifier;
import com.github.inc0grepoz.lix4j.util.Predicates;

public class UnitRoot extends UnitSection
{

    private final Script script;

    final Lookup<UnitFunction> functions = new Lookup<>();

    public UnitRoot(Script script, CompileTimeContext ctx)
    {
        super(null, ctx);
        this.script = script;
    }

    @Override
    public String toString()
    {
        StringJoiner joiner = new StringJoiner("\n");
        childs.forEach(child -> joiner.add(child.toString()));
        return joiner.toString();
    }

    public UnitFunction getFunctionAsChild(ResolvedIdentifier id)
    {
        UnitFunction fn;

        for (Unit child: childs)
        {
            if (child instanceof UnitFunction)
            {
                fn = ((UnitFunction) child);

                if (fn.identifier.equals(id))
                {
                    return fn;
                }
            }
        }

        return null;
    }

    public UnitFunction lookupFunction(ResolvedIdentifier id, Namespace namespaceGlobal)
    {
        Entry<ResolvedIdentifier, UnitFunction> result = functions.findEntry(id,
                namespaceGlobal, Predicates.alwaysTrue());
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
