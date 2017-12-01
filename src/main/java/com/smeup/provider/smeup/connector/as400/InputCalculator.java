package com.smeup.provider.smeup.connector.as400;

import java.io.CharConversionException;
import java.io.UnsupportedEncodingException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.as400.access.CharConverter;
import com.ibm.as400.access.Record;
import com.ibm.as400.access.RecordFormat;
import com.smeup.provider.log.Logged;
import com.smeup.provider.model.SmeupSession;

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

    @Inject
    private SmeupSession smeupSession;

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

    public String toDataQueueEntryAsString(final FUN fun)
            throws CharConversionException, UnsupportedEncodingException {

        return new CharConverter(getSmeupSession().getCCSID())
                .byteArrayToString(createRecord(fun).getContents()).trim();
    }

    public RecordFormat getRecordFormat() {
        return this.recordFormat;
    }

    public void setRecordFormat(final RecordFormat recordFormat) {
        this.recordFormat = recordFormat;
    }

    public SmeupSession getSmeupSession() {
        return this.smeupSession;
    }

    public void setSmeupSession(final SmeupSession smeupSession) {
        this.smeupSession = smeupSession;
    }
}
