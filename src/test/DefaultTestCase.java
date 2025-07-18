package test;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Supplier;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.inc0grepoz.lix4j.Script;
import com.github.inc0grepoz.lix4j.ScriptExecutor;
import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.exception.SyntaxError;
import com.github.inc0grepoz.lix4j.unit.Modifier;
import com.github.inc0grepoz.lix4j.unit.UnitFunction;
import com.github.inc0grepoz.lix4j.util.Lexer;

@SuppressWarnings("all")
class DefaultTestCase {

    private static final ScriptExecutor EXECUTOR = new ScriptExecutor();
    private static final File LOADER_DIRECTORY;

    static
    {
        EXECUTOR.setLoaderDirectory(LOADER_DIRECTORY = new File("scripts"));
    }

    private static Set<Modifier> readAllModifiers(LinkedList<String> tokens)
    {
        Modifier[] values = Modifier.values();
        Set<Modifier> set = EnumSet.noneOf(Modifier.class);

        outer:
        while (!tokens.isEmpty())
        {
            String token = tokens.peek();

            for (int i = 0; i < values.length; i++)
            {
                if (!values[i].getKeyword().equals(token))
                {
                    continue;
                }

                if (!set.add(values[i]))
                {
                    throw new SyntaxError("Used `" + values[i] + "` multiple times");
                }

                tokens.poll(); // consume the token
                continue outer; // check next token
            }

            break; // no modifier matches
        }

        return set;
    }

    @Disabled
    @Test
    void testModifiers()
    {
        LinkedList<String> tokens = new LinkedList<>();
        tokens.add("var");
        tokens.add("static");
        tokens.add("a");
        Set<Modifier> modifiers = readAllModifiers(tokens);
        System.out.println(modifiers);
        System.out.println(tokens);
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

    //@Disabled
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

        UnitFunction fn = script.getFunction("main", 0);
        Object rv = time("Executed", () -> fn.call());
    }

    @Disabled
    @Test
    void testReflection()
    {
        try
        {
            Object string = "value";
            Object[] args = { "a", "4" };
            Method m = string.getClass().getMethod("replace", CharSequence.class, CharSequence.class);
            Object rv = m.invoke(string, args);
            System.out.println(rv);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new AssertionError(t);
        }
    }

    private static <T> T time(Supplier<T> lambda)
    {
        return time(null, lambda);
    }

    private static <T> T time(String label, Supplier<T> lambda)
    {
        long time = System.currentTimeMillis();
        T rv = lambda.get();
        time = System.currentTimeMillis() - time;
        System.out.println((label == null ? "" : label + " in ") + time + " ms");
        return rv;
    }

}
