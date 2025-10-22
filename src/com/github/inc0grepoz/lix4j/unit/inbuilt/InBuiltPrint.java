package com.github.inc0grepoz.lix4j.unit.inbuilt;

import java.util.Arrays;

import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.unit.UnitFunction;
import com.github.inc0grepoz.lix4j.unit.UnitSection;

public class InBuiltPrint extends UnitFunction
{

    public InBuiltPrint(UnitSection parent, CompileTimeContext ctx)
    {
        super(parent, ctx, "print", Arrays.asList("object"));
    }

    @Override
    public Object call(Object... params)
    {
        System.out.print(params[0]);
        return params[0];
    }

}
