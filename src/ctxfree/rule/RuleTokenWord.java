package ctxfree.rule;

class RuleTokenWord extends RuleToken
{

    private final String word;

    RuleTokenWord(String word)
    {
        this.word = word;
    }

    @Override
    public RuleTokenWord clone()
    {
        return new RuleTokenWord(word);
    }

    @Override
    public boolean matchToken(String token)
    {
        return word.equals(token);
    }

}
