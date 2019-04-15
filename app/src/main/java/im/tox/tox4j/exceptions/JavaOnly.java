package im.tox.tox4j.exceptions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark error codes in Java exception enums as Java-only, so they are not emitted as part of the error
 * code conversion fragments in C++ (see {@link im.tox.tox4j.impl.jni.codegen.JniErrorCodes}).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JavaOnly {
    /**
     * This is just here so the annotation is retained at runtime.
     */
    String value() default "";
}
