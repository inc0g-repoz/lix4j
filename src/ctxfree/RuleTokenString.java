package ctxfree;

class RuleTokenString implements RuleToken {
    public boolean matchToken(String token) {
        return token.charAt(0) == '"' && token.charAt(token.length() - 1) == '"';
    }
}