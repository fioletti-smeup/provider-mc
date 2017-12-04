package com.smeup.provider.smeup.connector.as400.operations;

import java.io.Writer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.smeup.provider.log.Logged;
import com.smeup.provider.model.SmeupSession;

@Logged
@ApplicationScoped
public class FunHandler {

    @Inject
    private DataQueueWriter dataQueueWriter;

    @Inject
    private SmeupSession SmeupSession;

    @Inject
    private DataQueueReader dataQueueReader;

    public void executeFun(final String fun, final Writer writer) {

        getDataQueueWriter().writeToQueue(fun);
        getDataQueueReader().readFromQueue(writer);
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

    public SmeupSession getSmeupSession() {
        return this.SmeupSession;
    }

    public void setSmeupSession(final SmeupSession smeupSession) {
        this.SmeupSession = smeupSession;
    }
}
