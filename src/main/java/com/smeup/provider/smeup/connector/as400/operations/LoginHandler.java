package com.smeup.provider.smeup.connector.as400.operations;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.common.base.Splitter;
import com.google.common.collect.Streams;
import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.ProgramCall;
import com.smeup.provider.Claims;
import com.smeup.provider.JWTManager;
import com.smeup.provider.model.Credentials;
import com.smeup.provider.model.LoginResponse;
import com.smeup.provider.model.SmeupSession;
import com.smeup.provider.smeup.connector.as400.DataQueueReader;
import com.smeup.provider.smeup.connector.as400.DataQueueWriter;
import com.smeup.provider.smeup.connector.as400.as400.qualifiers.OfUser;

public class LoginHandler {

    private static final String[] CREATION_PARAMS = {
            String.format("%1$-" + 10 + "s", "JA"),
            String.format("%1$-" + 10 + "s", "DATSES"),
            String.format("%1$-" + 10 + "s", "CON"),
            String.format("%1$-" + 10 + "s", "MAS"),
            String.format("%1$-" + 15 + "s", ""),
            String.format("%1$-" + 15 + "s", ""),
            String.format("%1$-" + 512 + "s", ""),
            String.format("%1$-" + 128 + "s", ""),
            String.format("%1$-" + 1024 + "s", "") };

    private static final String[] ENVIRONMENTS = {
            String.format("%1$-" + 10 + "s", "JA"),
            String.format("%1$-" + 10 + "s", "LISAMB"),
            String.format("%1$-" + 10 + "s", "LET"),
            String.format("%1$-" + 10 + "s", "IU"),
            String.format("%1$-" + 15 + "s", ""),
            String.format("%1$-" + 15 + "s", ""),
            String.format("%1$-" + 512 + "s", ""),
            String.format("%1$-" + 128 + "s", ""),
            String.format("%1$-" + 1000 + "s", "") };

    private static final int ENVIRONMENT_CODE_LENGTH = 15;
    private static final int ENVIRONMENT_ENTRIES_LENGTH = 50;

    private static final String TWO_HUNDRED_AND_FIFTY_SIX_ZEROS = String
            .format("%0256d", Integer.valueOf(0));

    private static final String VERSION = "V4R1M151024";

    private static final String CHG_ENV_FUNCTION_STRING = "C(COL;CHG;) 1(IU;;{0}) P("
            + TWO_HUNDRED_AND_FIFTY_SIX_ZEROS
            + ") INPUT(<UISmeup><Setup><LO version=\"" + VERSION
            + "\"></LO></Setup></UISmeup>)";

    @Inject
    private JWTManager jwtManager;

    @Inject
    private ProgramCallHandler programCallHandler;

    @Inject
    private DataQueueWriter dataQueueWriter;

    @Inject
    private DataQueueReader dataQueueReader;

    @Inject
    private Instance<SmeupSession> smeupSession;

    @Inject
    @OfUser
    private Instance<AS400> as400OfUser;

    @Inject
    private Instance<Credentials> credentials;

    public LoginResponse.Data login() {

        String sessionId = null;
        final ProgramCall call = getProgramCallHandler()
                .createCall(getAs400OfUser().get(), CREATION_PARAMS);
        boolean exitStatus = false;
        try {
            exitStatus = call.run();
        } catch (AS400SecurityException | ErrorCompletingRequestException
                | IOException | InterruptedException
                | ObjectDoesNotExistException e) {
            throw new CommunicationException(e);
        }
        final LoginResponse.Data response = new LoginResponse.Data();
        if (exitStatus) {
            final SmeupSession smeupSession = getSmeupSession().get();
            sessionId = new AS400Text(CREATION_PARAMS[8].length(),
                    smeupSession.getCCSID())
                    .toObject(
                            call.getParameterList()[8].getOutputData())
                    .toString().substring(30, 36);
            smeupSession.setSessionId(sessionId);
            final Optional<Integer> environmentCode = resolveCode(
                    getCredentials().get().getEnvironment(),
                    smeupSession.getCCSID());

            String initXML = null;
            if (environmentCode.isPresent()) {

                initXML = changeEnvironment(environmentCode.get());
                if (null != initXML && !initXML.trim().isEmpty()) {

                    response.setInitXML(initXML);
                    final Map<String, Object> claims = new HashMap<>();
                    claims.put(Claims.SESSION_ID.name(),
                            String.valueOf(smeupSession.getSessionId()));
                    claims.put(Claims.CCSID.name(),
                            String.valueOf(smeupSession.getCCSID()));
                    response.setJWT(getJWTManager().sign(claims));
                }
            }

        } else {
            throw new CommunicationException(
                    "Program call to: " + call.getProgram() + " failed");
        }

        return response;
    }

