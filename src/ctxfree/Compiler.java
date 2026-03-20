package ctxfree;

import java.util.LinkedList;

import com.github.inc0grepoz.lix4j.exception.SyntaxError;
import com.github.inc0grepoz.lix4j.unit.Unit;

import ctxfree.ast.AST2;
import ctxfree.ast.AST2.GroupType;

public class Compiler {

    public Unit compileNext(AST2.TokenGroup flow) {

        if (flow.isGroup()) {

            LinkedList<AST2.Node> children = flow.getChildren();
            AST2.Node first = children.peek();

            if (first.isGroup()) {
                if (first.getGroupType() != GroupType.CURLY)
                {
                    SyntaxError.unexpectedTokenGroupType(first);
                }

                // it's just a block
                
            } else {
                
            }

        }



        // THERE WILL ALSO BE CODE FOR
        // LITERALS LATER
        else {
            
        }

        return null;
    }

}

class U {
    
}

class UReturn {
    
}

class USection extends U {
    LinkedList<Unit> children = new LinkedList<>();
}
