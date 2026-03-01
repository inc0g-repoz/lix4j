package ctxfree;

class Rules
{

    static final Rule TOKEN_BOOLEAN;
    static final Rule TOKEN_NUMBER;
    static final Rule TOKEN_STRING;
    static final Rule TOKENS_IDENTIFIER;
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
        TOKEN_BOOLEAN = new RuleTokenBoolean();
        TOKEN_NUMBER = new RuleTokenNumber();
        TOKEN_STRING = new RuleTokenString();
        TOKENS_IDENTIFIER = new RuleTokenIdentifier();
    }

}
