package net.wytrem.ecs.utils;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Injects the generic type class into fields annotated by {@link InjectGenericTypeClass}.
 */
public class GenericTypeClassListener implements TypeListener {

    @Override
    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
        if (typeLiteral.getType() instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) typeLiteral.getType();

            Type[] actualTypeArgs = parameterizedType.getActualTypeArguments();
            Class<?> clazz = typeLiteral.getRawType();

            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(InjectGenericTypeClass.class)) {
                    if (actualTypeArgs[0] instanceof Class) {
                        Class genericTypeClass = (Class) (actualTypeArgs[0]);
                        typeEncounter.register(new GenericTypeClassInjector<I>(field, genericTypeClass));
                    }
                }
            }
        }
    }
}
