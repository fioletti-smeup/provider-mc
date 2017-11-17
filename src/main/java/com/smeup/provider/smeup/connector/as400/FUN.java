package com.smeup.provider.smeup.connector.as400;

import java.util.Arrays;
import java.util.Optional;

@SuppressWarnings("nls")
public class FUN {

    public static String F = "F";
    public static String A = "A";
    public static String M = "M";
    public static String P = "P";
    public static String INPUT = "INPUT";
    public static String CRO = "CRO";
    public static String G = "G";
    public static String SS = "SS";
    public static String SERVER = "SERVER";
    public static String NOTIFY = "NOTIFY";
    public static String SG = "SG";

    // FUN - COM - AZI
    private String messaggio = "";
    // F(programma;...
    private String programma = "";
    // F(programma;funzione;...
    private String funzione = "";
    // F(programma;funzione;metodo...
    private String metodo = "";
    // F(..) P(parametro)
    private String parametro = "";
    // F(..) INPUT(input)
    private String input = "";
    // F(..) CRO(cro)
    private String cro = "";
    // F(..) G(graphicMode)
    private String graphicMode = "";
    // F(..) SS(setupSetup)
    private String setupSetup = "";
    // F(..) SERVER(server)
    private String server = "";
    // F(..) NOTIFY(notify)
    private String notify = "";
    // CLOSE(PARAM)
    private String virtualFUNParam;
    // F(..) SG(sg)
    private String setupGrafico;

    // F(...) 1(smeupObj[0].canonicalForm) 2(smeupObj[1].canonicalForm) .. 6(smeupObj[5].canonicalForm)..
    protected SmeupObject[] smeupObjs = new SmeupObject[6];
    ///////////////////////

    private VirtualFUN virtualFUN;

    public enum VirtualFUN {

        CLOSE, REFRESH, EXECUTE, POPUP, DINAMIC, EXIT, LOAD, RELOAD, HARDREFRESH;

        public static final Optional<VirtualFUN> fromString(final String f) {

            return Arrays.stream(values()).filter(
                    s -> f.trim().toUpperCase().startsWith(s.toString()))
                    .findAny();
        }
    }

    public FUN() {

        for (int i = 0; i < this.smeupObjs.length; i++) {

            this.smeupObjs[i] = new SmeupObject();
        }
    }

    public String getFunzione() {
        return this.funzione;
    }

    public String getService() {
        return this.funzione;
    }

    public String getInput() {
        return this.input;
    }

    public String getMessaggio() {
        return this.messaggio;
    }

    public String getMetodo() {
        return this.metodo;
    }

    public String getFunction() {
        return this.metodo;
    }

    public String getParametro() {
        return this.parametro;
    }

    public String getProgramma() {
        return this.programma;
    }

    public SmeupObject[] getSmeupObjs() {
        return this.smeupObjs;
    }

    public SmeupObject getSmeupObjs(final int index) {
        return this.smeupObjs[index];
    }

    public void setFunzione(final String funzione) {
        this.funzione = funzione;
    }

    public void setInput(final String input) {
        this.input = input;
    }

    public void setMessaggio(final String messaggio) {
        this.messaggio = messaggio;
    }

    public void setMetodo(final String metodo) {
        this.metodo = metodo;
    }

    public void setParametro(final String parametro) {
        this.parametro = parametro;
    }

    public void setProgramma(final String programma) {
        this.programma = programma;
    }

    public void setSmeupObjs(final int index, final SmeupObject newSmeupObjs) {
        this.smeupObjs[index] = newSmeupObjs;
    }

    public String getCro() {
        return this.cro;
    }

    public void setCro(final String cro) {
        this.cro = cro;
    }

    public String getGraphicMode() {
        return this.graphicMode;
    }

    public void setGraphicMode(final String graphicMode) {
        this.graphicMode = graphicMode;
    }

    public String getSetupGrafico() {
        return this.setupGrafico;
    }

    public void setSetupGrafico(final String setupGrafico) {
        this.setupGrafico = setupGrafico;
    }

    public String getSetupSetup() {
        return this.setupSetup;
    }

    public void setSetupSetup(final String setupSetup) {

        this.setupSetup = setupSetup;
    }

    public String getServer() {
        return this.server;
    }

    public void setServer(final String server) {
        this.server = server;
    }

    public String getNotify() {
        return this.notify;
    }

    public void setNotify(final String notify) {
        this.notify = notify;
    }

    public VirtualFUN getVirtualFUN() {
        return this.virtualFUN;
    }

    public void setVirtualFUN(final VirtualFUN virtualFUN) {
        this.virtualFUN = virtualFUN;
    }

    public boolean isVirtual() {
        return null != this.virtualFUN;
    }

    public static String extractParamOfVirtualFUN(final String virtualFUN) {

        String param;

        if (virtualFUN != null && virtualFUN.contains("(")) {
            param = virtualFUN.substring(virtualFUN.indexOf("(") + 1,
                    virtualFUN.lastIndexOf(")"));
        } else {
            param = virtualFUN;
        }

        return param;

    }

    private static boolean isNotEmptyOrNull(final String s) {
        return s != null && s.trim().length() != 0;
    }

    private void appendFormattedObject(final StringBuilder sb, final int i) {
        if (getSmeupObjs(i) != null && !getSmeupObjs(i).isEmpty()) {
            sb.append(" ").append(i + 1).append("(")
            .append(getSmeupObjs(i).getCanonicalForm()).append(")");
        }
    }

    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder();
        if (isNotEmptyOrNull(getMessaggio())) {
            sb.append(getMessaggio().charAt(0)).append("(")
            .append(getProgramma()).append(";").append(getFunzione())
            .append(";").append(getMetodo()).append(")");
        }
        for (int i = 0; i < 6; i++) {
            appendFormattedObject(sb, i);
        }
        if (isNotEmptyOrNull(getParametro())) {
            sb.append(" ").append(FUN.P).append("(").append(getParametro())
            .append(")");
        }
        if (isNotEmptyOrNull(getInput())) {
            sb.append(" ").append(FUN.INPUT).append("(").append(getInput())
            .append(")");
        }
        if (isNotEmptyOrNull(getGraphicMode())) {
            sb.append(" ").append(FUN.G).append("(").append(getGraphicMode())
            .append(")");
        }
        if (isNotEmptyOrNull(getSetupSetup())) {
            sb.append(" ").append(FUN.SS).append("(").append(getSetupSetup())
            .append(")");
        }
        if (isNotEmptyOrNull(getCro())) {
            sb.append(" ").append(FUN.CRO).append("(").append(getCro())
            .append(")");
        }
        if (isNotEmptyOrNull(getNotify())) {
            sb.append(" ").append(FUN.NOTIFY).append("(").append(getNotify())
            .append(")");
        }
        if (isNotEmptyOrNull(getServer())) {
            sb.append(" ").append(FUN.SERVER).append("(").append(getServer())
            .append(")");
        }
        if (isNotEmptyOrNull(getSetupGrafico())) {
            sb.append(" ").append(FUN.SG).append("(").append(getSetupGrafico())
            .append(")");
        }

        return sb.toString();

    }

    public String getVirtualFUNParam() {
        return this.virtualFUNParam;
    }

    public void setVirtualFUNParam(final String virtualFUNParam) {
        this.virtualFUNParam = virtualFUNParam;
    }
}
