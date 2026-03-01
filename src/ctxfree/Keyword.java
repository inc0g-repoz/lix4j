package ctxfree;

enum Keyword {
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
    private final String string = name().toLowerCase();
    public String getString() {
        return string;
    }
}
