package com.github.inc0grepoz.lix4j.util;


public class DebugPrint
{

    private static final boolean ENABLE = true;

    public static void a(AnsiColor color, Object object)
    {
        if (!ENABLE) return;

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String[] className = stackTrace[2].getClassName().split("\\.");
        String classSimpleName = className[className.length - 1];

        System.out.print("[");
        System.out.print(classSimpleName);
        System.out.print('.');
        System.out.print(stackTrace[2].getMethodName());
        System.out.print("] ");
        System.out.print(color);
        System.out.print(object);
        System.out.print(AnsiColor.RESET);
        System.out.println();
    }

    public static void calledFrom()
    {
        if (!ENABLE) return;

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String[] className = stackTrace[2].getClassName().split("\\.");
        String classSimpleName = className[className.length - 1];

        System.out.print("[");
        System.out.print(classSimpleName);
        System.out.print('.');
        System.out.print(stackTrace[2].getMethodName());
        System.out.print("] ");
        System.out.print(AnsiColor.GREEN_BOLD);
        System.out.print("Called from ");

        className = stackTrace[3].getClassName().split("\\.");
        classSimpleName = className[className.length - 1];

        System.out.print(classSimpleName);
        System.out.print('.');
        System.out.print(stackTrace[3].getMethodName());
        System.out.print(AnsiColor.RESET);
        System.out.println();
    }

}
