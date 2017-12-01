package com.smeup.provider.smeup.connector.as400.operations;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CharConverter;
import com.ibm.as400.access.DataQueue;
import com.ibm.as400.access.DataQueueEntry;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.IllegalObjectTypeException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.QSYSObjectPathName;
import com.smeup.provider.log.Logged;
import com.smeup.provider.model.CommunicationException;
import com.smeup.provider.model.SmeupSession;

@Logged
@ApplicationScoped
public class DataQueueReader {

    private static final int TIMEOUT = 300; // Seconds

    private static final int HEADER_LENGTH = 73;

    private static final String OUT_QUEUE_PREFIX = "ESTC";

    private static final int END_STRING_POSITION = HEADER_LENGTH - 10;

    private static final String END_STRING = "FINE";

    public static final String QUEUE_LIB = "SMEUPUIDQ";

    @Inject
    private Instance<AS400> as400;

    @Inject
    private SmeupSession smeupSession;

    public void readFromQueue(final Writer writer) {

        try {
            _readFromQueue(writer);
        } catch (AS400SecurityException | ErrorCompletingRequestException
                | IOException | IllegalObjectTypeException
                | InterruptedException | ObjectDoesNotExistException e) {
            throw new CommunicationException(e);
        }
    }

    private void _readFromQueue(final Writer writer)
            throws AS400SecurityException, ErrorCompletingRequestException,
            IOException, IllegalObjectTypeException, InterruptedException,
            ObjectDoesNotExistException {

        final DataQueue dataQueue = createDataQueue(OUT_QUEUE_PREFIX);

        while (true) {

            final DataQueueEntry dataQueueEntry = dataQueue.read(TIMEOUT);
            if (null == dataQueueEntry) {

                throw createNoResponseCommunicationException(
                        dataQueue.getName());
            }
            final byte[] data = dataQueueEntry.getData();

            final CharConverter cc = new CharConverter(
                    getSmeupSession().getCCSID());

            if (data.length < HEADER_LENGTH) {

                throw createBufferToShortCommunicationException(
                        cc.byteArrayToString(data));
            }

            writer.write(cc.byteArrayToString(
                    Arrays.copyOfRange(data, HEADER_LENGTH, data.length))
                    .trim());

            if (END_STRING
                    .equalsIgnoreCase(cc
                            .byteArrayToString(Arrays.copyOfRange(data,
                                    END_STRING_POSITION, HEADER_LENGTH))
                            .trim())) {
                break;
            }
        }
    }

    private CommunicationException createBufferToShortCommunicationException(
            final String buffer) {

        return new CommunicationException("Response with buffer shorter then "
                + HEADER_LENGTH + "(byte): " + buffer);
    }

    private CommunicationException createNoResponseCommunicationException(
            final String queueName) {

        return new CommunicationException(
                "No response received from the queue \"" + queueName
                + "\" within " + TIMEOUT + " seconds");
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
