package com.github.inc0grepoz.lix4j.unit.inbuilt;

import java.util.Arrays;

import com.github.inc0grepoz.lix4j.ctx.Identifier;
import com.github.inc0grepoz.lix4j.unit.UnitFunction;
import com.github.inc0grepoz.lix4j.unit.UnitSection;
import com.github.inc0grepoz.lix4j.util.Namespace;

public class InBuiltClassForName extends UnitFunction
{

    public InBuiltClassForName(UnitSection parent)
    {
        super(parent, Identifier.of(Namespace.GLOBAL, "class_for_name"), Arrays.asList("class"));
    }

    @Override
    public Object call(Object... params)
    {
        try {
            return Class.forName((String) params[0]);
        } catch (Throwable t) {
            throw new IllegalArgumentException(t);
        }
    }

}
