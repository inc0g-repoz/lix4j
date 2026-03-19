package ctxfree.rule;

import com.github.inc0grepoz.lix4j.util.Patterns;

class RuleTokenNumber extends RuleToken
{

    @Override
    public Rule clone()
    {
        return new RuleTokenNumber();
    }

    @Override
    public boolean matchToken(String token)
    {
        return Patterns.NUMBER.matcher(token).matches();
    }

}
