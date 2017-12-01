package com.smeup.provider.smeup.connector.as400;

import java.io.CharConversionException;
import java.io.UnsupportedEncodingException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.as400.access.Record;
import com.ibm.as400.access.RecordFormat;
import com.smeup.provider.log.Logged;

/**
 * @author gianluca
 *
 */

@Logged
@ApplicationScoped
public class InputCalculator {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(InputCalculator.class);

    @Inject
    private RecordFormat recordFormat;

    private static String truncate(final int size, final String v) {

        if (null != v && v.length() > size) {

            LOGGER.warn("Value \"{}\" truncated to length {}", v, size);
            return v.substring(0, size);
        }
        return v;
    }

    private Record createRecord(final FUN fun) {

        final Record r = getRecordFormat().getNewRecord();
        r.setField("TIPO", "JS");

        r.setField("MESSAGGIO", fun.getMessaggio());
        LOGGER.trace("MESSAGGIO: {}", fun.getMessaggio());

        r.setField("PROGRAMMA", fun.getProgramma());
        LOGGER.trace("PROGRAMMA: {}", fun.getProgramma());

        r.setField("FUNZIONE", fun.getFunzione());
        LOGGER.trace("FUNZIONE: {}", fun.getFunzione());

        r.setField("METODO", fun.getMetodo());
        LOGGER.trace("METODO: {}", fun.getMetodo());

        for (int i = 0; i < fun.getSmeupObjs().length; i++) {

            r.setField("TIPO_" + (i + 1),
                    truncate(2, fun.getSmeupObjs(i).getTipo()));
            LOGGER.trace("TIPO_" + (i + 1) + ": {}",
                    fun.getSmeupObjs(i).getTipo());
            r.setField("PARAMETRO_" + (i + 1),
                    truncate(10, fun.getSmeupObjs(i).getParametro()));
            LOGGER.trace("PARAMETRO_" + (i + 1) + ": {}",
                    fun.getSmeupObjs(i).getParametro());
            r.setField("CODICE_" + (i + 1),
                    truncate(15, fun.getSmeupObjs(i).getCodice()));
            LOGGER.trace("CODICE_" + (i + 1) + ": {}",
                    fun.getSmeupObjs(i).getCodice());
        }
        r.setField("PARAMETRO", fun.getParametro());
        LOGGER.trace("PARAM: {}", fun.getParametro());

        r.setField("SETUP_SETUP", fun.getSetupSetup());
        LOGGER.trace("SETUP_SETUP: {}", fun.getSetupSetup());

        r.setField("MSG_CONT", "FINE");

        r.setField("INPUT", fun.getInput());
        LOGGER.trace("INPUT: {}", fun.getInput());
        return r;
    }

    public byte[] toDataQueueEntry(final FUN fun) throws CharConversionException, UnsupportedEncodingException {

        final byte space = getRecordFormat().getFieldDescription(0)
                .getDataType().toBytes("")[0];
        final Record record = createRecord(fun);
        final byte[] input = record.getContents();
        int i = input.length;
        while (i-- > 0 && input[i] == space) {
        }

        final byte[] output = new byte[i + 1];
        System.arraycopy(input, 0, output, 0, i + 1);
        return output;
    }

    public RecordFormat getRecordFormat() {
        return this.recordFormat;
    }

    public void setRecordFormat(final RecordFormat recordFormat) {
        this.recordFormat = recordFormat;
    }
}
