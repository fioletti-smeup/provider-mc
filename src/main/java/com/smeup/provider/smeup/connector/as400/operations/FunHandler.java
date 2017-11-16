package com.smeup.provider.smeup.connector.as400.operations;

import javax.inject.Inject;

import com.smeup.provider.smeup.connector.as400.DataQueueReader;
import com.smeup.provider.smeup.connector.as400.DataQueueWriter;
import com.smeup.provider.smeup.connector.as400.FUNParser;

public class FunHandler {

    @Inject
    private DataQueueWriter dataQueueWriter;

    @Inject
    private DataQueueReader dataQueueReader;

    public String executeFun(final String fun) {

        String xml = null;
        getDataQueueWriter().writeToQueue(fun);
        xml = getDataQueueReader()
                .readFromQueue(new FUNParser().parse(fun).isCOM_or_FUN());
        return xml;
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
