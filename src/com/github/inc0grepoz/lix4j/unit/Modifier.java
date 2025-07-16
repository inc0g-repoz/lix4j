package com.github.inc0grepoz.lix4j.unit;

public enum Modifier
{

    STATIC, VAR;

    private final String keyword = name().toString().toLowerCase();

    public String getKeyword()
    {
        return keyword;
    }

}
