package com.smeup.provider.smeup.connector.as400;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.CharConverter;
import com.ibm.as400.access.DataQueue;
import com.ibm.as400.access.DataQueueEntry;
import com.ibm.as400.access.QSYSObjectPathName;
import com.smeup.provider.model.SmeupSession;
import com.smeup.provider.smeup.connector.as400.operations.CommunicationException;

public class DataQueueReader {

    private static final int TIMEOUT = 30; // Seconds

    private static final int HEADER_LENGTH = 73;

    private static final String OUT_QUEUE_PREFIX = "ESTC";

    public static final String QUEUE_LIB = "SMEUPUIDQ";

    @Inject
    private Instance<AS400> as400;

    @Inject
    private SmeupSession smeupSession;

    public String readFromQueue() throws CommunicationException {

        String buffer = "";
        final DataQueue dataQueue = createDataQueue(OUT_QUEUE_PREFIX);
        try {

            while (true) {

                final DataQueueEntry dataQueueEntry = dataQueue.read(TIMEOUT);
                if (null == dataQueueEntry) {

                    throw new RuntimeException(
                            "No response received from the queue \""
                                    + dataQueue.getName() + "\" within "
                                    + TIMEOUT + " seconds");
                }
                final String row = new CharConverter(
                        getSmeupSession().getCCSID())
                        .byteArrayToString(dataQueueEntry.getData());

                if (row.length() < HEADER_LENGTH) {

                    throw new RuntimeException(
                            "Response with buffer shorter then " + HEADER_LENGTH
                            + "(byte): " + row);
                }
                final String header = row
                        .substring(HEADER_LENGTH - 10, HEADER_LENGTH).trim();
                final String content = row.substring(HEADER_LENGTH).trim();
                buffer += content;
                if ("FINE".equalsIgnoreCase(header)) {
                    break;

                }
            }

        } catch (final Exception ex) {

            throw new CommunicationException(ex);
        }
        return buffer;
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

}
