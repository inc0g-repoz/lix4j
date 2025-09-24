package com.github.inc0grepoz.lix4j.util;

import java.util.function.Predicate;

public class Predicates
{

    @SuppressWarnings("rawtypes")
    public static final Predicate ALWAYS_TRUE = x -> true;

    @SuppressWarnings("rawtypes")
    public static final Predicate ALWAYS_FALSE = x -> false;

    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> alwaysTrue()
    {
        return (Predicate<T>) ALWAYS_TRUE;
    }

    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> alwaysFalse()
    {
        return (Predicate<T>) ALWAYS_FALSE;
    }

}
