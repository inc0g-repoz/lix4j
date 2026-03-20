package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.ctx.CompileTimeContext;
import com.github.inc0grepoz.lix4j.exception.SyntaxError;

import ctxfree.ast.AST2;

public class ScriptCompiler
{

    // Compiles, but doesn't enter or leave sections in the compile time varpool
    public static UnitSection compileSection(CompileTimeContext ctx, AST2.Node node, UnitSection section)
    {
        LinkedList<AST2.Node> childs = node.getChildren();

        while (!childs.isEmpty())
        {
            compileUnit(ctx, childs.poll(), section);
        }

        return section;
    }

    static UnitSection enterCompileSection(CompileTimeContext ctx, ASTNode node, UnitSection parent)
    {
        ctx.getVarpool().enterSection();
        compileSection(ctx, node, parent);

        ctx.getVarpool().exitSection(); // also exit
        return parent;
    }

    static Unit compileUnit(CompileTimeContext ctx, ASTNode node, UnitSection parent)
    {
        if (!node.hasTokens())
        {
            return enterCompileSection(ctx, node, new UnitSection(parent, ctx));
        }

        switch (node.getTokens().peek())
        {
        case "break":
            return UnitFlowBreak.compile(ctx, node, parent);
        case "catch":
            throw new SyntaxError("Every \"catch\" statement should have a \"try\" statement before");
        case "continue":
            return UnitFlowContinue.compile(ctx, node, parent);
        case "do":
            return UnitLoopDo.compile(ctx, node, parent);
        case "else":
            throw new SyntaxError("There can be no \"else\" without an \"if\"");
        case "for":
            return UnitLoopFor.compile(ctx, node, parent);
        case "function":
            return UnitFunction.compile(ctx, node, parent);
        case "if":
            return UnitConditionIf.compile(ctx, node, parent);
        case "include":
            return UnitInclude.compile(ctx, node, parent);
        case "namespace":
            return UnitNamespace.compile(ctx, node, parent);
        case "return":
            return UnitFlowReturn.compile(ctx, node, parent);
        case "switch":
            throw new SyntaxError("Switching is not supported");
        case "try":
            return UnitErrorTry.compile(ctx, node, parent);
        case "while":
            return UnitLoopWhile.compile(ctx, node, parent);
        default:
            return UnitOperation.compile(ctx, node, parent);
        }
    }

    static void appendSectionUnits(CompileTimeContext ctx, ASTNode node, UnitSection parent)
    {
        // Compiling and appending all the node children
        if (node.getNodeBreakerType() == NodeBreakerType.BLOCK)
        {
            ASTNode next = node.getParent().getChildNodes().poll();
            enterCompileSection(ctx, next, parent);
        }
        // A one-liner as a section element
        else if (!node.getTokens().isEmpty())
        {
            compileUnit(ctx, node, parent);
        }
    }

}
