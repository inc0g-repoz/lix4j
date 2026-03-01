package test;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.inc0grepoz.lix4j.Script;
import com.github.inc0grepoz.lix4j.ScriptExecutor;
import com.github.inc0grepoz.lix4j.id.Identifier;
import com.github.inc0grepoz.lix4j.id.Lookup;
import com.github.inc0grepoz.lix4j.id.Namespace;
import com.github.inc0grepoz.lix4j.unit.UnitFunction;
import com.github.inc0grepoz.lix4j.util.Lexer;
import com.github.inc0grepoz.lix4j.util.Predicates;

@SuppressWarnings("all")
class DefaultTestCase {

    private static final ScriptExecutor EXECUTOR = new ScriptExecutor();
    private static final File LOADER_DIRECTORY;

    static
    {
        EXECUTOR.setLoaderDirectory(LOADER_DIRECTORY = new File("scripts"));
    }

    @Disabled
    @Test
    void testLexer()
    {
        // src/com/github/inc0grepoz/dsl/util/Lexer.java
        // main.script
        File file = new File(LOADER_DIRECTORY, "main.lix");

        try
        {
            LinkedList<String> tokens = Lexer.readTokens(new FileReader(file));
            tokens.forEach(token -> System.out.print("$" + token));
            System.out.println();
        }
        catch (Throwable t)
        {
            fail("Failed to lex the script: " + t);
        }
    }

//  @Disabled
    @Test
    void testScriptEngine()
    {
        File file = new File(LOADER_DIRECTORY, "main.lix");

        Script script = time("Compiled", () -> {
            try
            {
                return EXECUTOR.load(file);
            }
            catch (IOException e)
            {
                throw new AssertionError(e);
            }
        });

//      System.err.println(script);
        UnitFunction fn = script.getFunction("main", 0);

        PrintStream ps = System.out;
        PrintStream nps = new PrintStream(new NullOutputStream());

        System.setOut(nps);
        time("Executed", () -> fn.call());

        System.setOut(ps);
        time("Executed", () -> fn.call());
    }

    @Disabled
    @Test
    void testLookup()
    {
        Namespace global = new Namespace(null, Namespace.GLOBAL_NAME);
        Lookup<Object> lookup = new Lookup<>();

        lookup.set(Identifier.resolved(global, "a"), "default");
        lookup.set(Identifier.resolved(global.nest("values"), "a"), 5);
        lookup.set(Identifier.resolved(global.nest("values").nest("strings"), "a"), "otherValue");

        Entry<Identifier, Object> result = lookup.findEntry(
                Identifier.unresolved(link("values", "strings"), global, "a"),
                global, Predicates.alwaysTrue());
        System.out.println(result.getKey() + " = " + result.getValue());
        System.out.println(global.nest("c").nest("d") == global.nest("c").nest("d"));
        System.out.println(global.find(link("values", "strings")) == global.find(link("values", "strings")));
    }

    private static <T> LinkedList<T> link(T... values)
    {
        return Stream.of(values).collect(Collectors.toCollection(LinkedList::new));
    }

    private static <T> T time(Supplier<T> lambda)
    {
        return time(null, lambda);
    }

    private static <T> T time(String label, Supplier<T> lambda)
    {
        long time = System.nanoTime();
        T rv = lambda.get();
        time = System.nanoTime() - time;
        double timeMs = (double) time / 1000000;
        System.out.println((label == null ? "" : label + " in ") + timeMs + " ms");
        return rv;
    }

}

class NullOutputStream extends OutputStream
{

    @Override
    public void write(int b) throws IOException {}

    @Override
    public void write(byte[] b) throws IOException {}

    @Override
    public void write(byte[] b, int off, int len) throws IOException {}

    @Override
    public void flush() throws IOException {}

    @Override
    public void close() throws IOException {}

}
