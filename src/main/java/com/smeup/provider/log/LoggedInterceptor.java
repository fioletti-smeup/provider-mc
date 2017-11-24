package com.smeup.provider.log;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.interceptor.AroundConstruct;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Logged
@Interceptor
public class LoggedInterceptor implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory
            .getLogger(LoggedInterceptor.class);

    @AroundConstruct
    public void logConstruction(final InvocationContext ctx) throws Exception {

        final String className = ctx.getConstructor().getDeclaringClass().getName();
        LOGGER.info("Constructing class: " + className);
        ctx.proceed();
    }

    @AroundInvoke
    public Object logMethodEntry(final InvocationContext ctx)
            throws Exception {

        final Method method = ctx.getMethod();
        final String methodName = "#" + method.getDeclaringClass() + "."
                + method.getName();
        LOGGER.info("Entering method: " + methodName);

        final long time = System.currentTimeMillis();
        final Object object = ctx.proceed();

        LOGGER.info("Time elapsed executing method " + methodName + " "
                + (System.currentTimeMillis() - time) + "ms");
        return object;
    }
}
