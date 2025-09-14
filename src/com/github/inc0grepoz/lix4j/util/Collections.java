package com.github.inc0grepoz.lix4j.util;

import java.util.LinkedList;
import java.util.Set;

public class Collections
{

    @SuppressWarnings("rawtypes")
    public static final LinkedList EMPTY_LINKED_LIST = new LinkedList();

    @SuppressWarnings("unchecked")
    public static <T> LinkedList<T> emptyLinkedList()
    {
        return (LinkedList<T>) EMPTY_LINKED_LIST;
    }

    public static <T> Set<T> emptySet()
    {
        return java.util.Collections.emptySet();
    }

}
