package ctxfree;

class RuleTokenBoolean implements RuleToken {
    public boolean matchToken(String token) {
        return Keyword.TRUE.getString().equals(token)
            || Keyword.FALSE.getString().equals(token);
    }
}