package ctxfree;

import com.github.inc0grepoz.lix4j.util.Patterns;

class RuleTokenNumber implements RuleToken {
    public boolean matchToken(String token) {
        return Patterns.NUMBER.matcher(token).matches();
    }
}