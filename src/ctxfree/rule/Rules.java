package ctxfree.rule;

import com.github.inc0grepoz.lix4j.util.Keyword;

class Rules
{

    static final Rule TOKEN_VALUE_BOOLEAN;
    static final Rule TOKEN_VALUE_NUMBER;
    static final Rule TOKEN_VALUE_STRING;
    static final Rule TOKEN_VALUE_CHAR;
    static final Rule TOKEN_VALUE;
    //static final Rule CALLABLE;
    //static final Rule CALLABLE_SEQUENCE;

    static final Rule FUNCTION;

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
        TOKEN_VALUE_BOOLEAN = new RuleTokenBoolean();
        TOKEN_VALUE_NUMBER = new RuleTokenNumber();
        TOKEN_VALUE_STRING = new RuleTokenString();
        TOKEN_VALUE_CHAR = new RuleTokenChar();

        TOKEN_VALUE = TOKEN_VALUE_BOOLEAN.clone()
                .or(TOKEN_VALUE_NUMBER.clone())
                .or(TOKEN_VALUE_STRING.clone())
                .or(TOKEN_VALUE_CHAR.clone());

        FUNCTION = new RuleTokenWord(Keyword.FUNCTION.getString());
        FUNCTION.then();
    }

}
