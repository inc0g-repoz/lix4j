package ctxfree;

import java.util.Iterator;
import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.unit.exp.Operator;

import ctxfree.lex.AST;
import ctxfree.lex.Lexer;

public class TokenHelper
{

    public static LinkedList<AST.Node> readUntilLineBreaker(Iterator<AST.Node> iter)
    {
        LinkedList<AST.Node> out = new LinkedList<>();
        AST.Node next;

        while (iter.hasNext())
        {
            next = iter.next();

            if (!next.isGroup()
                    && next.getTokenType() == Lexer.TokenType.SPECIAL_CHARACTER
                    && next.getChar() == ';')
            {
                break;
            }

            out.add(next);
        }

        return out;
    }

    public static LinkedList<AST.Node> readUntilLineBreaker(LinkedList<AST.Node> flow)
    {
        LinkedList<AST.Node> out = new LinkedList<>();
        AST.Node next;

        while (!flow.isEmpty())
        {
            next = flow.poll();

            if (!next.isGroup()
                    && next.getTokenType() == Lexer.TokenType.SPECIAL_CHARACTER
                    && next.getChar() == ';')
            {
                break;
            }

            out.add(next);
        }

        return out;
    }

    @SuppressWarnings("unchecked")
    public static LinkedList<AST.Node>[] splitOperands(LinkedList<AST.Node> flow,
            Operator operator)
    {
        LinkedList<LinkedList<AST.Node>> out = new LinkedList<>();
        LinkedList<AST.Node> write = new LinkedList<>();
        Iterator<AST.Node> iter = flow.iterator();
        AST.Node next;

        while (iter.hasNext())
        {
            next = iter.next();

            if (next.isGroup() || !operator.isNameMatching((Lexer.Token) next))
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

        LinkedList<AST.Node>[] arr = new LinkedList[out.size()];
        out.toArray(arr);
        return arr;
    }

}
