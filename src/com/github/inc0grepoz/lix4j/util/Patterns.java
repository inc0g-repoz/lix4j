package com.github.inc0grepoz.lix4j.util;

import java.util.regex.Pattern;

public class Patterns
{

    public static final Pattern NUMBER = Pattern.compile("(\\d*\\.)?(\\d+)[LlFfDd]?");
    public static final Pattern NUMBER_INT = Pattern.compile("\\d+");
    public static final Pattern NUMBER_LONG = Pattern.compile("(\\d+)[Ll]");
    public static final Pattern NUMBER_FLOAT = Pattern.compile("(\\d*\\.?\\d+)[Ff]");
    public static final Pattern NUMBER_DOUBLE = Pattern.compile("(\\d*\\.?\\d+)[Dd]?");

    private Patterns()
    {
        throw new UnsupportedOperationException("Utility class");
    }

}
