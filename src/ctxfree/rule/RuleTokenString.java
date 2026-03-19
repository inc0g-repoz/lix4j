package ctxfree.rule;

class RuleTokenString extends RuleToken
{

    @Override
    public Rule clone() {
        return new RuleTokenString();
    }

    @Override
    public boolean matchToken(String token)
    {
        return token.charAt(0) == '"' && token.charAt(token.length() - 1) == '"';
    }

}
