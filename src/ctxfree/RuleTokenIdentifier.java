package ctxfree;

import java.util.List;

import com.github.inc0grepoz.lix4j.id.Namespace;

class RuleTokenIdentifier implements RuleTokens {
    public boolean matchTokens(List<String> tokens) {
        switch (tokens.size()) {
        case 1: return true;
        case 3: return tokens.get(1).equals(Namespace.SEPARATOR);
        default: return false;
        }
    }
}