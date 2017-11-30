package com.smeup.provider.smeup.connector.as400;

import java.io.CharConversionException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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

    private static final Logger LOGGER = Logger
            .getLogger(InputCalculator.class.getName());

    @Inject
    private RecordFormat recordFormat;

    @Inject
    private SmeupSession smeupSession;

    private static String truncate(final int size, final String v) {

        String r = null;
        if (null != v) {
            final int realSize = v.length();
            r = v.substring(0, Math.min(size, realSize));
        }
        return r;
    }

    private Record createRecord(final FUN fun) {

        final Record r = getRecordFormat().getNewRecord();
        r.setField("TIPO", "JS");

        r.setField("MESSAGGIO", fun.getMessaggio());
        LOGGER.log(Level.FINE, "MESSAGGIO: {0}", fun.getMessaggio());

        r.setField("PROGRAMMA", fun.getProgramma());
        LOGGER.log(Level.FINE, "PROGRAMMA: {0}", fun.getProgramma());

        r.setField("FUNZIONE", fun.getFunzione());
        LOGGER.log(Level.FINE, "FUNZIONE: {0}", fun.getFunzione());

        r.setField("METODO", fun.getMetodo());
        LOGGER.log(Level.FINE, "METODO: {0}", fun.getMetodo());

        for (int i = 0; i < fun.getSmeupObjs().length; i++) {

            // TODO loggare i troncamenti
            r.setField("TIPO_" + (i + 1),
                    truncate(2, fun.getSmeupObjs(i).getTipo()));
            LOGGER.log(Level.FINE, "TIPO_" + (i + 1) + ": {0}",
                    fun.getSmeupObjs(i).getTipo());
            r.setField("PARAMETRO_" + (i + 1),
                    truncate(10, fun.getSmeupObjs(i).getParametro()));
            LOGGER.log(Level.FINE, "PARAMETRO_" + (i + 1) + ": {0}",
                    fun.getSmeupObjs(i).getParametro());
            r.setField("CODICE_" + (i + 1),
                    truncate(15, fun.getSmeupObjs(i).getCodice()));
            LOGGER.log(Level.FINE, "CODICE_" + (i + 1) + ": {0}",
                    fun.getSmeupObjs(i).getCodice());
        }
        r.setField("PARAMETRO", fun.getParametro());
        LOGGER.log(Level.FINE, "PARAM: {0}", fun.getParametro());

        r.setField("SETUP_SETUP", fun.getSetupSetup());
        LOGGER.log(Level.FINE, "SETUP_SETUP: {0}", fun.getSetupSetup());

        r.setField("MSG_CONT", "FINE");

        r.setField("INPUT", fun.getInput());
        LOGGER.log(Level.FINE, "INPUT: {0}", fun.getInput());
        return r;
    }

    public String toDataQueueEntryString(final FUN fun)
            throws CharConversionException, UnsupportedEncodingException {

        return new CharConverter(this.smeupSession.getCCSID()).byteArrayToString(createRecord(fun).getContents());
    }

    public RecordFormat getRecordFormat() {
        return this.recordFormat;
    }

    public void setRecordFormat(final RecordFormat recordFormat) {
        this.recordFormat = recordFormat;
    }
}
