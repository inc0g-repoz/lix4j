package com.github.inc0grepoz.lix4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.id.Identifier;
import com.github.inc0grepoz.lix4j.id.Namespace;
import com.github.inc0grepoz.lix4j.unit.ScriptCompiler;
import com.github.inc0grepoz.lix4j.unit.UnitFunction;
import com.github.inc0grepoz.lix4j.unit.UnitRoot;
import com.github.inc0grepoz.lix4j.unit.exp.Operator;
import com.github.inc0grepoz.lix4j.util.Lexer.Token;

import ctxfree.lex.AST;
import ctxfree.lex.Lexer;

/**
 * Represents a compiled script.
 * 
 * @author inc0g-repoz
 */
public class Script
{

    private final File loaderDirectory;
    private final List<Operator> operators = new ArrayList<>();
    private final ExecutionContext globalContext;
    private final Namespace globalNamespace;
    private final UnitRoot root;

    {
        globalNamespace = new Namespace(null, Namespace.GLOBAL_NAME);
    }

    // Should only be instantiated within the executor's code
    Script(ScriptExecutor executor, AST.TokenGroup ast)
    {
        CompileTimeContext ctx = new CompileTimeContext(this);
        root = new UnitRoot(this, ctx);

        // Configuring and loading inbuilt features
        loaderDirectory = executor.getLoaderDirectory();
        executor.getDefaultOperators().forEach(operators::add);
        executor.supplyInbuiltFunctions(root, ctx);

        // Compiling declared script members
        ctx.getVarpool().enterSection(); // for global variables
        ScriptCompiler.compileSection(ctx, ast, root);

        // Initializing the script (global variables)
        globalContext = new ExecutionContext(this);
        globalContext.getVarpool().enterSection();
        root.init(globalContext);
    }

    @Override
    public String toString()
    {
        return root.toString();
    }

    public Namespace getGlobalNamespace()
    {
        return globalNamespace;
    }

    public UnitRoot getRoot()
    {
        return root;
    }

    /**
     * Returns the function by the specified namespace, name and parameters
     * count, if exists, or {@code null} otherwise.
     * 
     * @param id the function identifier
     * @return a function
     */
    public UnitFunction getFunction(Identifier id)
    {
        return root.lookupFunction(id, globalNamespace);
    }

    /**
     * Returns the function by the specified name and parameters
     * count, if exists, or {@code null} otherwise.
     * 
     * @param name       the function name
     * @param paramCount the parameters count
     * @return a function
     */
    public UnitFunction getFunction(String name, int paramCount)
    {
        Identifier id = Identifier.resolved(globalNamespace, name, paramCount);
        return root.lookupFunction(id, globalNamespace);
    }

    /**
     * Returns the list of operators used by this {@code Script}.
     * 
     * @return the list of operators
     */
    public List<Operator> getOperators()
    {
        return operators;
    }

    /**
     * Supplies the execution context copy to a function.
     * 
     * @return a clone of the script execution context
     */
    public ExecutionContext supplyContext()
    {
        return globalContext.clone();
    }

    // Includes code from the file by the specified filepath
    public void include(CompileTimeContext ctx, String filepath) throws IOException
    {
        if (loaderDirectory == null || !loaderDirectory.isDirectory())
        {
            throw new AssertionError("No loader directory provided for including code");
        }

        // Lexing the input and generating a syntax tree
        File file = new File(loaderDirectory, filepath);
        LinkedList<Lexer.Token> input = Lexer.lex(new FileReader(file));
        AST.TokenGroup ast = AST.generate(input);

        // Compiling into the global namespace by default
        Namespace namespace = ctx.getNamespace();
        ctx.setNamespace(globalNamespace);
        ScriptCompiler.compileSection(ctx, ast, root);
        ctx.setNamespace(namespace);
    }

}
