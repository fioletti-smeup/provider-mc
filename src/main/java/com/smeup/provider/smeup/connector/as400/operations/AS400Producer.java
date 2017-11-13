package com.smeup.provider.smeup.connector.as400.operations;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400ConnectionPool;
import com.ibm.as400.access.ConnectionPoolException;
import com.smeup.provider.model.SmeupSession;

@ApplicationScoped
public class AS400Producer {

    @Inject
    private SmeupSession smeupSession;

    private static final String USER = "SMEUP_USER";
    private static final String PASSWORD = "SMEUP_PASSWORD";
    private static final String SERVER = "SMEUP_SERVER";

    private AS400ConnectionPool as400ConnectionPool;

    @PostConstruct
    public void init() {

        this.as400ConnectionPool = new AS400ConnectionPool();
        try {
            this.as400ConnectionPool.fill(getServer(), getUser(), getPassword(),
                    AS400.DATAQUEUE, 8);
            this.as400ConnectionPool.fill(getServer(), getUser(), getPassword(),
                    AS400.COMMAND, 4);
        } catch (final ConnectionPoolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Produces
    @RequestScoped
    public AS400 provide() {

        AS400 as400 = null;
        try {
            if (null == getSmeupSession().getUser()) {
                as400 = getAs400ConnectionPool().getConnection(getServer(),
                        getUser(), getPassword());
            }
            else {

                as400 = getAs400ConnectionPool().getConnection(
                        getSmeupSession().getServer(),
                        getSmeupSession().getUser(),
                        getSmeupSession().getPassword());
            }

        } catch (final ConnectionPoolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return as400;

    }

    public SmeupSession getSmeupSession() {
        return this.smeupSession;
    }

    public void setSmeupSession(final SmeupSession smeupSession) {
        this.smeupSession = smeupSession;
    }

    public AS400ConnectionPool getAs400ConnectionPool() {
        return this.as400ConnectionPool;
    }

    public void setAs400ConnectionPool(
            final AS400ConnectionPool as400ConnectionPool) {
        this.as400ConnectionPool = as400ConnectionPool;
    }

    void close(@Disposes final AS400 as400) {

        getAs400ConnectionPool().returnConnectionToPool(as400);

    }

    public String getUser() {
        return System.getenv(USER);
    }

    public String getPassword() {
        return System.getenv(PASSWORD);
    }

    public String getServer() {
        return System.getenv(SERVER);
    }

}
