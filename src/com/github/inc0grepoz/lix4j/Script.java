package com.github.inc0grepoz.lix4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.inc0grepoz.lix4j.ast.AST;
import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.ctx.Namespace;
import com.github.inc0grepoz.lix4j.unit.ScriptCompiler;
import com.github.inc0grepoz.lix4j.unit.UnitFunction;
import com.github.inc0grepoz.lix4j.unit.UnitRoot;
import com.github.inc0grepoz.lix4j.unit.expression.Operator;
import com.github.inc0grepoz.lix4j.util.Collections;
import com.github.inc0grepoz.lix4j.util.Lexer;

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
    private final UnitRoot root = new UnitRoot(this);

    // Should only be instantiated within the executor's code
    Script(ScriptExecutor executor, AST ast)
    {
        // Configuring and loading inbuilt features
        loaderDirectory = executor.getLoaderDirectory();
        globalNamespace = new Namespace(null, Namespace.GLOBAL_NAME);
        executor.getDefaultOperators().forEach(operators::add);
        executor.supplyInbuiltFunctions(root, globalNamespace);

        // Compiling declared script members
        CompileTimeContext ctx = new CompileTimeContext(this);
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
     * @param namespaceParts the parts of a nested namespace
     * @param name           the function name
     * @param namespaceStart the namespace to start searching in
     * @param paramCount     the parameters count
     * @return a function
     */
    public UnitFunction getFunction(LinkedList<String> namespaceParts, String name,
            Namespace namespaceStart, int paramCount)
    {
        return root.lookupFunction(namespaceParts, name,
                globalNamespace, namespaceStart, paramCount);
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
        return root.lookupFunction(Collections.emptyLinkedList(), name,
                globalNamespace, globalNamespace, paramCount);
    }

    /**
     * Calls the function with the specified namespace and name and passes
     * the specified parameters, if exists.
     * 
     * @param namespaceParts the parts of a nested namespace
     * @param name           the function name
     * @param namespaceStart the namespace to start searching in
     * @param params     the parameters
     * @return the function return value
     * @throws IllegalArgumentException
     *         if no function has the same name and parameters count
     */
    public Object callFunction(LinkedList<String> namespaceParts, String name,
            Namespace namespaceStart, Object... params)
    {
        return root.lookupFunction(namespaceParts, name,
                globalNamespace, namespaceStart, params.length).call(params);
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
        return root.lookupFunction(Collections.emptyLinkedList(), name,
                globalNamespace, globalNamespace, params.length).call(params);
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
        LinkedList<String> input = Lexer.readTokens(new FileReader(file));
        AST ast = AST.generateTree(input);

        // Compiling into the global namespace by default
        Namespace namespace = ctx.getNamespace();
        ctx.setNamespace(globalNamespace);
        ScriptCompiler.compileSection(ctx, ast, root);
        ctx.setNamespace(namespace);
    }

}
