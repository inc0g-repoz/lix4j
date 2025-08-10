package com.github.inc0grepoz.lix4j.unit.inbuilt;

import java.util.Arrays;

import com.github.inc0grepoz.lix4j.ctx.Identifier;
import com.github.inc0grepoz.lix4j.ctx.Namespace;
import com.github.inc0grepoz.lix4j.unit.UnitFunction;
import com.github.inc0grepoz.lix4j.unit.UnitSection;
import com.github.inc0grepoz.lix4j.util.ControlFlow;

public class InBuiltPrintlnEmpty extends UnitFunction
{

    public InBuiltPrintlnEmpty(UnitSection parent, Namespace namespace)
    {
        super(parent, Identifier.of(namespace, "println"), Arrays.asList());
    }

    @Override
    public Object call(Object... params)
    {
        System.out.println();
        return ControlFlow.VOID;
    }

}
