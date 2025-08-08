package com.github.inc0grepoz.lix4j.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.StringJoiner;

public class Reflection
{

    public static Constructor<?> findConstructor(Class<?> clazz, Object[] params, Class<?>[] classes)
    {
        try
        {
            return clazz.getConstructor(classes);
        }
        catch (Throwable t1)
        {
            try
            {
                return clazz.getDeclaredConstructor(classes);
            }
            catch (Throwable t2)
            {}
        }

        for (Constructor<?> next: clazz.getConstructors())
        {
            if (next.getParameterCount() == params.length
                    && matchParameterTypes(next, classes))
            {
                return next;
            }
        }

        for (Constructor<?> next: clazz.getDeclaredConstructors())
        {
            if (next.getParameterCount() == params.length
                    && matchParameterTypes(next, classes))
            {
                return next;
            }
        }

        throw new RuntimeException("Unknown constructor " + clazz.getName() + stringifyParameterTypes(classes));
    }

    public static Method findMethod(Class<?> clazz, String name, Object[] params, Class<?>[] classes)
    {
        try
        {
            return clazz.getMethod(name, classes);
        }
        catch (Throwable t)
        {}

        try
        {
            return clazz.getDeclaredMethod(name, classes);
        }
        catch (Throwable t)
        {}

        for (Method next: clazz.getMethods())
        {
            if (next.getParameterCount() == params.length
                    && next.getName().equals(name)
                    && matchParameterTypes(next, classes))
            {
                return next;
            }
        }

        for (Method next: clazz.getDeclaredMethods())
        {
            if (next.getParameterCount() == params.length
                    && next.getName().equals(name)
                    && matchParameterTypes(next, classes))
            {
                return next;
            }
        }

        throw new RuntimeException("Unknown method " + clazz.getName() + "." + name + stringifyParameterTypes(classes));
    }

    public static Field findField(Class<?> clazz, String name)
    {
        try
        {
            return clazz.getField(name);
        }
        catch (Throwable t)
        {}

        try
        {
            return clazz.getDeclaredField(name);
        }
        catch (Throwable t)
        {}

        throw new RuntimeException("Unknown field " + clazz.getName() + "." + name);
    }

    private static boolean matchParameterTypes(Executable executable, Class<?>[] paramTypes)
    {
        Class<?>[] mpt = executable.getParameterTypes();

        for (int i = 0; i < paramTypes.length; i++)
        {
            if (mpt[i].isAssignableFrom(paramTypes[i])
                    || mpt[i].isPrimitive() && PrimitiveTester.isPrimitiveClass(paramTypes[i])
//                  && mpt[i] == unwrapPrimitiveType(paramTypes[i])
                    || isFunctionalInterface(mpt[i]))
            {
                continue;
            }

            return false;
        }

        return true;
    }

    public static Class<?> unwrapPrimitiveType(Class<?> type)
    {
        if (type == Boolean.class)
        {
            return boolean.class;
        }

        if (type == Character.class)
        {
            return char.class;
        }

        if (type == Byte.class)
        {
            return byte.class;
        }

        if (type == Short.class)
        {
            return short.class;
        }

        if (type == Integer.class)
        {
            return int.class;
        }

        if (type == Long.class)
        {
            return long.class;
        }

        if (type == Float.class)
        {
            return float.class;
        }

        if (type == Double.class)
        {
            return double.class;
        }

        return type;
    }

    private static String stringifyParameterTypes(Class<?>[] classes)
    {
        if (classes.length == 0)
        {
            return "()";
        }

        StringJoiner joiner = new StringJoiner(", ", "(", ")");

        for (int i = 0; i < classes.length; i++)
        {
            joiner.add(classes[i] == null ? null : classes[i].getSimpleName());
        }

        return joiner.toString();
    }

    // Checks if a given type is a functional interface.
    public static boolean isFunctionalInterface(Class<?> type)
    {
        return type.isInterface() && type.isAnnotationPresent(FunctionalInterface.class);
    }

    // Instances of this class should never be made
    private Reflection()
    {
        throw new UnsupportedOperationException("Utility class");
    }

}
