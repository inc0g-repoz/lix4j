package com.github.inc0grepoz.lix4j.unit;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.ctx.ExecutionVarpool;
import com.github.inc0grepoz.lix4j.ctx.Identifier;
import com.github.inc0grepoz.lix4j.util.ControlFlow;

public class UnitFunction extends UnitSection
{

    static final Object[] EMPTY_ARRAY = new Object[0];

    static UnitFunction compile(CompileTimeContext ctx, ASTNode node, UnitSection section)
    {
        LinkedList<String> tokens = node.getTokens();

        tokens.poll(); // function
        String name = tokens.poll();
        String token = tokens.poll();

        if (!token.equals("("))
        {
            throw new IllegalStateException("'(' expected, but \"" + token + "\" found");
        }

        List<String> paramNames;

        if ((token = tokens.poll()).equals(")"))
        {
            paramNames = Collections.emptyList();
        }
        else
        {
            (paramNames = new ArrayList<>()).add(token);

            while ((token = tokens.poll()).equals(","))
            {
                paramNames.add(tokens.poll());
            }

            if (!token.equals(")"))
            {
                throw new IllegalStateException("')' expected, but \"" + token + "\" found");
            }
        }

        Identifier id = new Identifier(ctx.getNamespace(), name);
        UnitFunction fn = ctx.getScript().getFunction(id, paramNames.size());

        if (fn != null)
        {
            throw new RuntimeException("Function overloading is not supported (duplicate function " + fn.getSignature() + ")");
        }

        fn = new UnitFunction(section, id, paramNames);
        ScriptCompiler.appendSectionUnits(ctx, node, fn);

        return fn;
    }

    final Identifier identifier;
    final List<String> paramNames;
    final UnitRoot root;

    protected UnitFunction(UnitSection parent, Identifier identifier, List<String> paramNames)
    {
        super(parent);
        this.identifier = identifier;
        this.paramNames = paramNames;
        root = root();
    }

    @Override
    public String toString()
    {
        return "function " + getSignature() + " " + super.toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof UnitFunction))
        {
            return false;
        }

        UnitFunction fn = (UnitFunction) obj;

        return fn.paramNames.size() == paramNames.size()
                && fn.identifier.equals(identifier);
    }

    @Override
    Object execute(ExecutionContext context)
    {
        return ControlFlow.KEEP_EXECUTING;
    }

    /**
     * Calls this function and returns an object, if has to,
     * or {@link ControlFlow#VOID VOID} otherwise.
     * 
     * @param params the function parameters
     * @return an object instance
     */
    public Object call(Object... params)
    {
        if (params == null)
        {
            params = EMPTY_ARRAY;
        }

        if (params.length != paramNames.size())
        {
            throw new IllegalArgumentException(getSignature());
        }

        ExecutionContext ctx = root.getScript().supplyContext();
        ExecutionVarpool pool = ctx.getVarpool();
        pool.enterSection();

        for (int i = 0; i < params.length; i++)
        {
            Identifier id = new Identifier(identifier.getNamespace(), paramNames.get(i));
            pool.set(id, params[i]);
        }

        Object rv = executeChilds(ctx);
        pool.exitSection();

        return rv == ControlFlow.KEEP_EXECUTING ? ControlFlow.VOID : rv;
    }

    /**
     * Returns the function identifier.
     * 
     * @return the function identifier
     */
    public Identifier getIdentifier()
    {
        return identifier;
    }

    /**
     * Returns a string representation of the signature.
     * 
     * @return a string representation of the signature
     */
    public String getSignature()
    {
        return identifier + "(" + String.join(", ", paramNames) + ")";
    }

    /**
     * Returns a {@code List} of parameter names in the
     * function definition.
     * 
     * @return a {@code List} of parameter names
     */
    public List<String> getParameterNames()
    {
        return paramNames;
    }

    /**
     * Implements a functional interface using a proxy instance
     * and returns it.
     * 
     * @param type the functional interface class to implement
     * @return a proxy instance
     */
    public Object createProxy(Class<?> type)
    {
        return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type },
                (proxy, method, args) -> call(args));
    }

}
