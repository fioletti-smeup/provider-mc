/**
 *
 */
package com.smeup.provider.smeup.connector.as400;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.CharacterFieldDescription;
import com.ibm.as400.access.RecordFormat;
import com.smeup.provider.model.SmeupSession;

/**
 * @author gianluca
 *
 */

@ApplicationScoped
public class DataStructurProvider {

    @Inject
    private SmeupSession smeupSession;

    @Produces @RequestScoped
    public RecordFormat getRecordFormatForInQueue() {

        final Integer ccsid = getSmeupSession().getCCSID();

        final RecordFormat recordFormat = new RecordFormat();

        recordFormat.addFieldDescription(new CharacterFieldDescription( // 0
                new AS400Text(10, ccsid), "TIPO"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(10, ccsid), "MESSAGGIO"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(10, ccsid), "PROGRAMMA"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(10, ccsid), "FUNZIONE"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(10, ccsid), "METODO"));

        // Ogetto 1
        recordFormat.addFieldDescription(new CharacterFieldDescription( // 5
                new AS400Text(2, ccsid), "TIPO_1"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(10, ccsid), "PARAMETRO_1"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(15, ccsid), "CODICE_1"));

        // Ogetto 2
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(2, ccsid), "TIPO_2"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(10, ccsid), "PARAMETRO_2"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(// 10
                new AS400Text(15, ccsid), "CODICE_2"));

        // Ogetto 3
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(2, ccsid), "TIPO_3"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(10, ccsid), "PARAMETRO_3"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(15, ccsid), "CODICE_3"));

        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(256, ccsid), "PARAMETRO"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(1, ccsid), "ERRORE"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(1, ccsid), "RICERCA"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(7, ccsid), "MSG_CONT"));

        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(10, ccsid), "VUOTO_IBFM"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(10, ccsid), "VUOTO_IBSC"));

        // Ogetto 4
        recordFormat.addFieldDescription(new CharacterFieldDescription( // 20
                new AS400Text(2, ccsid), "TIPO_4"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(10, ccsid), "PARAMETRO_4"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(15, ccsid), "CODICE_4"));

        // Ogetto 5
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(2, ccsid), "TIPO_5"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(10, ccsid), "PARAMETRO_5"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(15, ccsid), "CODICE_5"));

        // Ogetto 6
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(2, ccsid), "TIPO_6"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(10, ccsid), "PARAMETRO_6"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(15, ccsid), "CODICE_6"));

        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(15, ccsid), "SERVER"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(15, ccsid), "CLIENT"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(58, ccsid), "RICHIESTO"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(4, ccsid), "MODO_GRAFICO"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(256, ccsid), "SETUP_SETUP"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(155, ccsid), "VUOTO"));
        recordFormat.addFieldDescription(new CharacterFieldDescription(
                new AS400Text(25000, ccsid), "INPUT"));

        return recordFormat;
    }

    public SmeupSession getSmeupSession() {
        return this.smeupSession;
    }

    public void setSmeupSession(final SmeupSession smeupSession) {
        this.smeupSession = smeupSession;
    }
}
