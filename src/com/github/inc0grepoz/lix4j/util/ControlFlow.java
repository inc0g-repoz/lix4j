package com.github.inc0grepoz.lix4j.util;

/**
 * A set of values for controlling execution of scripts.
 * 
 * @author inc0g-repoz
 */
public enum ControlFlow
{

    /**
     * Used in looping constructs for breaking execution.
     */
    BREAK,
    /**
     * Used in looping constructs for continuing execution.
     */
    CONTINUE,
    /**
     * Used by script units to continue executing, if one
     * of the units returns a value.
     */
    KEEP_EXECUTING,

    /**
     * Used as a return value by functions, when a return
     * statement is not followed by any values.
     */
    VOID;

}
