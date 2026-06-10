package ctxfree;

import java.util.ListIterator;

import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.exception.SyntaxError;
import com.github.inc0grepoz.lix4j.lookup.id.LexedIdentifier;
import com.github.inc0grepoz.lix4j.unit.Unit;
import com.github.inc0grepoz.lix4j.unit.UnitFlowBreak;
import com.github.inc0grepoz.lix4j.unit.UnitFlowContinue;
import com.github.inc0grepoz.lix4j.unit.UnitSection;
import com.github.inc0grepoz.lix4j.util.Keyword;

import ctxfree.lex.AST;
import ctxfree.lex.Lexer;

public class Compiler {

    public static void root(CompileTimeContext ctx, AST.TokenGroup root) {
        ListIterator<AST.Node> iter = root.getChildren().listIterator();
        AST.Node node;

        
    }

    public static Unit unit(CompileTimeContext ctx, AST.TokenGroup from, UnitSection to) {
        ListIterator<AST.Node> iter = from.getChildren().listIterator();
        AST.Node node = iter.next();

        if (node.isGroup()) {
            if (node.getGroupType() == AST.GroupType.CURLY) {
                // a scope
                UnitSection usec = new UnitSection(to);
                AST.TokenGroup tkgrp = (AST.TokenGroup) node;
                node.getChildren().forEach(child -> unit(ctx, tkgrp, usec));
                return (UnitSection) usec;
            }

            // not a scope
            StringBuilder message = new StringBuilder();
            message.append("Unexpected group type (");
            message.append(node.getGroupType().name());
            message.append("), line: ");
            message.append(node.getLine());
            throw new SyntaxError(message.toString());
        }

        // THE HEAD NODE IS A TOKEN

        if (node.getTokenType() == Lexer.TokenType.KEYWORD) {
            switch (node.getKeyword()) {
            case BREAK:
                return new UnitFlowBreak(to);
            case CATCH:
                throw new SyntaxError("Every \"catch\" statement should have a \"try\" statement before");
            case CONTINUE:
                return new UnitFlowContinue(to);
            case DO:
                break;
            case ELSE:
                throw new SyntaxError("There can be no \"else\" without an \"if\"");
            case FALSE:
                break;
            case FOR:
                break;
            case FUNCTION:
                break;
            case IF:
                break;
            case INCLUDE:
                break;
            case NAMESPACE:
                break;
            case NULL:
                break;
            case RETURN:
                break;

            case STATIC:
                node = iter.next();
                if (node.isGroup() || node.getTokenType() != Lexer.TokenType.KEYWORD) {
                    throw new SyntaxError("Expected variable or function declaration: line " + node.getLine());
                }

                if (node.getKeyword() == Keyword.VAR) {
                    node = iter.next();
                    if (node.getTokenType() != Lexer.TokenType.IDENTIFIER) {
                        throw new SyntaxError("Expected variable identifier: line " + node.getLine());
                    }
                    LexedIdentifier id = node.getLexedIdentifier();
                }

                if (node.getKeyword() == Keyword.FUNCTION) {
                    
                }
                break;

            case SWITCH:
                throw new SyntaxError("Switching is not supported");
            case TRUE:
                break;
            case TRY:
                break;
            case VAR:
                break;
            case WHILE:
                break;
            default:
                break;
            }
        }

        return null;
    }

    public static void operation(CompileTimeContext ctx, AST.TokenGroup node) {
        
    }

    public static Object token(CompileTimeContext ctx, AST.Node node) {
        switch (node.getTokenType()) {
        case IDENTIFIER:
            break;
        case KEYWORD:
            break;
        case LITERAL_BOOLEAN:
            break;
        case LITERAL_CHAR:
            break;
        case LITERAL_NULL:
            break;
        case LITERAL_NUMBER:
            break;
        case LITERAL_STRING:
            break;
        case SPECIAL_CHARACTER:
            break;
        case SPECIAL_COMPOSITE:
            break;
        default:
            break;
        }
        return null;
    }

}
