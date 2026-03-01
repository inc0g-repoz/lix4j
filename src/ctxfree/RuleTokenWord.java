package ctxfree;

class RuleTokenWord implements RuleToken {
    String word;
    RuleTokenWord(String word) {
        this.word = word;
    }
    public boolean matchToken(String token) {
        return word.equals(token);
    }
}