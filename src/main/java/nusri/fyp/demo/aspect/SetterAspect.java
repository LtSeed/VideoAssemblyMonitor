package nusri.fyp.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import nusri.fyp.demo.annotation.Config;
import nusri.fyp.demo.service.ConfigService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

import static org.apache.tomcat.util.IntrospectionUtils.convert;

/**
 * Aspect for managing configuration updates and default value retrieval in the application.<br>
 * This class intercepts setter and getter methods in the {@link ConfigService} class to update<br>
 * configuration when a setter is called and to retrieve default values for null fields when a getter is called.<br>
 * <br>
 * This aspect uses AOP to modify the behavior of configuration-related methods, ensuring that<br>
 * configurations are consistently updated and that default values are returned when necessary.<br>
 * <br>
 * @author Liu Binghong
 * @since 1.0
 */
@Slf4j
@Aspect
@Component
public class SetterAspect {

    private final ConfigService configService;

    /**
     * Constructs a {@link SetterAspect} with the given {@link ConfigService}.
     *
     * @param configService The ConfigService to use for updating configurations.
     */
    public SetterAspect(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Pointcut for matching setter methods in the {@link ConfigService}.
     * <p>Matches all methods starting with 'set' in the ConfigService class.</p>
     */
    @Pointcut("execution(* nusri.fyp.demo.service.ConfigService.set*(..))")
    public void configServiceSetters() {}


    /**
     * Around advice for setter methods.<br>
     * This advice updates the configuration every time a setter method is called.<br>
     *
     * @param joinPoint The join point representing the method execution.
     * @return The result of the method execution.
     * @throws Throwable If any error occurs during method execution.
     */
    @Around("configServiceSetters()")
    public Object aroundSetter(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        configService.updateConfig();
        return result;
    }

    /**
     * Pointcut for matching getter methods in the {@link ConfigService}.<br>
     * Matches all methods starting with 'get' in the ConfigService class.<br>
     */
    @Pointcut("execution(* nusri.fyp.demo.service.ConfigService.get*(..))")
    public void configServiceGetters() {}


    /**
     * Around advice for getter methods.<br>
     * This advice checks if a getter method returns null and, if so, attempts to return
     * the default value specified in the {@link Config} annotation for the corresponding field.<br>
     * <br>
     * @param joinPoint The join point representing the method execution.
     * @return The result of the method execution or the default value if the result is null.
     * @throws Throwable If any error occurs during method execution.
     */
    @Around("configServiceGetters()")
    public Object aroundGetter(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (result == null) {
            Object target = joinPoint.getTarget();
            String methodName = joinPoint.getSignature().getName();
            if (methodName.startsWith("get") && methodName.length() > 3) {
                String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
                try {
                    Field field = target.getClass().getDeclaredField(fieldName);
                    if (field.isAnnotationPresent(Config.class)) {
                        Config config = field.getAnnotation(Config.class);
                        String defaultValue = config.defaultValue();
                        if (!defaultValue.isEmpty()) {
                            return convert(defaultValue, field.getType());
                        }
                    }
                } catch (NoSuchFieldException e) {
                    log.error(e.getMessage());
                    throw new RuntimeException("NoSuchFieldException " + e.getMessage());
                }
            }
        }
        return result;
    }
}
