package com.github.inc0grepoz.lix4j.ctxfree;


public class ContextFreeGrammar
{

    public static final Grammar FUNCTION = Grammar.builder()
            .keyword("function")
            .token(t -> t.matches("[a-zA-Z_][a-zA-Z0-9_]*")) // Function name
            .keyword("(")
            .token(t -> t.matches("[^)]*")) // Parameters (simplified)
            .keyword(")")
            .block(FUNCTION_BODY) // Nested rules for the body
            .build();

    public static final Grammar IF_STATEMENT = Grammar.builder()
            .keyword("if")
            .keyword("(")
            .token(t -> !t.equals(")")) // Condition
            .keyword(")")
            .block(STATEMENT) // Then-block
            .keyword("else")
            .block(STATEMENT) // Else-block (optional)
            .build();

    public static final Grammar FOR_LOOP = Grammar.builder()
            .keyword("for")
            .keyword("(")
            .token(t -> !t.equals(";")) // Initializer
            .keyword(";")
            .token(t -> !t.equals(";")) // Condition
            .keyword(";")
            .token(t -> !t.equals(")")) // Increment
            .keyword(")")
            .block(STATEMENT) // Loop body
            .build();

}
