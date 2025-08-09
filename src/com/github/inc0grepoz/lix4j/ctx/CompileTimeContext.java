package com.github.inc0grepoz.lix4j.ctx;

import com.github.inc0grepoz.lix4j.Script;
import com.github.inc0grepoz.lix4j.util.Namespace;

public class CompileTimeContext
{

    private final Script script;
    private final CompileTimeVarpool varpool = new CompileTimeVarpool();

    private String namespace = Namespace.GLOBAL;

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

    public String getNamespace()
    {
        return namespace;
    }

    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }

}
