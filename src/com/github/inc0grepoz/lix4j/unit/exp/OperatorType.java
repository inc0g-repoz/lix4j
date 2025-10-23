package com.github.inc0grepoz.lix4j.unit.exp;

/**
 * Represents the enumerable type of an operator.
 * 
 * @author inc0g-repoz
 */
public enum OperatorType
{

    /**
     * Can only have one operand on the left.
     * Returns the evaluation result.
     */
    UNARY_LEFT(1),
    /**
     * Can only have one operand on the right.
     * Returns the value before the evaluation.
     */
    UNARY_RIGHT(1),
    /**
     * Can have two operands, but in the most
     * cases can be chained.
     */
    BINARY(2),
    /**
     * Only used by the ternary operator.
     */
    TERNARY(3);

    private final int operandCount;

    OperatorType(int operandCount)
    {
        this.operandCount = operandCount;
    }

    /**
     * Returns the minimal amount of operands
     * required for evaluation.
     * 
     * @return the operands minimal amount
     */
    public int getOperandCount()
    {
        return operandCount;
    }

}
