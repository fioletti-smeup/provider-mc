package com.smeup.provider.smeup.connector.as400.operations;

import java.beans.PropertyVetoException;

import javax.inject.Inject;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.ProgramCall;
import com.ibm.as400.access.ProgramParameter;
import com.ibm.as400.access.QSYSObjectPathName;
import com.smeup.provider.smeup.connector.as400.as400.qualifiers.CCSID;

public class ProgramCallHandler {

    public static final String DEFAULT_LIBRARIES = "*LIBL";

    private static final String DEFAULT_PROGRAM = "JAJAC0";

    private static final int TIMEOUT = 30; // Seconds

    @Inject
    @CCSID
    private Integer ccsid;

    @Inject
    private AS400 as400;

    private static final QSYSObjectPathName PROGRAM_PATH_NAME = new QSYSObjectPathName(
            DEFAULT_LIBRARIES, DEFAULT_PROGRAM, "PGM");

    public ProgramCall createCall(final String[] params) {

        final ProgramCall call = new ProgramCall();
        try {
            call.setSystem(getAS400());
            call.setProgram(PROGRAM_PATH_NAME.getPath());
            call.setParameterList(createParameterList(params));
        } catch (final PropertyVetoException e) {
            e.printStackTrace();
            throw new CommunicationException(e);
        }

        call.setTimeOut(TIMEOUT);

        return call;
    }

    private ProgramParameter[] createParameterList(final String[] params) {

        final ProgramParameter[] programParameters = new ProgramParameter[params.length];
        int i = 0;
        final AS400Text[] texts = createAS400Texts(params, getCCSID());
        for (final String param : params) {

            programParameters[i] = new ProgramParameter(texts[i].toBytes(param),
                    param.length());
            i++;
        }
        return programParameters;
    }

    public Integer getCCSID() {
        return this.ccsid;
    }

    public void setCCSID(final Integer ccsid) {
        this.ccsid = ccsid;
    }

    private AS400Text[] createAS400Texts(final String[] params,
            final int ccsid) {

        final AS400Text[] txts = new AS400Text[params.length];
        int i = 0;
        for (final String param : params) {
            txts[i] = new AS400Text(param.length(), ccsid);
            i++;
        }
        return txts;
    }

    public AS400 getAS400() {
        return this.as400;
    }

    public void setAS400(final AS400 as400) {
        this.as400 = as400;
    }
}
