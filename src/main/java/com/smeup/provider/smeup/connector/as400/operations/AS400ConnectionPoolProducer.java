package com.smeup.provider.smeup.connector.as400.operations;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400ConnectionPool;
import com.ibm.as400.access.ConnectionPoolException;
import com.smeup.provider.log.Logged;
import com.smeup.provider.model.CommunicationException;
import com.smeup.provider.model.FixedCredentials;

@ApplicationScoped
@Logged
public class AS400ConnectionPoolProducer {

    private AS400ConnectionPool as400ConnectionPool;

    @Inject
    private FixedCredentials fixedCredentials;

    @PostConstruct
    public void init() {

        final FixedCredentials fixedCredentials = getFixedCredentials();

        final AS400ConnectionPool as400ConnectionPool = new AS400ConnectionPool();
        try {
            as400ConnectionPool.fill(fixedCredentials.getServer(),
                    fixedCredentials.getUser(), fixedCredentials.getPassword(),
                    AS400.DATAQUEUE, 4);
            setAs400ConnectionPool(as400ConnectionPool);
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

    @PreDestroy
    public void clean() {

        getAs400ConnectionPool().close();
    }

    public FixedCredentials getFixedCredentials() {
        return this.fixedCredentials;
    }

    public void setFixedCredentials(final FixedCredentials fixedCredentials) {
        this.fixedCredentials = fixedCredentials;
    }
}
