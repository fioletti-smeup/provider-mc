package com.smeup.provider.smeup.connector.as400.operations;

import com.smeup.provider.model.FunResponse;
import com.smeup.provider.smeup.connector.as400.DataQueueReader;
import com.smeup.provider.smeup.connector.as400.DataQueueWriter;
import com.smeup.provider.smeup.connector.as400.FUNParser;

public class FunHandler {

    private DataQueueWriter dataQueueWriter;

    private DataQueueReader dataQueueReader;

    public FunResponse.Data executeFun(final String fun) {

        String xml = null;
        try {
            getDataQueueWriter().writeToQueue(fun);
        } catch (final CommunicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            xml = getDataQueueReader()
                    .readFromQueue(new FUNParser().parse(fun).isCOM_or_FUN());
        } catch (final CommunicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final FunResponse.Data data = new FunResponse.Data();
        data.setResponse(xml);

        return data;
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
