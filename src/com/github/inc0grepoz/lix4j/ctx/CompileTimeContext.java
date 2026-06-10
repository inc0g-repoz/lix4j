package com.github.inc0grepoz.lix4j.ctx;

import com.github.inc0grepoz.lix4j.Script;
import com.github.inc0grepoz.lix4j.ctx.pool.CompileTimeVarpool;
import com.github.inc0grepoz.lix4j.lookup.Namespace;

public class CompileTimeContext extends AbstractContext<CompileTimeVarpool>
{

    private Namespace namespace;

    public CompileTimeContext(Script script)
    {
        super(script, new CompileTimeVarpool());

        this.namespace = script.getGlobalNamespace();
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
