package com.github.inc0grepoz.lix4j.unit.inbuilt;

import java.util.Arrays;

import com.github.inc0grepoz.lix4j.unit.UnitFunction;
import com.github.inc0grepoz.lix4j.unit.UnitSection;
import com.github.inc0grepoz.lix4j.util.Namespace;
import com.github.inc0grepoz.lix4j.value.AccessorNoInstance;

public class InBuiltNoInstance extends UnitFunction
{

    public InBuiltNoInstance(UnitSection parent)
    {
        super(parent, Namespace.GLOBAL, "no_instance", Arrays.asList("class"));
    }

    @Override
    public Object call(Object... params)
    {
        try {
            return AccessorNoInstance.of(Class.forName((String) params[0]));
        } catch (Throwable t) {
            throw new IllegalArgumentException(t);
        }
    }

}
