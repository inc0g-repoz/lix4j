package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;
import java.util.StringJoiner;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.util.FlowControl;

public class UnitSection extends Unit
{

    final LinkedList<Unit> childs = new LinkedList<>();

    UnitSection(UnitSection parent, boolean add)
    {
        super(parent, add);
    }

    UnitSection(UnitSection parent)
    {
        super(parent);
    }

    @Override
    public String toString()
    {
        if (childs.isEmpty())
        {
            return "{}";
        }

        StringJoiner joiner = new StringJoiner("\n", "{\n", "\n}");

        for (Unit child: childs)
        {
            joiner.add(child.toString());
        }

        return joiner.toString();
    }

    @Override
    Object execute(ExecutionContext ctx)
    {
        ctx.getVarpool().enterSection();
        Object rv = executeChilds(ctx);
        ctx.getVarpool().exitSection();
        return rv;
    }

    Object executeChilds(ExecutionContext ctx)
    {
        Object rv;

        for (Unit unit: childs)
        {
            rv = unit.execute(ctx);

            if (rv != FlowControl.KEEP_EXECUTING)
            {
                return rv;
            }
        }

        return FlowControl.KEEP_EXECUTING;
    }

}
