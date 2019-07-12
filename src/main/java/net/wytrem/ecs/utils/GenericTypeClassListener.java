package net.wytrem.ecs.utils;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import net.wytrem.ecs.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericTypeClassListener implements TypeListener {

    @Inject
    World world;

    @Override
    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
        if (typeLiteral.getType() instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) typeLiteral.getType();

            Type[] actualTypeArgs = parameterizedType.getActualTypeArguments();
            Class<?> clazz = typeLiteral.getRawType();

            if (clazz != Mapper.class) {
                return;
            }
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(ComponentTypeClass.class)) {
                    if (actualTypeArgs[0] instanceof Class) {
                        Class genericTypeClass = (Class) (actualTypeArgs[0]);
                        typeEncounter.register(new ComponentTypeClassInjector<I>(field, genericTypeClass));
                    }
                }
            }

        }
    }
}
