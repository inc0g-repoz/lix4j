package com.github.inc0grepoz.lix4j.unit;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.util.ControlFlow;

public class UnitFlowContinue extends Unit
{

    public UnitFlowContinue(UnitSection parent)
    {
        super(parent);
    }

    @Override
    public String toString()
    {
        return "continue";
    }

    @Override
    Object execute(ExecutionContext context)
    {
        return ControlFlow.CONTINUE;
    }

}
