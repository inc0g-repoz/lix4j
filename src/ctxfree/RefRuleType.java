package ctxfree;

import java.util.function.Consumer;

import com.github.inc0grepoz.lix4j.ast.ASTNode;

enum RefRuleType {
    VAROP(r -> r
        .varop()
        ),
    FOR_LOOP(r -> r
        .keyword(Keyword.FOR)
        .inParentheses('(', ')')
            .varop().separator(';')
            .varop().separator(';')
            .varop()
            .then()
        .block()
        ),
    FOR_LOOP_ENHANCED(r -> r
        .keyword(Keyword.FOR)
        .inParentheses('(', ')')
            .varopDeclare().separator(':')
            .varop()
            .then()
        .block()
        ),
    WHILE(r -> r
        .keyword(Keyword.WHILE)
        .inParentheses('(', ')')
            .varop()
            .then()
        .block()
        ),
    DO(r -> r
        .keyword(Keyword.DO)
        .block()
        ),
    DO_WHILE(r -> r
        .keyword(Keyword.DO)
        .block()
        .keyword(Keyword.WHILE)
        .inParentheses('(', ')')
            .varop()
            .then()
        ),
    IF(r -> r
        .keyword(Keyword.IF)
        .inParentheses('(', ')')
            .varop()
            .then()
        .block()
        ),
    IF_ELSE(r -> r
        .keyword(Keyword.IF)
        .inParentheses('(', ')')
            .varop()
            .then()
        .block()
        .keyword(Keyword.ELSE)
        .block()
        ),
    ;
    private final Rule rule;
    RefRuleType() {
        this.rule = null;
    }
    RefRuleType(Consumer<RuleBuilder> consumer) {
        RuleBuilder rb = new RuleBuilder();
        consumer.accept(rb);
        rule = rb.build();
    }
    public Rule getRule() {
        return rule;
    }
    public boolean isMatching(ASTNode node) {
        return false;
    }
}