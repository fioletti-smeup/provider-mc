package com.smeup.provider.smeup.connector.as400;

import java.io.IOException;

import javax.inject.Inject;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.DataQueue;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.IllegalObjectTypeException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.QSYSObjectPathName;
import com.smeup.provider.smeup.connector.as400.as400.qualifiers.SessionId;
import com.smeup.provider.smeup.connector.as400.operations.CommunicationException;

public class DataQueueWriter {

    public static final String QUEUE_LIB = "SMEUPUIDQ";

    private static final String IN_QUEUE_PREFIX = "ECTS";

    @Inject
    private AS400 as400;

    @Inject
    private Integer CCSID;

    @Inject
    @SessionId
    private String sessionId;

    public void writeToQueue(final String fun) throws CommunicationException {

        byte[] bs;
        try {
            bs = getInputCalculator().toByte(new FUNParser().parse(fun));
            createDataQueue(IN_QUEUE_PREFIX).write(bs);
        } catch (IOException | AS400SecurityException
                | ErrorCompletingRequestException | IllegalObjectTypeException
                | InterruptedException | ObjectDoesNotExistException e) {
            throw new CommunicationException(e);
        }

    }

    private DataQueue createDataQueue(final String outQueuePrefix) {

        final DataQueue dataQueue = new DataQueue(getAS400(),
                new QSYSObjectPathName(QUEUE_LIB,
                        outQueuePrefix + getSessionId(), "DTAQ").getPath());
        return dataQueue;
    }

    private InputCalculator getInputCalculator() {

        final InputCalculator inputCalculator = new InputCalculator();
        inputCalculator.setCcsid(getCCSID());
        return inputCalculator;
    }

    public AS400 getAS400() {
        return this.as400;
    }

    public void setAS400(final AS400 as400) {
        this.as400 = as400;
    }

    public Integer getCCSID() {
        return this.CCSID;
    }

    public void setCCSID(final Integer cCSID) {
        this.CCSID = cCSID;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }
}
