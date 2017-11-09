package com.smeup.provider.smeup.connector.as400;

import javax.inject.Inject;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.CharConverter;
import com.ibm.as400.access.DataQueue;
import com.ibm.as400.access.DataQueueEntry;
import com.ibm.as400.access.QSYSObjectPathName;
import com.smeup.provider.smeup.connector.as400.as400.qualifiers.SessionId;
import com.smeup.provider.smeup.connector.as400.operations.CommunicationException;

public class DataQueueReader {

    private static final int TIMEOUT = 30; // Seconds

    private static final int HEADER_LENGTH = 73;

    private static final String OUT_QUEUE_PREFIX = "ESTC";

    public static final String QUEUE_LIB = "SMEUPUIDQ";

    @Inject
    private AS400 as400;

    @Inject
    private Integer CCSID;

    @Inject
    @SessionId
    private String sessionId;

    public String readFromQueue(final boolean RUN_Or_COM)
            throws CommunicationException {

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
                final String row = new CharConverter(getCCSID())
                        .byteArrayToString(dataQueueEntry.getData());

                if (RUN_Or_COM) {
                    if (row.length() < HEADER_LENGTH) {

                        throw new RuntimeException(
                                "Response with buffer shorter then "
                                        + HEADER_LENGTH + "(byte): " + row);
                    }
                    final String header = row
                            .substring(HEADER_LENGTH - 10, HEADER_LENGTH)
                            .trim();
                    final String content = row.substring(HEADER_LENGTH).trim();
                    buffer += content;
                    if ("FINE".equalsIgnoreCase(header)) {
                        break;

                    }
                } else {

                    break;
                }
            }

        } catch (final Exception ex) {

            throw new CommunicationException(ex);
        }
        return buffer;
    }

    private DataQueue createDataQueue(final String outQueuePrefix) {

        final DataQueue dataQueue = new DataQueue(getAS400(),
                new QSYSObjectPathName(QUEUE_LIB,
                        outQueuePrefix + getSessionId(), "DTAQ").getPath());
        return dataQueue;
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
