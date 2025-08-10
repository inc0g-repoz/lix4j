package com.github.inc0grepoz.lix4j.unit.inbuilt;

import java.lang.reflect.Array;
import java.util.Arrays;

import com.github.inc0grepoz.lix4j.ctx.Identifier;
import com.github.inc0grepoz.lix4j.unit.UnitFunction;
import com.github.inc0grepoz.lix4j.unit.UnitSection;
import com.github.inc0grepoz.lix4j.util.Namespace;

public class InBuiltLength extends UnitFunction
{

    public InBuiltLength(UnitSection parent)
    {
        super(parent, Identifier.of(Namespace.GLOBAL, "length"), Arrays.asList("array"));
    }

    @Override
    public Object call(Object... params)
    {
        return Array.getLength(params[0]);
    }

}
