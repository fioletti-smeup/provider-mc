package com.smeup.provider.smeup.connector.as400;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FUNParser {

    @SuppressWarnings("nls")
    private static final String SEPARATOR_TERN_EXPRESSION = ";";
    @SuppressWarnings("nls")
    private static final Pattern STARTING_EXPRESSION_PATTERN = Pattern.compile("\\)?\\s*(__)?\\p{Alnum}+\\s*\\(");
    @SuppressWarnings("nls")
    private static final Pattern FUN_PREDICATE_PATTERN = Pattern.compile("[FCA]\\s*\\(");
    @SuppressWarnings("nls")
    private static final Pattern INPUT_OBJECT_PATTERN = Pattern.compile("\\d");
    @SuppressWarnings("nls")
    private static final Pattern INPUT_PATTERN = Pattern.compile(FUN.INPUT + "\\s*\\(");
    @SuppressWarnings("nls")
    private static final Pattern PARAM_PATTERN = Pattern.compile(FUN.P + "\\s*\\(");
    @SuppressWarnings("nls")
    private static final Pattern CRO_PATTERN = Pattern.compile(FUN.CRO + "\\s*\\(");
    @SuppressWarnings("nls")
    private static final Pattern SERVER_PATTERN = Pattern.compile(FUN.SERVER + "\\s*\\(");
    @SuppressWarnings("nls")
    private static final Pattern SS_PATTERN = Pattern.compile(FUN.SS + "\\s*\\(");
    @SuppressWarnings("nls")
    private static final Pattern G_PATTERN = Pattern.compile(FUN.G + "\\s*\\(");
    @SuppressWarnings("nls")
    private static final Pattern NOTIFY_PATTERN = Pattern.compile(FUN.NOTIFY + "\\s*\\(");
    @SuppressWarnings("nls")
    private static final Pattern SG_PATTERN = Pattern.compile(FUN.SG + "\\s*\\(");

    private boolean useEscape;

    private final FUN fun = new FUN();

    final static class FindClosingBracketResult {

        String f;
        int pos;
        boolean found;
    }

    private FindClosingBracketResult findClosingBracket(final String f, final int start) {
        int pos = start;
        String modifiedString = "";
        boolean found = false;
        if (f.substring(start).startsWith("_$_STXT_$_") && (f.substring(start).indexOf("_$_ETXT_$_)") > -1)) {
            final int posEndTXT = start + f.substring(start).indexOf("_$_ETXT_$_)") + 10;
            String content = f.substring(start, posEndTXT);
            if ((content.indexOf("(") > 0) || (content.indexOf(")") > 0)) {
                content = content.replace("(", "*{*").replace(")", "*}*");
            }
            modifiedString = f.replace(f.substring(start, posEndTXT), content);
            pos = start + modifiedString.substring(start).indexOf("_$_ETXT_$_)") + 11;
            found=true;
        } else {
            final char[] modifiedChars = f.toCharArray();
            int openBrackets = 1;
            boolean escape = false;

            for (final char c : f.substring(start).toCharArray()) {
                switch (c) {
                case '\\':
                    if (this.useEscape) {
                        if (!escape) {

                            removeElement(modifiedChars, pos);
                            pos--;
                        }
                        escape = !escape;
                    }
                    break;
                case '(':
                    if (!escape) {
                        openBrackets++;
                    }
                    escape = false;
                    break;
                case ')':
                    if (!escape) {
                        found = --openBrackets == 0;
                    }
                    escape = false;
                    break;
                default:
                    escape = false;
                }
                pos++;
                if (found) {
                    break;
                }
            }
            modifiedString = new String(modifiedChars);
        }
        final FindClosingBracketResult r = new FindClosingBracketResult();
        r.pos = pos;
        r.f = modifiedString;
        r.found = found;
        return r;
    }

    public FUN parse(final String f) {

        FUN.VirtualFUN.fromString(f).ifPresent(v -> this.fun.setVirtualFUN(v));
        if (!this.fun.isVirtual()) {
            parse(f, 0);
        } else if (this.fun.isVirtual() && f.contains("(")) {
            final String param = FUN.extractParamOfVirtualFUN(f);
            if (FUN.VirtualFUN.DINAMIC.equals(this.fun.getVirtualFUN()) && param.contains("(")) {
                // i.e. DYNAMIC(LOAD(PIPPO)) --> VFun=LOAD Param=PIPPO
                FUN.VirtualFUN.fromString(param).ifPresent(v -> this.fun.setVirtualFUN(v));
                if (this.fun.isVirtual() && param.contains("(")) {
                    this.fun.setVirtualFUNParam(FUN.extractParamOfVirtualFUN(param));
                }
            } else {
                this.fun.setVirtualFUNParam(param);
            }
        }

        return this.fun;
    }

    private void parse(final String f, final int start) {

        final Matcher matcher = STARTING_EXPRESSION_PATTERN.matcher(f);
        final boolean found = matcher.find(start);

        if (found) {

            parseSubExpr(f, matcher);
        }
    }

    @SuppressWarnings("nls")
    private void parseFUN(final String funName, final String paramsList) {

        final char fName = funName.charAt(0);
        switch (fName) {

        case 'F':
            this.fun.setMessaggio("FUN");
            break;
        case 'C':
            this.fun.setMessaggio("COM");
            break;
        case 'A':
            this.fun.setMessaggio("AZI");
            break;
        default:

            throw new UnsupportedOperationException("Unsupported message: " + fName);
        }

        final String[] tern = parseTern(paramsList);
        this.fun.setProgramma(tern[0]);
        this.fun.setFunzione(tern[1]);
        this.fun.setMetodo(tern[2]);
    }

    @SuppressWarnings("nls")
    private void parseInputObject(final String objectNum, final String paramsList) {

        if ("".equals(paramsList)) {
            return;
        }
        if (";;".equals(paramsList)) {
            return;
        }
        final String[] ternValues = parseTern(paramsList);

        if (ternValues.length > 2) {
            // if (ternValues[2].length() > 15) {
            //
            // this.fun.setInput("K" + objectNum + "(" + ternValues[2] + ") "
            // + this.fun.getInput());
            // ternValues[2] = "[K" + objectNum + "]";
            // }

            this.fun.setSmeupObjs(Integer.parseInt(objectNum) - 1,
                    new SmeupObject(ternValues[0], ternValues[1], ternValues[2]));
        }
    }

    @SuppressWarnings("nls")
    private void parseSubExpr(final String funString, final Matcher matcher) {

        final int startContent = matcher.end(0);
        final FindClosingBracketResult r = findClosingBracket(funString, startContent);

        if(!r.found) {
            throw new RuntimeException("No closing bracket found in string: " + funString);
        }

        final String expr = matcher.group(0);
        final String content = r.f.substring(startContent, r.pos - 1);

        final Matcher funMatcher = FUN_PREDICATE_PATTERN.matcher(expr);

        if (funMatcher.find()) {

            parseFUN(funMatcher.group(0), content);

        } else {
            final Matcher inputObjectMatcher = INPUT_OBJECT_PATTERN.matcher(expr);

            if (inputObjectMatcher.find()) {

                parseInputObject(inputObjectMatcher.group(0), content);
            } else {

                final Matcher inputMatcher = INPUT_PATTERN.matcher(expr);

                if (inputMatcher.find()) {

                    // TODO rivedere: non so se e` giusto interporre lo
                    // spazio
                    final String origInput = this.fun.getInput();
                    this.fun.setInput((null == origInput || origInput.isEmpty()) ? content : origInput + " " + content);

                } else {

                    final Matcher paramMatcher = PARAM_PATTERN.matcher(expr);

                    if (paramMatcher.find()) {

                        this.fun.setParametro(content);
                    } else {

                        final Matcher croMatcher = CRO_PATTERN.matcher(expr);

                        if (croMatcher.find()) {

                            final String origCro = this.fun.getCro();
                            this.fun.setCro(
                                    (null == origCro || origCro.isEmpty()) ? content : origCro + " " + content);

                        } else {

                            final Matcher ssMatcher = SS_PATTERN.matcher(expr);

                            if (ssMatcher.find()) {

                                final String origSS = this.fun.getSetupSetup();
                                this.fun.setSetupSetup(
                                        (null == origSS || origSS.isEmpty()) ? content : origSS + " " + content);
                            } else {

                                final Matcher serverMatcher = SERVER_PATTERN.matcher(expr);

                                if (serverMatcher.find()) {

                                    final String origServer = this.fun.getServer();
                                    this.fun.setServer((null == origServer || origServer.isEmpty()) ? content
                                            : origServer + " " + content);

                                } else {
                                    final Matcher sgMatcher = SG_PATTERN.matcher(expr);

                                    if (sgMatcher.find()) {
                                        final String origSG = this.fun.getSetupGrafico();
                                        this.fun.setSetupGrafico((null == origSG || origSG.isEmpty()) ? content : origSG + " " + content);
                                    } else {
                                        final Matcher graphiModeMatcher = G_PATTERN.matcher(expr);

                                        if (graphiModeMatcher.find()) {

                                            final String origG = this.fun.getGraphicMode();
                                            this.fun.setGraphicMode((null == origG || origG.isEmpty()) ? content : origG + " " + content);
                                        } else {

                                            final Matcher notifyMatcher = NOTIFY_PATTERN.matcher(expr);

                                            if (notifyMatcher.find()) {

                                                final String origNotify = this.fun.getNotify();
                                                this.fun.setNotify((null == origNotify || origNotify.isEmpty()) ? content : origNotify + " " + content);

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        parse(r.f, r.pos);
    }

    private static void removeElement(final char[] a, final int del) {
        System.arraycopy(a, del + 1, a, del, a.length - 1 - del);
    }

    private static String[] parseTern(final String s) {

        final String[] strings = s.split(SEPARATOR_TERN_EXPRESSION, 3);
        return strings;
    }

    public boolean isUseEscape() {
        return this.useEscape;
    }

    public void setUseEscape(final boolean useEscape) {
        this.useEscape = useEscape;
    }
}
