package net.wytrem.ecs.utils;

import com.google.inject.MembersInjector;

import java.lang.reflect.Field;

/**
 * Injects a {@link Class} object into the given field of the given generic type.
 */
public class GenericTypeClassInjector<T> implements MembersInjector<T> {

    private final Field field;
    private final Class<?> clazz;

    GenericTypeClassInjector(Field field, Class<?> clazz) {
        this.field = field;
        this.clazz = clazz;
        field.setAccessible(true);
    }

    public void injectMembers(T t) {
        try {
            field.set(t, clazz);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
