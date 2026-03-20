package ctxfree;

import java.util.Iterator;
import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.unit.exp.Operator;

import ctxfree.ast.AST2;
import ctxfree.lex.Lexer2;

public class TokenHelper
{

    public static LinkedList<AST2.Node> readUntilLineBreaker(LinkedList<AST2.Node> flow)
    {
        LinkedList<AST2.Node> out = new LinkedList<>();
        AST2.Node next;

        while (!flow.isEmpty())
        {
            next = flow.poll();

            if (!next.isGroup()
                    && next.getTokenType() == Lexer2.TokenType.SPECIAL_CHARACTER
                    && next.getChar() == ';')
            {
                break;
            }

            out.add(next);
        }

        return out;
    }

    @SuppressWarnings("unchecked")
    public static LinkedList<AST2.Node>[] splitOperands(LinkedList<AST2.Node> flow,
            Operator operator)
    {
        LinkedList<LinkedList<AST2.Node>> out = new LinkedList<>();
        LinkedList<AST2.Node> write = new LinkedList<>();
        Iterator<AST2.Node> iter = flow.iterator();
        AST2.Node next;

        while (iter.hasNext())
        {
            next = iter.next();

            if (next.isGroup() || !operator.isNameMatching((Lexer2.Token) next))
            {
                write.add(next);
            }
            else
            {
                out.add(write);
                write = new LinkedList<>();
            }
        }

        if (!write.isEmpty())
        {
            out.add(write);
        }

        LinkedList<AST2.Node>[] arr = new LinkedList[out.size()];
        out.toArray(arr);
        return arr;
    }

}
