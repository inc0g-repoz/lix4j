package com.github.inc0grepoz.lix4j.unit.inbuilt;

import java.lang.reflect.Array;
import java.util.Arrays;

import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.unit.UnitFunction;
import com.github.inc0grepoz.lix4j.unit.UnitSection;

public class InBuiltNewArray extends UnitFunction
{

    public InBuiltNewArray(UnitSection parent, CompileTimeContext ctx)
    {
        super(parent, ctx, "new_array", Arrays.asList("class", "length"));
    }

    @Override
    public Object call(Object... params)
    {
        try {
            Class<?> clazz = Class.forName((String) params[0]);
            int length = (int) params[1];
            return Array.newInstance(clazz, length);
        } catch (Throwable t) {
            throw new IllegalArgumentException(t);
        }
    }

}
