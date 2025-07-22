package com.github.inc0grepoz.lix4j.value;

import java.lang.reflect.Array;
import java.util.function.Function;

import com.github.inc0grepoz.lix4j.ctx.ExecutionContext;
import com.github.inc0grepoz.lix4j.util.ControlFlow;
import com.github.inc0grepoz.lix4j.util.PrimitiveConverter;

/**
 * Represents a value, variable or a member access chain.
 * 
 * @author inc0g-repoz
 */
public abstract class Accessor
{

    /** Precached {@code void} return value accessor. **/
    public static final Accessor VOID = AccessorValue.of(ControlFlow.VOID);

    /** Precached {@code null} value accessor. **/
    public static final Accessor NULL = AccessorValue.of(null);

    /** Precached {@code 0} accessor for subtraction operators. **/
    public static final Accessor ZERO = AccessorValue.of(0);

    /** Precached {@code true} value accessor. **/
    public static final Accessor TRUE = AccessorValue.of(true);

    /** Precached {@code false} value accessor. **/
    public static final Accessor FALSE = AccessorValue.of(false);

    /** Precached {@code this} return value accessor. **/
    public static final Accessor THIS = new AccessorValue(null) {

        @Override
        public String toString()
        {
            return "this";
        }

        @Override
        public Object access(ExecutionContext ctx, Object src)
        {
            throw new UnsupportedOperationException("Only use \"this\" for function references");
        }

    };

    /**
     * Creates a new {@code AccessorBuilder} with an empty
     * access chain.
     * 
     * @return a new {@code AccessorBuilder}
     */
    public static AccessorBuilder builder()
    {
        return new AccessorBuilder();
    }

    static Class<?> unwrapSourceType(Object object)
    {
        Class<?> rv = object.getClass();

        return rv == AccessorNoInstance.class
                ? ((AccessorNoInstance) object).clazz
                : rv;
    }

    Accessor next, elementIndex;

    /**
     * Returns an {@code Object} accessed through this {@code Accessor}
     * instance from the specified source to be used in a linked access
     * chain or as a return value.
     * 
     * @param ctx the function {@code ExecutionContext}
     * @param src the {@code Object} source
     * @return an accessed {@code Object}
     */
    public abstract Object access(ExecutionContext ctx, Object src);

    public Object linkedAccess(ExecutionContext ctx, Object src)
    {
        Object rv = access(ctx, src);
        return next == null ? rv : next.linkedAccess(ctx, rv);
    }

    /**
     * Mutates the {@code Object} referred to by this {@code Accessor}
     * and returns the new value specified.
     * 
     * @param ctx the function {@code ExecutionContext}
     * @param src the {@code Object} source
     * @param val the new value
     * @return the new value
     */
    public Object mutate(ExecutionContext ctx, Object src, Object val)
    {
        throw new UnsupportedOperationException("Mutating " + toString());
    }

    /**
     * Mutates the {@code Object} referred to by this {@code Accessor}
     * and returns the new value from the specified function.
     * 
     * @param ctx the function {@code ExecutionContext}
     * @param src the {@code Object} source
     * @param fn  the mutator function
     * @return the new value
     */
    public Object mutate(ExecutionContext ctx, Object src, Function<Object, Object> fn)
    {
        Object cv = access(ctx, src);
        return next == null
                ? mutate(ctx, src, PrimitiveConverter.narrow(fn.apply(cv)))
                : next.mutate(ctx, src, fn);
    }

    // Accesses the source object element
    Object accessElement(ExecutionContext ctx, Object eltSrc)
    {
        int idx = ((Number) elementIndex.linkedAccess(ctx, null)).intValue();
        return Array.get(eltSrc, idx);
    }

    // Mutates the source object element
    Object mutateElement(ExecutionContext ctx, Object eltSrc, Object val)
    {
        int idx = ((Number) elementIndex.linkedAccess(ctx, null)).intValue();

        if (next == null)
        {
            val = PrimitiveConverter.convert(val, eltSrc.getClass().getComponentType());
            Array.set(eltSrc, idx, val);
            return val;
        }

        Object elt = Array.get(eltSrc, idx);
        return next.mutate(ctx, elt, val);
    }

}
