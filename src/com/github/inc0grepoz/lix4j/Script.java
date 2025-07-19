package com.github.inc0grepoz.lix4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.inc0grepoz.lix4j.ast.AST;
import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.unit.CompileTimeContext;
import com.github.inc0grepoz.lix4j.unit.ScriptCompiler;
import com.github.inc0grepoz.lix4j.unit.UnitFunction;
import com.github.inc0grepoz.lix4j.unit.UnitRoot;
import com.github.inc0grepoz.lix4j.unit.expression.Operator;
import com.github.inc0grepoz.lix4j.util.Lexer;

/**
 * Represents a compiled script.
 * 
 * @author inc0g-repoz
 */
public class Script
{

    private final List<Operator> operators = new ArrayList<>();
    private final ExecutionContext globalContext = new ExecutionContext(this);
    private final File loaderDirectory;
    private final UnitRoot root = new UnitRoot(this);

    // A package-private constructor
    Script(ScriptExecutor executor, AST ast)
    {
        executor.getDefaultOperators().forEach(operators::add);
        loaderDirectory = executor.getLoaderDirectory();

        // Loading inbuilt functions
        executor.supplyInbuiltFunctions(root);

        // Compiling declared script members
        CompileTimeContext ctx = new CompileTimeContext(this);
        ctx.getVarpool().enterSection();
        ScriptCompiler.compile(ctx, ast, this, root);

        globalContext.getVarpool().enterSection();
        root.init(globalContext);
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
        return root.getFunction(name, paramCount);
    }

    /**
     * Calls the function with the specified name and parameters
     * count, if exists.
     * 
     * @param name   the function name
     * @param params the parameters
     * @return the function return value
     * @throws IllegalArgumentException
     *         if no function has the same name and parameters count
     */
    public Object callFunction(String name, Object... params)
    {
        return root.getFunction(name, params.length).call(params);
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

        File file = new File(loaderDirectory, filepath);
        LinkedList<String> input = Lexer.readTokens(new FileReader(file));
        AST ast = AST.generateTree(input);

        ScriptCompiler.compile(ctx, ast, this, root);
    }

}