    private String changeEnvironment(final Integer environment)
            throws CommunicationException {

        final String fun = MessageFormat.format(CHG_ENV_FUNCTION_STRING,
                String.format("%04d", environment));
        getDataQueueWriter().writeToQueue(fun);
        return getDataQueueReader()
                .readFromQueue();
    }

    private Optional<Integer> resolveCode(final String env, final int ccsid) {

        Optional<Integer> code = null;
        final ProgramCall call = getProgramCallHandler()
                .createCall(getAs400OfUser().get(), ENVIRONMENTS);
        boolean exitStatus = false;
        try {
            exitStatus = call.run();
        } catch (AS400SecurityException | ErrorCompletingRequestException
                | IOException | InterruptedException
                | ObjectDoesNotExistException e) {

            throw new CommunicationException(e);
        }
        if (exitStatus) {
            code = extractCode(new AS400Text(ENVIRONMENTS[8].length(), ccsid)
                    .toObject(call.getParameterList()[8].getOutputData())
                    .toString(), env);
        } else {
            throw new CommunicationException(
                    "Program call to: " + call.getProgram() + " failed");
        }
        return code;
    }

    public ProgramCallHandler getProgramCallHandler() {
        return this.programCallHandler;
    }

    public void setProgramCallHandler(
            final ProgramCallHandler programCallHandler) {
        this.programCallHandler = programCallHandler;
    }

    private Optional<Integer> extractCode(final String environments,
            final String env) {

        Optional<Integer> result = null;
        final Map<Integer, String> map = toMap(environments);

        Integer envAsIntger;
        try {
            envAsIntger = Integer.valueOf(env);
            if (map.containsKey(envAsIntger))
                result = Optional.of(envAsIntger);
        } catch (final NumberFormatException numberFormatException) {

            result = map.entrySet().stream()
                    .map(e -> e.getValue().split("\\s+")[0]
                            .equalsIgnoreCase(env) ? e.getKey() : null)
                    .filter(v -> null != v).findAny();
        }
        return result;
    }

    private Map<Integer, String> toMap(final String envs) {

        final Map<Integer, String> map = Streams
                .stream(Splitter
                        .fixedLength(ENVIRONMENT_ENTRIES_LENGTH).split(envs))
                .filter(s -> !s.trim().isEmpty())
                .collect(Collectors.toMap(
                        s -> Integer.valueOf(
                                s.substring(0, ENVIRONMENT_CODE_LENGTH).trim()),
                        s -> s.substring(ENVIRONMENT_CODE_LENGTH,
                                ENVIRONMENT_ENTRIES_LENGTH)));
        return map;
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

    public JWTManager getJWTManager() {
        return this.jwtManager;
    }

    public void setJWTManager(final JWTManager jwtManager) {
        this.jwtManager = jwtManager;
    }

    public Instance<SmeupSession> getSmeupSession() {
        return this.smeupSession;
    }

    public void setSmeupSession(final Instance<SmeupSession> smeupSession) {
        this.smeupSession = smeupSession;
    }

    public Instance<AS400> getAs400OfUser() {
        return this.as400OfUser;
    }

    public void setAs400OfUser(final Instance<AS400> as400OfUser) {
        this.as400OfUser = as400OfUser;
    }

    public Instance<Credentials> getCredentials() {
        return this.credentials;
    }

    public void setCredentials(final Instance<Credentials> credentials) {
        this.credentials = credentials;
    }

}
