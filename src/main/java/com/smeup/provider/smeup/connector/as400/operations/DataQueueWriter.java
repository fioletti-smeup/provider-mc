package com.smeup.provider.smeup.connector.as400.operations;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.DataQueue;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.IllegalObjectTypeException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.QSYSObjectPathName;
import com.smeup.provider.log.Logged;
import com.smeup.provider.model.CommunicationException;
import com.smeup.provider.model.SmeupSession;
import com.smeup.provider.smeup.connector.as400.FUNParser;
import com.smeup.provider.smeup.connector.as400.InputCalculator;

@Logged
@ApplicationScoped
public class DataQueueWriter {

    public static final String QUEUE_LIB = "SMEUPUIDQ";

    private static final String IN_QUEUE_PREFIX = "ECTS";

    @Inject
    private InputCalculator inputCalculator;

    @Inject
    private Instance<AS400> as400;

    @Inject
    private SmeupSession smeupSession;

    public void writeToQueue(final String fun) throws CommunicationException {

        try {
            createDataQueue(IN_QUEUE_PREFIX).write(getInputCalculator()
                    .toDataQueueEntryAsString(new FUNParser().parse(fun)));

        } catch (IOException | AS400SecurityException
                | ErrorCompletingRequestException | IllegalObjectTypeException
                | InterruptedException | ObjectDoesNotExistException e) {
            throw new CommunicationException(e);
        }

    }

    private DataQueue createDataQueue(final String outQueuePrefix) {

        final DataQueue dataQueue = new DataQueue(getAS400().get(),
                new QSYSObjectPathName(QUEUE_LIB,
                        outQueuePrefix + getSmeupSession().getSessionId(),
                        "DTAQ").getPath());
        return dataQueue;
    }

    public Instance<AS400> getAS400() {
        return this.as400;
    }

    public void setAS400(final Instance<AS400> as400) {
        this.as400 = as400;
    }

    public SmeupSession getSmeupSession() {
        return this.smeupSession;
    }

    public void setSmeupSession(final SmeupSession smeupSession) {
        this.smeupSession = smeupSession;
    }

    public InputCalculator getInputCalculator() {
        return this.inputCalculator;
    }

    public void setInputCalculator(final InputCalculator inputCalculator) {
        this.inputCalculator = inputCalculator;
    }
}
