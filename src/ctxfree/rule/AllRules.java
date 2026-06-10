package ctxfree.rule;

import static ctxfree.rule.Helper.*;

import com.github.inc0grepoz.lix4j.util.Keyword;

class AllRules
{

    /*
    VAROP(r -> r
        .varop()
        ),
    FOR_LOOP(r -> r
        .keyword(Keyword.FOR)
        .inParentheses()
            .varop().word(":")
            .varop().word(":")
            .varop()
            .then()
        .block()
        ),
    FOR_LOOP_ENHANCED(r -> r
        .keyword(Keyword.FOR)
        .inParentheses()
            .varopDeclare().word(":")
            .varop()
            .then()
        .block()
        ),
    WHILE(r -> r
        .keyword(Keyword.WHILE)
        .inParentheses()
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
        .inParentheses().varop().then()
        ),
    IF(r -> r
        .keyword(Keyword.IF)
        .inParentheses()
            .varop()
            .then()
        .block()
        ),
    IF_ELSE(r -> r
        .keyword(Keyword.IF)
        .inParentheses().varop().then()
        .block()
        .keyword(Keyword.ELSE)
        .block()
        ),
    ;
    private final Rule rule;
    RefRuleType() {
        this.rule = null;
    }
    RefRuleType(Consumer<RuleListStructure.Builder> consumer) {
        RuleListStructure.Builder rb = new RuleListStructure.Builder(null);
        consumer.accept(rb);
        rule = rb.build();
    }
    public Rule getRule() {
        return rule;
    }
    public boolean isMatching(ASTNode node) {
        return false;
    }
    */

    static final Rule LITERAL_BOOLEAN = bool();
    static final Rule LITERAL_CHAR = character();
    static final Rule LITERAL_NUMBER = number();
    static final Rule LITERAL_STRING = string();
    static final Rule LITERAL = literal();
    //static final Rule CALLABLE;
    //static final Rule CALLABLE_SEQUENCE;

    //static final Rule FUNCTION;

    //static final Rule VAROP;
    //static final Rule FOR_LOOP;
    //static final Rule FOR_LOOP_ENHANCED;
    //static final Rule WHILE;
    //static final Rule DO;
    //static final Rule DO_WHILE;
    //static final Rule IF;
    //static final Rule IF_ELSE;

    static
    {
        keyword(Keyword.IF);
    }

}
