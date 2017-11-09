package com.smeup.provider.smeup.connector.as400;

import java.io.IOException;

import javax.inject.Inject;

import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.ProgramCall;
import com.smeup.provider.smeup.connector.as400.operations.CommunicationException;
import com.smeup.provider.smeup.connector.as400.operations.ProgramCallHandler;

public class LogoutHandler {

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

    public void disconnect() {

        final ProgramCall call = getProgramCallHandler()
                .createCall(DESTRUCTION_PARAMS);
        try {
            call.run();

        } catch (AS400SecurityException | ErrorCompletingRequestException
                | IOException | InterruptedException
                | ObjectDoesNotExistException e) {

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
}
