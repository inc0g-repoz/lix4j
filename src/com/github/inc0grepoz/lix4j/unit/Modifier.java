package com.github.inc0grepoz.lix4j.unit;

/**
 * Represents a certain type of a modifier applied to a variable.
 * 
 * @author inc0g-repoz
 */
public enum Modifier
{

    /**
     * Indicates that a variable is static (doesn't require lookups
     * in the varpool for accessing).
     */
    STATIC,
    /**
     * Indicates that a variable is fresh (was just created) where
     * used (purely for clarity in the current version).
     */
    VAR;

    private final String keyword = name().toString().toLowerCase();

    /**
     * Returns the lowercase string used as the keyword for this
     * modifier in code.
     * 
     * @return a {@code String} value
     */
    public String getKeyword()
    {
        return keyword;
    }

}
