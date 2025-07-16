package com.github.inc0grepoz.lix4j.unit;

import com.github.inc0grepoz.lix4j.Script;

public class CompileTimeContext
{

    private final Script script;
    private final CompileTimeVarpool varpool = new CompileTimeVarpool();

    public CompileTimeContext(Script script)
    {
        this.script = script;
    }

    public Script getScript()
    {
        return script;
    }

    public CompileTimeVarpool getVarpool()
    {
        return varpool;
    }

}
