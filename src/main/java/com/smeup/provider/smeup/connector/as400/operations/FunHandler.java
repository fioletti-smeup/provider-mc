package com.smeup.provider.smeup.connector.as400.operations;

import java.io.IOException;
import java.io.Writer;

import javax.inject.Inject;

import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.IllegalObjectTypeException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.smeup.provider.log.Logged;
import com.smeup.provider.model.CommunicationException;

@Logged
public class FunHandler {

    @Inject
    private DataQueueWriter dataQueueWriter;

    @Inject
    private DataQueueReader dataQueueReader;

    public void executeFun(final String fun, final Writer writer) {

        getDataQueueWriter().writeToQueue(fun);

        try {
            getDataQueueReader().readFromQueue(writer);

        } catch (AS400SecurityException | ErrorCompletingRequestException
                | IOException | IllegalObjectTypeException
                | InterruptedException | ObjectDoesNotExistException e) {
            throw new CommunicationException(e);
        }
    }

    public DataQueueWriter getDataQueueWriter() {
        return this.dataQueueWriter;
    }

    public void setDataQueueWriter(final DataQueueWriter dataQueueWriter) {
        this.dataQueueWriter = dataQueueWriter;
    }

    public DataQueueReader getDataQueueReader() {
        return this.dataQueueReader;
    }

    public void setDataQueueReader(final DataQueueReader dataQueueReader) {
        this.dataQueueReader = dataQueueReader;
    }
}
