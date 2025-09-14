package com.github.inc0grepoz.lix4j.ctx;

import com.github.inc0grepoz.lix4j.Script;

public abstract class AbstractContext<T extends Varpool<?>>
{

    protected final Script script;
    protected final T varpool;

    AbstractContext(Script script, T varpool)
    {
        this.script = script;
        this.varpool = varpool;
    }

    public Script getScript()
    {
        return script;
    }

    public T getVarpool()
    {
        return varpool;
    }

}
