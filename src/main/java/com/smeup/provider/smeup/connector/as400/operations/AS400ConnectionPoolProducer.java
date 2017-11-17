package com.smeup.provider.smeup.connector.as400.operations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400ConnectionPool;
import com.ibm.as400.access.ConnectionPoolException;

@ApplicationScoped
public class AS400ConnectionPoolProducer {

    private AS400ConnectionPool as400ConnectionPool;

    @PostConstruct
    public void init() {

        final AS400ConnectionPool as400ConnectionPool = new AS400ConnectionPool();
        try {
            as400ConnectionPool.fill(getServer(), getUser(), getPassword(),
                    AS400.DATAQUEUE, 4);
            setAs400ConnectionPool(as400ConnectionPool);
        } catch (final ConnectionPoolException e) {

            throw new CommunicationException(e);
        }
    }

    @Produces @RequestScoped
    public AS400ConnectionPool getAs400ConnectionPool() {
        return this.as400ConnectionPool;
    }

    public void setAs400ConnectionPool(
            final AS400ConnectionPool as400ConnectionPool) {
        this.as400ConnectionPool = as400ConnectionPool;
    }

    public String getUser() {
        return System.getenv(AS400Producer.USER);
    }

    public String getPassword() {
        return System.getenv(AS400Producer.PASSWORD);
    }

    public String getServer() {
        return System.getenv(AS400Producer.SERVER);
    }
}
