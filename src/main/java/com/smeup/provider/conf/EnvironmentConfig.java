package com.smeup.provider.conf;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smeup.provider.model.FixedCredentials;
import com.smeup.provider.smeup.connector.as400.operations.AS400ConnectionPoolProducer;

@ApplicationScoped
public class EnvironmentConfig implements Config {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AS400ConnectionPoolProducer.class);

    private @Inject ServletContext servletContext;

    private FixedCredentials fixedCredentials;

    @PostConstruct
    public void init() {

        final FixedCredentials fixedCredentials = new FixedCredentials();
        fixedCredentials.setServer(getServer());
        fixedCredentials.setUser(getUser());
        fixedCredentials.setPassword(getPassword());
        setFixedCredentials(fixedCredentials);
    }

    @Override
    public String getUser() {

        return getPrefixedEnvironmentVariable(Config.USER);
    }

    @Override
    public String getPassword() {
        return getPrefixedEnvironmentVariable(Config.PASSWORD);
    }

    @Override
    public String getServer() {
        return getPrefixedEnvironmentVariable(Config.SERVER);
    }

    @Override
    @Produces
    @Secret
    public String getSecret() {
        return getPrefixedEnvironmentVariable(Config.SECRET);
    }

    private String getPrefixedEnvironmentVariable(final String varName) {

        final String prefixed = getEnvironmentVariablePrefix() + varName;
        final String value = System.getenv(prefixed);
        LOGGER.info(String.format("Read variable: %s=%s", prefixed,
                Config.PASSWORD.equals(varName) || Config.SECRET.equals(varName)
                ? "***"
                        : value));
        return value;
    }

    private String getEnvironmentVariablePrefix() {

        final String contextRoot = getServletContext().getContextPath();
        return contextRoot.isEmpty() ? contextRoot
                : contextRoot.replaceAll("/", "").replaceAll("-", "_")
                .concat("_");
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Produces
    @RequestScoped
    @Override
    public FixedCredentials getFixedCredentials() {
        return this.fixedCredentials;
    }

    public void setFixedCredentials(final FixedCredentials fixedCredentials) {
        this.fixedCredentials = fixedCredentials;
    }

}
