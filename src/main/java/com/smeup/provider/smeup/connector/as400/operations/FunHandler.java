package com.smeup.provider.smeup.connector.as400.operations;

import java.io.Writer;

import javax.inject.Inject;

import com.smeup.provider.log.Logged;

@Logged
public class FunHandler {

    @Inject
    private DataQueueWriter dataQueueWriter;

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
}
