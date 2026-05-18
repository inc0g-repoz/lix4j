package com.github.inc0grepoz.lix4j.unit;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.util.ControlFlow;

public class UnitFlowBreak extends Unit
{

    public UnitFlowBreak(UnitSection parent)
    {
        super(parent);
    }

    @Override
    public String toString()
    {
        return "break";
    }

    @Override
    Object execute(ExecutionContext context)
    {
        return ControlFlow.BREAK;
    }

}
