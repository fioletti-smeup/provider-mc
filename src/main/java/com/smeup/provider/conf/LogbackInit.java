package com.smeup.provider.conf;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

@WebListener
public class LogbackInit implements ServletContextListener {

    @Override
    public void contextInitialized(final ServletContextEvent sce) {

        final LoggerContext context = (LoggerContext) LoggerFactory
                .getILoggerFactory();
        final JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(context);
        context.reset();
        final String contextPath = sce.getServletContext().getContextPath();
        context.setName(contextPath.isEmpty() ? "default" : contextPath.substring(1));

        //        context.putProperty("userHome", System.getProperty("user.home"));
        try {
            jc.doConfigure(
                    getClass().getClassLoader().getResource("/logback.xml"));
        } catch (final JoranException e) {
            throw new Error(e);
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {

        //TODO Have I to close the logger context?
    }
}
