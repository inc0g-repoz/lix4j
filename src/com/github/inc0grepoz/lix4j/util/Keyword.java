package com.github.inc0grepoz.lix4j.util;

public enum Keyword
{

    // Constants
    FALSE, NULL, TRUE,
    // Variables
    STATIC, VAR,
    // Conditional
    ELSE, IF,
    // Looping
    DO, FOR, WHILE,
    // Control flow of execution
    BREAK, CONTINUE, FUNCTION, RETURN,
    // Error handling
    CATCH, TRY,
    // Modules
    INCLUDE, NAMESPACE,
    ;

    public static Keyword fromString(String string)
    {
        for (Keyword k: values())
        {
            if (k.string.equals(string)) return k;
        }
        return null;
    }

    private final String string = name().toLowerCase();

    public String getString()
    {
        return string;
    }

}
