package net.wytrem.ecs.utils;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
/**
 * Used to infer the generic type into a field, when an instance is created via {@link com.google.inject.Injector}.
 */
public @interface InjectGenericTypeClass {

}
