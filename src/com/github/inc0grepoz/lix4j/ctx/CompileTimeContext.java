package com.github.inc0grepoz.lix4j.ctx;

import com.github.inc0grepoz.lix4j.Script;

public class CompileTimeContext
{

    private final Script script;
    private final CompileTimeVarpool varpool = new CompileTimeVarpool();
    private final Namespace globalNamespace;

    private Namespace namespace;

    public CompileTimeContext(Script script, Namespace namespace)
    {
        this.script = script;
        this.globalNamespace = this.namespace = namespace; // global
    }

    public Script getScript()
    {
        return script;
    }

    public CompileTimeVarpool getVarpool()
    {
        return varpool;
    }

    public Namespace getGlobalNamespace()
    {
        return globalNamespace;
    }

    public Namespace getNamespace()
    {
        return namespace;
    }

    public void setNamespace(Namespace namespace)
    {
        this.namespace = namespace;
    }

    public void namespaceDown(String name)
    {
        this.namespace = namespace.nest(name);
    }

    public void namespaceUp()
    {
        Namespace parent = namespace.getParent();

        if (parent == null)
        {
            throw new RuntimeException("No parent namespace for " + namespace);
        }

        namespace = parent;
    }

}
