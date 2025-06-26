package common.extensions;

import common.annotations.RetryOnFailure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;
import java.util.Optional;

public class RetryOnFailureExtension implements InvocationInterceptor {
    @Override
    public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
                                    ExtensionContext extensionContext) throws Throwable {
        int maxRetries = 1;
        Optional<RetryOnFailure> annotation = getAnnotation(invocationContext, extensionContext);
        if (annotation.isPresent()) {
            maxRetries = annotation.get().value();
        }
        int attempt = 0;
        Throwable testException = null;
        while (attempt < maxRetries) {
            try {
                invocation.proceed();
                return;
            } catch (Throwable t) {
                testException = t;
                attempt++;
                if (attempt >= maxRetries) {
                    throw testException;
                }
            }
        }
    }

    private Optional<RetryOnFailure> getAnnotation(ReflectiveInvocationContext<Method> invocationContext,
                                                   ExtensionContext extensionContext) {
        Method method = invocationContext.getExecutable();
        RetryOnFailure methodAnnotation = method.getAnnotation(RetryOnFailure.class);
        if (methodAnnotation != null) {
            return Optional.of(methodAnnotation);
        }
        Class<?> testClass = extensionContext.getRequiredTestClass();
        RetryOnFailure classAnnotation = testClass.getAnnotation(RetryOnFailure.class);
        return Optional.ofNullable(classAnnotation);
    }
}