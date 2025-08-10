package com.github.inc0grepoz.lix4j.unit.inbuilt;

import java.util.Arrays;

import com.github.inc0grepoz.lix4j.ctx.Identifier;
import com.github.inc0grepoz.lix4j.ctx.Namespace;
import com.github.inc0grepoz.lix4j.unit.UnitFunction;
import com.github.inc0grepoz.lix4j.unit.UnitSection;
import com.github.inc0grepoz.lix4j.util.ControlFlow;

public class InBuiltSleep extends UnitFunction
{

    public InBuiltSleep(UnitSection parent, Namespace namespace)
    {
        super(parent, Identifier.of(namespace, "sleep"), Arrays.asList("time"));
    }

    @Override
    public Object call(Object... params)
    {
        try
        {
            Thread.sleep(((Number) params[0]).longValue());
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }

        return ControlFlow.VOID;
    }

}
