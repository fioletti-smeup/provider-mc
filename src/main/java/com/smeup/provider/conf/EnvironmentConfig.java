package com.smeup.provider.conf;

import java.time.Duration;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smeup.provider.model.FixedCredentials;
import com.smeup.provider.smeup.connector.as400.operations.AS400ConnectionPoolProducer;

@ApplicationScoped
@Alternative
public class EnvironmentConfig implements Config {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AS400ConnectionPoolProducer.class);

    public static final String USER = "SMEUP_USER";
    public static final String PASSWORD = "SMEUP_PASSWORD";
    public static final String SERVER = "SMEUP_SERVER";
    public static final String SECRET = "SMEUP_SECRET";
    // Minutes
    public static final String TOKEN_DURATION = "SMEUP_TOKEN_DURATION";

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

        return getPrefixedEnvironmentVariable(USER);
    }

    @Override
    public String getPassword() {
        return getPrefixedEnvironmentVariable(PASSWORD);
    }

    @Override
    public String getServer() {
        return getPrefixedEnvironmentVariable(SERVER);
    }

    @Override
    @Produces
    @RequestScoped
    public TokenConfig getTokenConfig() {

        final TokenConfig tokenConfig = new TokenConfig();
        tokenConfig.setSecret(getPrefixedEnvironmentVariable(SECRET));

        getAsInteger(getPrefixedEnvironmentVariable(TOKEN_DURATION))
        .ifPresent(v -> tokenConfig.setDuration(Duration.ofMinutes(v)));

        return tokenConfig;
    }

    private Optional<Integer> getAsInteger(final String v) {

        Optional<Integer> value;

        try {
            final Integer integer = Integer.valueOf(v);
            value = Optional.of(integer);
        } catch (final NumberFormatException formatException) {
            LOGGER.warn("Token Duration not explicitly supplied");
            value = Optional.empty();
        }
        return value;
    }

    private String getPrefixedEnvironmentVariable(final String varName) {

        final String prefixed = getEnvironmentVariablePrefix() + varName;
        final String value = System.getenv(prefixed);
        LOGGER.info(String.format("Read variable: %s=%s", prefixed,
                PASSWORD.equals(varName) || SECRET.equals(varName) ? "***"
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
