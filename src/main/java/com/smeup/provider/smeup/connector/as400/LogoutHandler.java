package com.smeup.provider.smeup.connector.as400;

import java.beans.PropertyVetoException;
import java.io.IOException;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.ProgramCall;
import com.ibm.as400.access.ProgramParameter;
import com.smeup.provider.log.Logged;
import com.smeup.provider.model.CommunicationException;
import com.smeup.provider.model.SmeupSession;
import com.smeup.provider.smeup.connector.as400.operations.ProgramCallHandler;

@Logged
public class LogoutHandler {

    @Inject
    private SmeupSession smeupSession;

    private static final String[] DESTRUCTION_PARAMS = {
            String.format("%1$-" + 10 + "s", "JA"),
            String.format("%1$-" + 10 + "s", "DATSES"),
            String.format("%1$-" + 10 + "s", "DIS"),
            String.format("%1$-" + 10 + "s", "MAS"),
            String.format("%1$-" + 15 + "s", ""),
            String.format("%1$-" + 15 + "s", ""),
            String.format("%1$-" + 512 + "s", ""),
            String.format("%1$-" + 128 + "s", ""),
            String.format("%1$-" + 1024 + "s", "") };

    @Inject
    private ProgramCallHandler programCallHandler;

    @Inject
    private Instance<AS400> as400;

    public void logout() {

        final ProgramCall call = getProgramCallHandler()
                .createCall(getAS400().get(), DESTRUCTION_PARAMS);
        try {
            final String texts_6 = DESTRUCTION_PARAMS[6].substring(0, 35)
                    + getSmeupSession().getSessionId()
                    + DESTRUCTION_PARAMS[6].substring(41);
            final ProgramParameter[] params = call.getParameterList();
            params[6].setInputData(new AS400Text(texts_6.length(),
                    getSmeupSession().getCCSID().intValue()).toBytes(texts_6));
            call.setParameterList(params);
            call.run();

        } catch (AS400SecurityException | ErrorCompletingRequestException
                | IOException | InterruptedException
                | ObjectDoesNotExistException | PropertyVetoException e) {

            throw new CommunicationException(e);
        }
    }

    public ProgramCallHandler getProgramCallHandler() {
        return this.programCallHandler;
    }

    public void setProgramCallHandler(
            final ProgramCallHandler programCallHandler) {
        this.programCallHandler = programCallHandler;
    }

    public SmeupSession getSmeupSession() {
        return this.smeupSession;
    }

    public void setSmeupSession(final SmeupSession smeupSession) {
        this.smeupSession = smeupSession;
    }

    public Instance<AS400> getAS400() {
        return this.as400;
    }

    public void setAs400(final Instance<AS400> as400) {
        this.as400 = as400;
    }
}
