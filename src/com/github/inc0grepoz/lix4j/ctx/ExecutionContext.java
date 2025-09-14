package com.github.inc0grepoz.lix4j.ctx;

import com.github.inc0grepoz.lix4j.Script;

/**
 * Represents a context of the execution flow with a
 * pool for variables.
 * 
 * @author inc0g-repoz
 */
public class ExecutionContext extends AbstractContext<ExecutionVarpool> implements Cloneable
{

    /**
     * Creates a new global context for the specified {@code script}.
     * 
     * @param script the {@code Script}
     */
    public ExecutionContext(Script script)
    {
        this(script, new ExecutionVarpool());
    }

    private ExecutionContext(Script script, ExecutionVarpool varpool)
    {
        super(script, varpool);
    }

    @Override
    public ExecutionContext clone()
    {
        return new ExecutionContext(script, varpool.clone());
    }

}
