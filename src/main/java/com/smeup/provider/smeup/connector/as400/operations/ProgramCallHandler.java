package com.smeup.provider.smeup.connector.as400.operations;

import java.beans.PropertyVetoException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.ProgramCall;
import com.ibm.as400.access.ProgramParameter;
import com.ibm.as400.access.QSYSObjectPathName;
import com.smeup.provider.model.SmeupSession;

@RequestScoped
public class ProgramCallHandler {

    public static final String DEFAULT_LIBRARIES = "*LIBL";

    private static final String DEFAULT_PROGRAM = "JAJAC0";

    private static final int TIMEOUT = 30; // Seconds

    @Inject
    private SmeupSession smeupSession;

    private static final QSYSObjectPathName PROGRAM_PATH_NAME = new QSYSObjectPathName(
            DEFAULT_LIBRARIES, DEFAULT_PROGRAM, "PGM");

    public ProgramCall createCall(final AS400 as400, final String[] params) {

        final ProgramCall call = new ProgramCall();
        try {
            call.setSystem(as400);
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
        final AS400Text[] texts = createAS400Texts(params,
                getSmeupSession().getCCSID());
        for (final String param : params) {

            programParameters[i] = new ProgramParameter(texts[i].toBytes(param),
                    param.length());
            i++;
        }
        return programParameters;
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

    public SmeupSession getSmeupSession() {
        return this.smeupSession;
    }

    public void setSmeupSession(final SmeupSession smeupSession) {
        this.smeupSession = smeupSession;
    }
}
