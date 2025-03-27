package nusri.fyp.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * This annotation is used to mark fields that should be configurable and serialized.<br>
 * It allows the specification of a default value for the annotated field, which will be used<br>
 * if the field's value is not set or is null.<br>
 * <br>
 * Fields annotated with this annotation are processed by the {@link nusri.fyp.demo.service.ConfigService} class.<br>
 *
 * @author Liu Binghong
 * @since 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
    /**
     * Specifies the default value for the annotated field. The value will be used if the field's
     * value is not set or is null.
     *
     * @return The default value as a string.
     */
    String defaultValue() default "";
}
