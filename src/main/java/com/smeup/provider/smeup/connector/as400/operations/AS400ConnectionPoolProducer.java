package com.smeup.provider.smeup.connector.as400.operations;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400ConnectionPool;
import com.ibm.as400.access.ConnectionPoolException;
import com.smeup.provider.model.FixedCredentials;

@ApplicationScoped
public class AS400ConnectionPoolProducer {

    private static final Logger LOGGER = Logger
            .getLogger(AS400ConnectionPoolProducer.class.getName());

    private @Inject ServletContext servletContext;

    private AS400ConnectionPool as400ConnectionPool;

    private FixedCredentials fixedCredentials;

    @PostConstruct
    public void init() {

        final FixedCredentials fixedCredentials = new FixedCredentials();
        fixedCredentials.setServer(getServer());
        fixedCredentials.setUser(getUser());
        fixedCredentials.setPassword(getPassword());

        final AS400ConnectionPool as400ConnectionPool = new AS400ConnectionPool();
        try {
            as400ConnectionPool.fill(fixedCredentials.getServer(),
                    fixedCredentials.getUser(), fixedCredentials.getPassword(),
                    AS400.DATAQUEUE, 4);
            setAs400ConnectionPool(as400ConnectionPool);
            setFixedCredentials(fixedCredentials);
        } catch (final ConnectionPoolException e) {

            throw new CommunicationException(e);
        }
    }

    @Produces
    @ApplicationScoped
    public AS400ConnectionPool getAs400ConnectionPool() {
        return this.as400ConnectionPool;
    }

    public void setAs400ConnectionPool(
            final AS400ConnectionPool as400ConnectionPool) {
        this.as400ConnectionPool = as400ConnectionPool;
    }

    public String getUser() {

        return getPrefixedEnvironmentVariable(AS400Producer.USER);
    }

    public String getPassword() {
        return getPrefixedEnvironmentVariable(AS400Producer.PASSWORD);
    }

    public String getServer() {
        return getPrefixedEnvironmentVariable(AS400Producer.SERVER);
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    private String getPrefixedEnvironmentVariable(final String varName) {

        final String prefixed = getEnvironmentVariablePrefix() + varName;
        final String value = System.getenv(prefixed);
        LOGGER.info(String.format("Read variable: %s=%s", prefixed,
                AS400Producer.PASSWORD.equals(varName) ? "***" : value));
        return value;
    }

    private String getEnvironmentVariablePrefix() {

        final String contextRoot = getServletContext().getContextPath();
        return contextRoot.isEmpty() ? contextRoot
                : contextRoot.replaceAll("/", "").replaceAll("-", "_")
                .concat("_");
    }

    @Produces
    public FixedCredentials getFixedCredentials() {
        return this.fixedCredentials;
    }

    public void setFixedCredentials(final FixedCredentials fixedCredentials) {
        this.fixedCredentials = fixedCredentials;
    }
}
