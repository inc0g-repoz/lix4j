package com.github.inc0grepoz.lix4j.unit.inbuilt;

import java.util.Arrays;

import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.unit.UnitFunction;
import com.github.inc0grepoz.lix4j.unit.UnitSection;
import com.github.inc0grepoz.lix4j.util.ControlFlow;

public class InBuiltPrintlnEmpty extends UnitFunction
{

    public InBuiltPrintlnEmpty(UnitSection parent, CompileTimeContext ctx)
    {
        super(parent, ctx, "println", Arrays.asList());
    }

    @Override
    public Object call(Object... params)
    {
        System.out.println();
        return ControlFlow.VOID;
    }

}
