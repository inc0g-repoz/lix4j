package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.Script;
import com.github.inc0grepoz.lix4j.ast.AST;
import com.github.inc0grepoz.lix4j.ast.ASTNode;
import com.github.inc0grepoz.lix4j.ast.NodeBreakerType;
import com.github.inc0grepoz.lix4j.exception.SyntaxError;

public class ScriptCompiler
{

    public static void compile(AST ast, Script script, UnitRoot root)
    {
        CompileTimeContext ctx = new CompileTimeContext(script);
        compileSection_r(ctx, ast, root);
    }

    static UnitSection compileSection_r(CompileTimeContext ctx, ASTNode node, UnitSection parent)
    {
        LinkedList<ASTNode> childs = node.getChildNodes();
        ctx.getVarpool().enterSection();

        while (!childs.isEmpty())
        {
            compileUnit_r(ctx, childs.poll(), parent);
        }

        ctx.getVarpool().exitSection();
        return parent;
    }

    static Unit compileUnit_r(CompileTimeContext ctx, ASTNode node, UnitSection parent)
    {
        if (!node.hasTokens())
        {
            return compileSection_r(ctx, node, new UnitSection(parent));
        }

        switch (node.getTokens().peek())
        {
        case "break":
            return UnitFlowBreak.compile(ctx, node, parent);
        case "catch":
            throw new SyntaxError("Every \"catch\" statement should have a \"try\" statement before");
        case "continue":
            return UnitFlowContinue.compile(ctx, node, parent);
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

    static void appendSectionUnits(CompileTimeContext ctx, ASTNode node, Unit parent)
    {
        if (parent instanceof UnitSection)
        {
            UnitSection section = (UnitSection) parent;

            if (node.getNodeBreakerType() == NodeBreakerType.BLOCK)
            {
                ASTNode next = node.getParent().getChildNodes().poll();
                compileSection_r(ctx, next, section);
            }
            else
            {
                if (!node.getTokens().isEmpty())
                {
                    compileUnit_r(ctx, node, section);
                }
            }
        }
        else
        {
            throw new IllegalStateException("An unexpected block of code");
        }
    }

}
