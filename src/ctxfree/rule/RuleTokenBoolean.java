package ctxfree.rule;

import com.github.inc0grepoz.lix4j.util.Keyword;

class RuleTokenBoolean extends RuleToken
{

    @Override
    public RuleTokenBoolean clone()
    {
        return new RuleTokenBoolean();
    }

    @Override
    public boolean matchToken(String token)
    {
        return Keyword.TRUE.getString().equals(token)
            || Keyword.FALSE.getString().equals(token);
    }

}
