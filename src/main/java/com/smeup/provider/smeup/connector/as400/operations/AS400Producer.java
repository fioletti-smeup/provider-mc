package com.smeup.provider.smeup.connector.as400.operations;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400ConnectionPool;
import com.ibm.as400.access.ConnectionPoolException;
import com.smeup.provider.model.Credentials;
import com.smeup.provider.model.FixedCredentials;
import com.smeup.provider.smeup.connector.as400.as400.qualifiers.OfUser;

@RequestScoped
public class AS400Producer {

    @Inject
    private Instance<AS400ConnectionPool> as400ConnectionPool;

    @Inject
    private Instance<Credentials> credentials;

    @Inject
    private Instance<FixedCredentials> fixedCredentials;

    @Produces
    @OfUser
    @RequestScoped
    public AS400 provideForUser() {

        final FixedCredentials fixedCredentials = getFixedCredentials().get();
        final Credentials credentials = getCredentials().get();

        return retrieveAS400(fixedCredentials.getServer(),
                credentials.getUser(), credentials.getPassword());
    }

    @Produces
    @RequestScoped
    public AS400 provide() {

        final FixedCredentials fixedCredentials = getFixedCredentials().get();

        return retrieveAS400(fixedCredentials.getServer(),
                fixedCredentials.getUser(), fixedCredentials.getPassword());
    }

    private AS400 retrieveAS400(final String server, final String user,
            final String password) {

        AS400 as400;
        try {

            as400 = getAs400ConnectionPool().get().getConnection(server, user,
                    password);

        } catch (final ConnectionPoolException e) {

            throw new CommunicationException(e);
        }
        return as400;
    }

    public Instance<AS400ConnectionPool> getAs400ConnectionPool() {
        return this.as400ConnectionPool;
    }

    public void setAs400ConnectionPool(
            final Instance<AS400ConnectionPool> as400ConnectionPool) {
        this.as400ConnectionPool = as400ConnectionPool;
    }

    void close(@Disposes final AS400 as400) {

        getAs400ConnectionPool().get().returnConnectionToPool(as400);
    }

    void closeOfUserAS400(@Disposes @OfUser final AS400 as400) {

        getAs400ConnectionPool().get().returnConnectionToPool(as400);
    }

    public Instance<Credentials> getCredentials() {
        return this.credentials;
    }

    public void setCredentials(final Instance<Credentials> credentials) {
        this.credentials = credentials;
    }

    public Instance<FixedCredentials> getFixedCredentials() {
        return this.fixedCredentials;
    }

    public void setFixedCredentials(
            final Instance<FixedCredentials> fixedCredentials) {
        this.fixedCredentials = fixedCredentials;
    }
}
