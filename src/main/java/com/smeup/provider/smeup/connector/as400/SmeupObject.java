package com.smeup.provider.smeup.connector.as400;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class SmeupObject implements Serializable, Comparable<SmeupObject>, PropertyChangeListener, Cloneable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(SmeupObject.class.getName());

    // @SuppressWarnings("nls")
    // private static final String LITERAL_PATTERN = "([^;]*);([^;]*);(.*)";
    // private static final Pattern PATTERN = Pattern.compile(LITERAL_PATTERN);

    @SuppressWarnings("nls")
    private static final String DEFAULT_FUN = "F(EXD;*SCO;) 1(%s)";

    @SuppressWarnings("nls")
    private static final String SMEUP_NUMBER_PATTERN = "#,##0.#####;#,##0.#####-";
    @SuppressWarnings("nls")
    private static final String SMEUP_NUMBER_PATTERN2 = "#,##0.#####;#,-##0.#####";
    @SuppressWarnings("nls")
    private static final String D8_YYMM_PATTERN = "yyyyMMdd";

    @SuppressWarnings("nls")
    private static final String D8_YY_BAR_MM_BAR_PATTERN = "yyyy/MM/dd";
    @SuppressWarnings("nls")
    public static final String I12_HHMMSS_PATTERN = "HHmmss";
    @SuppressWarnings("nls")
    public static final String I13_HHMM_PATTERN = "HHmm";

    @SuppressWarnings("nls")
    private static final String I12_HH_COL_MM_COL_PATTERN = "HH:mm:ss";
    @SuppressWarnings("nls")
    private static final String I13_HH_COL_MM_COL_PATTERN = "HH:mm";
    private static final DateTimeFormatter D8_YYMM_formatter = DateTimeFormatter.ofPattern(D8_YYMM_PATTERN);

    private static final DateTimeFormatter D8_YY_BAR_MM_BAR_formatter = DateTimeFormatter
            .ofPattern(D8_YY_BAR_MM_BAR_PATTERN);
    private static final DateTimeFormatter I12_HHMMSS_formatter = DateTimeFormatter.ofPattern(I12_HHMMSS_PATTERN);
    private static final DateTimeFormatter I13_HHMMSS_formatter = DateTimeFormatter.ofPattern(I13_HHMM_PATTERN);

    private static final DateTimeFormatter I12_HH_COL_MM_COL_formatter = DateTimeFormatter
            .ofPattern(I12_HH_COL_MM_COL_PATTERN);

    private static final DateTimeFormatter I13_HH_COL_MM_COL_formatter = DateTimeFormatter
            .ofPattern(I13_HH_COL_MM_COL_PATTERN);

    @SuppressWarnings("nls")
    private final static Pattern RTRIM = Pattern.compile("\\s+$");

    private int minimumScale;

    private String formula;

    public String getFormula() {
        return this.formula;
    }

    public void setFormula(final String formula) {
        this.formula = formula;
    }

    public int getMinimumScale() {
        return this.minimumScale;
    }

    public void setMinimumScale(final int minimumScale) {
        this.minimumScale = minimumScale;
    }

    @SuppressWarnings("nls")
    private static String rtrim(final String s) {
        return RTRIM.matcher(s).replaceAll("");
    }

    @SuppressWarnings("nls")
    private static String tryToCorrect(final String canonicalForm, final int numberOfSemicolons) {

        String corrected = canonicalForm;
        if (numberOfSemicolons == 0) {
            corrected = ";;" + canonicalForm;
        } else if (numberOfSemicolons == 1) {
            corrected = canonicalForm + ";";
        }
        return corrected;
    }

    private String rTrimmedCode;

    private Character decimalSeparator = ',';

    private Character groupingSeparator = '.';
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private String nome;
    private SmeupClass smeupClass;

    private String codice;
    private String testo;

    private BigDecimal internalBigDecimal;
    private Date internalDate;
    private String exec;
    private String fld;
    private String leaf;

    private String grp;

    private String i;
    private String style;

    private boolean disableChangeListener;

    @SuppressWarnings("nls")
    public SmeupObject() {
        this("", "", "");
    }

    @SuppressWarnings("nls")
    public SmeupObject(final LocalDate date) {
        this("D8", "*YYMD", "19700101");
        this.codice = retrieveSimpleDateFormat().format(date);
    }

    public SmeupObject(final String canonicalForm) {
        super();
        initFromCanonicalForm(canonicalForm);
        configureListeners();
    }

    public SmeupObject(final String tipo, final String parametro, final String codice) {

        super();
        this.codice = codice;
        this.smeupClass = new SmeupClass(tipo, parametro);
        configureListeners();
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        getPcs().addPropertyChangeListener(listener);
    }

    public Date asDate() {
        if (null == getDateInternalValue()) {
            final LocalDate ldt = asLocalDate();
            if (ldt != null) {
                setDateInternalValue(Date.from(ldt.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            }
        }
        return getDateInternalValue();
    }

    @SuppressWarnings("nls")
    public LocalDate asLocalDate() {
        final String trimmedCode = getCodice().trim();
        if ((getTipo().equalsIgnoreCase("D8") || getTipo().equalsIgnoreCase("A8")) && !trimmedCode.isEmpty()
                && !"00000000".equals(trimmedCode) && !"0000/00/00".equals(trimmedCode)
                && !"00/00/0000".equals(trimmedCode) && !"0".equals(trimmedCode)) {
            final DateTimeFormatter dateTimeFormatter = retrieveSimpleDateFormat();

            try {
                return LocalDate.parse(trimmedCode, dateTimeFormatter);
            } catch (final DateTimeParseException e) {
                LOGGER.fine(e.getMessage());
                LOGGER.log(Level.WARNING, "Parse error: {0}", this);
            }
        }
        return null;
    }

    @SuppressWarnings("nls")
    public LocalTime asLocalTime() {
        final String trimmedCode = getCodice().trim();

        if (getTipo().equalsIgnoreCase("I1") && !trimmedCode.isEmpty()) {

            DateTimeFormatter hourTimeFormatter;

            if (getParametro().equalsIgnoreCase("3")) {

                hourTimeFormatter = retrieveI3TimeFormat();

            } else {

                hourTimeFormatter = retrieveI12TimeFormat();
            }

            try {
                return LocalTime.parse(trimmedCode, hourTimeFormatter);
            } catch (final DateTimeParseException e) {
                LOGGER.fine(e.getMessage());
                LOGGER.log(Level.WARNING, "Parse error: {0}", this);
            }
        }
        return null;
    }

    @SuppressWarnings("nls")
    public BigDecimal asNumber() {

        if (null == getBigDecimalInternalValue()) {

            final String code = getCodice();
            try {

                final String trimmedCode = code.trim();
                final boolean startsWithMinus = trimmedCode.startsWith("-");
                final DecimalFormat df = startsWithMinus ? retrieveOrCreateDecimalFormat2()
                        : retrieveOrCreateDecimalFormat();
                if (!code.equals(trimmedCode)) {

                    LOGGER.fine("Number containing spaces");
                }
                if (!trimmedCode.isEmpty()) {

                    if (!trimmedCode.contains("---")) {
                        final BigDecimal v = (BigDecimal) df.parse(trimmedCode);
                        setBigDecimalInternalValue(v);
                    }

                } else {

                    setBigDecimalInternalValue(BigDecimal.ZERO);
                }
            } catch (final ParseException e) {
                setBigDecimalInternalValue(BigDecimal.ZERO);
                LOGGER.log(Level.WARNING, "ParseException: {0}", code);
            }

        }
        return getBigDecimalInternalValue();
    }

    public BigDecimal asNumberOrNullIfEmptyCode() {
        final String trimmedCode = getCodice().trim();

        return trimmedCode.isEmpty() ? null : this.asNumber();
    }

    public boolean canBeDecoded() {
        return !isText() && !isMemo() && !isNumber() && !isPositiveNumber() && !isPassword() && !isFile()
                && !isJLSmeupObject();
    }

    public boolean cantHaveIcon() {

        return isButton() || isNumber() || isText();
    }

    @Override
    public SmeupObject clone() {

        SmeupObject c = null;
        try {

            c = (SmeupObject) super.clone();
            c.smeupClass = c.smeupClass.clone();

        } catch (final CloneNotSupportedException e) {
            LOGGER.fine(e.getMessage());
            throw new Error();
        }

        c.pcs = new PropertyChangeSupport(c);
        c.configureListeners();

        return c;
    }

    @Override
    public int compareTo(final SmeupObject o) {

        int result = getSmeupClass().compareTo(o.getSmeupClass());
        if (0 == result) {

            // Se questo e` un numero lo e` anche l'altro
            if (isNumber()) {

                final BigDecimal number = asNumber();
                final BigDecimal otherAsNumber = o.asNumber();
                if (null != number && null != otherAsNumber) {
                    result = number.compareTo(otherAsNumber);
                } else {

                    result = getRTrimmedCode().compareTo(o.getRTrimmedCode());
                }

            } else {

                result = getRTrimmedCode().compareTo(o.getRTrimmedCode());
            }
        }
        return result;
    }

    private void configureListeners() {

        if (null != this.smeupClass) {

            this.smeupClass.addPropertyChangeListener(this);
        }
        this.addPropertyChangeListener(this);
    }

    public int decimalDigitsNumber() {

        int value = 0;
        if (isNumber()) {

            final BigDecimal bigDecimal = asNumber();
            if (null != bigDecimal) {
                value = bigDecimal.stripTrailingZeros().scale();
            }
        }
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }
        final SmeupObject other = (SmeupObject) obj;

        // check smeup class
        if (!this.getSmeupClass().equals(other.getSmeupClass())) {
            return false;
        }

        if (this.codice == null) {
            if (other.codice != null) {
                return false;
            }
        } else if (this.codice.equals(other.codice) || getRTrimmedCode().equals(other.getRTrimmedCode())) {
            return true;
        } else {

            // BRAVOOOO!!!
            if (isNumber() && other.isNumber()) {

                if (null == asNumber() || null == other.asNumber()) {

                    return false;
                }
                return asNumber().compareTo(other.asNumber()) == 0;
            }

            if (isDate() && other.isDate()) {

                if ((null==asDate()) && (null==other.asDate())) {
                    return true;
                } else if (null == asDate() || null == other.asDate()) {
                    return false;
                }
                return asDate().compareTo(other.asDate()) == 0;
            }
        }
        return false;
    }

    public String getAsPrimitiveLiteral() {

        String r = getCodice();
        if (isNumber() && null != asNumber()) {
            r = asNumber().toString();
        }
        return r;

    }

    private BigDecimal getBigDecimalInternalValue() {
        return this.internalBigDecimal;
    }

    @SuppressWarnings("nls")
    public String getCanonicalForm() {

        return String.format("%s;%s;%s", getTipo(), getParametro(), getCodice());
    }

    @SuppressWarnings({ "nls" })
    public String getCellTooltip() {
        if (null == getSmeupClass() || null == getSmeupClass() || null == getSmeupClass().getCanonicalForm()) {
            return "";
        }
        if (this.isButton() && null != this.getCodice()) {
            String toolTip = "";
            try {
                toolTip = getCodice().split(";", 5)[3];
            } catch (final ArrayIndexOutOfBoundsException e) {
                LOGGER.log(Level.WARNING, "J4;BTN without tooltip {0}", e.getMessage());
            }

            return toolTip;
        }

        return "";
    }

    public String getCodice() {
        return this.codice;
    }

    private Date getDateInternalValue() {
        return this.internalDate;
    }

    public Character getDecimalSeparator() {
        return this.decimalSeparator;
    }

    @SuppressWarnings("nls")
    public String getDefaultFUN() {

        // TODO togliere questo if: lo manteniamo finche` il client joomla
        // non viene modificato di conseguenza
        if ("J1".equals(getSmeupClass().getTipo()) && "FUN".equals(getSmeupClass().getParametro())) {

            return getCodice();
        }
        // ------------------------------

        return String.format(DEFAULT_FUN, getCanonicalForm());
    }

    public String getExec() {
        return this.exec;
    }

    public String getFld() {
        return this.fld;
    }

    public Character getGroupingSeparator() {

        return this.groupingSeparator;
    }

    public String getGrp() {
        return this.grp;
    }

    public String getI() {
        return this.i;
    }

    public String getIconName() {
        if (hasIcon()) {
            return getCodice();
        }
        return getCanonicalForm();
    }

    @SuppressWarnings("nls")
    public String getImageExternalURL() {

        String url = null;

        if (isExternalImage() && getCodice() != null) {

            final String[] objUrl = getCodice() != null ? getCodice().split(";") : null;
            url = objUrl != null && objUrl.length > 2 ? objUrl[2] : null;

        }

        return url;
    }

    @SuppressWarnings({ "nls" })
    public String getImageName() {

        if (isImage()) {
            final String code = this.getCodice();
            if (objHasImageBadge()) {
                final String[] split = code.split(";");
                return split[0] + ";" + split[1] + ";" + split[2];
            }
            return code;
        }
        return this.getCanonicalForm();
    }

    public String getLeaf() {
        return this.leaf;
    }

    public String getNome() {
        return this.nome;
    }

    public String getParametro() {
        return null == getSmeupClass() ? null : this.smeupClass.getParametro();
    }

    private PropertyChangeSupport getPcs() {

        return this.pcs;
    }

    private String getRTrimmedCode() {

        if ((null == this.rTrimmedCode) 
                && (null != getCodice()))  {

            this.rTrimmedCode = rtrim(getCodice());
        }
        return this.rTrimmedCode;
    }

    /**
     * @return the smeupClass
     */
    public SmeupClass getSmeupClass() {
        return this.smeupClass;
    }

    public String getStyle() {
        return this.style;
    }

    public String getTesto() {
        return this.testo;
    }

    public String getTipo() {
        return null == getSmeupClass() ? null : getSmeupClass().getTipo();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        if (isNumber() && null != asNumber()) {

            result = prime * result + asNumber().hashCode();

        } else {

            result = prime * result + ((this.codice == null) ? 0 : this.codice.hashCode());
        }
        return result;
    }

    @SuppressWarnings("nls")
    public boolean hasIcon() {
        return "J4;ICO".equals(getSmeupClass().getCanonicalForm());
    }

    public boolean hasImage() {

        return isImage() && !getCodice().trim().isEmpty();
    }

    @SuppressWarnings("nls")
    public boolean hasObjectNavigation() {
        return isObject() && !"".equals(getCodice().trim()) && !getSmeupClass().getCanonicalForm().startsWith("J4")
                && !isText() && !isNumber() && !"VO;COD_VER".equals(getSmeupClass().getCanonicalForm())
                && !"VO;COD_SOS".equals(getSmeupClass().getCanonicalForm())
                && !"VO;COD_EXT".equals(getSmeupClass().getCanonicalForm())
                && !"VO;COD_AGG".equals(getSmeupClass().getCanonicalForm());
    }

    @SuppressWarnings("nls")
    public boolean hasRightButton() {
        return !(!isObject() || null == getCodice() || "".equals(getCodice())
                || "J1;STR".equals(getSmeupClass().getCanonicalForm())
                || "J1;TXT".equals(getSmeupClass().getCanonicalForm())
                || "V2;SI/NO".equals(getSmeupClass().getCanonicalForm()) || "NR".equals(getTipo())
                || "**".equals(getTipo()) || hasIcon() || isWebIco()
                || "J4;BTN".equals(getSmeupClass().getCanonicalForm())
                || "J4;BRC".equals(getSmeupClass().getCanonicalForm())
                || "J4;BAR".equals(getSmeupClass().getCanonicalForm())
                || "VO;COD_SOS".equals(getSmeupClass().getCanonicalForm())
                || "VO;COD_AGG".equals(getSmeupClass().getCanonicalForm())
                || "VO;COD_VER".equals(getSmeupClass().getCanonicalForm())
                || (isDate() && "00000000".equals(getCodice().trim())));
    }

    @SuppressWarnings("nls")
    public boolean hasTextcode() {
        return !(null == getSmeupClass().getCanonicalForm() || "V2;SI/NO".equals(getSmeupClass().getCanonicalForm())
                || getSmeupClass().getCanonicalForm().startsWith("J4")
                || "VO;COD_VER".equals(getSmeupClass().getCanonicalForm())
                || "IN;WEB".equals(getSmeupClass().getCanonicalForm()));
    }

    @SuppressWarnings("nls")
    private void initFromCanonicalForm(final String canonicalForm) {

        String[] vs = canonicalForm.split(";", 3);
        final boolean correct = vs.length == 3;
        if (!correct) {

            final String corrected = tryToCorrect(canonicalForm, vs.length - 1);
            vs = corrected.split(";", -1);
        }

        final SmeupClass sc = new SmeupClass();
        sc.setTipo(vs[0]);
        sc.setParametro(vs[1]);
        this.smeupClass = sc;
        this.codice = vs[2];
    }

    @SuppressWarnings("nls")
    public boolean isBar() {

        return "J4;BAR".equals(getSmeupClass().getCanonicalForm());
    }

    @SuppressWarnings("nls")
    public boolean isBoolean() {

        return "V2;SI/NO".equals(getSmeupClass().getCanonicalForm());
    }

    @SuppressWarnings("nls")
    public boolean isButton() {

        return "J4;BTN".equals(getSmeupClass().getCanonicalForm());
    }

    @SuppressWarnings("nls")
    public boolean isCodVer() {
        return getCodice() != null && !getCodice().trim().isEmpty()
                && "VO;COD_VER".equals(getSmeupClass().getCanonicalForm());
    }

    @SuppressWarnings("nls")
    public boolean isColor() {
        return "J1;COL".equals(getSmeupClass().getCanonicalForm());
    }

    @SuppressWarnings("nls")
    public boolean isDate() {

        return getSmeupClass().getTipo().equals("D8");
    }

    public boolean isDisableChangeListener() {
        return this.disableChangeListener;
    }

    @SuppressWarnings("nls")
    public boolean isEmpty() {
        if (getSmeupClass() == null) {
            return true;
        }
        if (getCodice() == null) {
            return true;
        }
        return getCanonicalForm().equals(";;");
    }

    @SuppressWarnings("nls")
    public boolean isExternalImage() {
        return getCodice() != null && getCodice().startsWith("J1;URL");
    }

    @SuppressWarnings("nls")
    public boolean isFile() {
        return "J7".equals(getTipo()) || "J9".equals(getTipo())
                || ("J1;PATHFILE".equals(getSmeupClass().getCanonicalForm()));
    }

    @SuppressWarnings("nls")
    public boolean isGraph() {
        return "J4;GRA".equals(getSmeupClass().getCanonicalForm());
    }

    @SuppressWarnings({ "nls" })
    public boolean isImage() {

        return "J4;IMG".equals(this.getSmeupClass().getCanonicalForm());

    }

    @SuppressWarnings({ "nls" })
    public boolean isJLSmeupObject() {
        return "JL".equals(getTipo());
    }

    @SuppressWarnings("nls")
    public boolean isMemo() {

        boolean isMemo = false;
        final String tipo = null == getSmeupClass() ? null : getSmeupClass().getTipo();
        final String parametro = null == getSmeupClass() ? null : getSmeupClass().getParametro();
        if (null == tipo || tipo.trim().isEmpty() || "J1".equals(tipo) || "J5".equals(tipo) || "J6".equals(tipo)
                || "J7".equals(tipo) || "J8".equals(tipo) || "J9".equals(tipo)) {

            isMemo = null != getCodice() && getCodice().length() > 32;
        }

        if ("J1".equals(tipo) && "STR".equals(parametro)) {

            isMemo = null != getCodice() && getCodice().length() > 16;
        }

        return isMemo;
    }

    @SuppressWarnings("nls")
    public boolean isNumber() {

        return getSmeupClass().getTipo().equals("NR") || getSmeupClass().getTipo().equals("NP");

    }

    public boolean isObject() {
        boolean isObj = false;
        if (null != getSmeupClass() && !getTipo().isEmpty()) {
            isObj = true;
        }
        return isObj;
    }

    public boolean isOggetto() {
        return getSmeupClass().isOggetto();
    }

    @SuppressWarnings("nls")
    public boolean isPassword() {
        return "J1;PWD".equals(getSmeupClass().getCanonicalForm());
    }

    @SuppressWarnings("nls")
    public boolean isPositiveNumber() {
        return getSmeupClass().getTipo().equals("NP");
    }

    @SuppressWarnings("nls")
    public boolean isProgressBar() {
        return "J4;PGB".equals(getSmeupClass().getCanonicalForm());
    }

    @SuppressWarnings("nls")
    public boolean isRadio() {
        return "V2;RADIO".equals(getSmeupClass().getCanonicalForm());
    }

    @SuppressWarnings("nls")
    public boolean isText() {
        return "J1;STR".equals(getSmeupClass().getCanonicalForm())
                || "J1;TXT".equals(getSmeupClass().getCanonicalForm()) || ";".equals(getSmeupClass().getCanonicalForm())
                || "**;".equals(getSmeupClass().getCanonicalForm());
    }

    @SuppressWarnings("nls")
    public boolean isTime() {
        return "I1".equals(getTipo()) || "I2".equals(getTipo());
    }

    @SuppressWarnings("nls")
    public boolean isWebIco() {
        return "J4;WEBICO".equals(getSmeupClass().getCanonicalForm());
    }

    @SuppressWarnings("nls")
    public boolean isWebLink() {

        return "IN;WEB".equals(getSmeupClass().getCanonicalForm());
    }

    @SuppressWarnings({ "nls" })
    public boolean objHasImageBadge() {
        if (isImage() && this.getCodice().split(";").length >= 4) {
            final String exp = this.getCodice().split(";", 4)[3];
            return exp.startsWith("B(");
        }

        return false;

    }

    @SuppressWarnings("nls")
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {

        setBigDecimalInternalValue(null);
        setDateInternalValue(null);
        setRTrimmedCode(null);
        if (evt.getSource() instanceof SmeupObject && "smeupClass".equals(evt.getPropertyName())) {

            final SmeupClass sc = (SmeupClass) evt.getNewValue();
            sc.addPropertyChangeListener(this);
            final SmeupClass oldSC = (SmeupClass) evt.getOldValue();
            if (null != oldSC) {

                oldSC.removePropertyChangeListener(this);
            }
        }
    }

    public DecimalFormat retrieveOrCreateDecimalFormat() {

        DecimalFormat df;
        DecimalFormatSymbols decimalFormatSymbols;
        df = (DecimalFormat) NumberFormat.getNumberInstance();

        df.applyPattern(SMEUP_NUMBER_PATTERN);

        decimalFormatSymbols = df.getDecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator(getDecimalSeparator());
        if (null != getGroupingSeparator()) {
            decimalFormatSymbols.setGroupingSeparator(getGroupingSeparator());
        }
        df.setDecimalFormatSymbols(decimalFormatSymbols);

        df.setParseBigDecimal(true);
        return df;
    }

    private DecimalFormat retrieveOrCreateDecimalFormat2() {

        DecimalFormat df;

        DecimalFormatSymbols decimalFormatSymbols;
        df = (DecimalFormat) NumberFormat.getNumberInstance();

        df.applyPattern(SMEUP_NUMBER_PATTERN2);

        decimalFormatSymbols = df.getDecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator(getDecimalSeparator());
        if (null != getGroupingSeparator()) {
            decimalFormatSymbols.setGroupingSeparator(getGroupingSeparator());
        }
        df.setDecimalFormatSymbols(decimalFormatSymbols);

        df.setParseBigDecimal(true);
        return df;
    }

    @SuppressWarnings("nls")
    public String getTimePattern() {
        String pattern = null;
        if (isTime()) {
            pattern = "3".equals(getParametro()) ? I13_HHMM_PATTERN : I12_HHMMSS_PATTERN;
        }
        return pattern;
    }

    @SuppressWarnings("nls")
    public String getTimeColonPattern() {

        String pattern = null;
        if (isTime()) {
            pattern = "3".equals(getParametro()) ? I13_HH_COL_MM_COL_PATTERN : I12_HH_COL_MM_COL_PATTERN;
        }
        return pattern;
    }

    @SuppressWarnings("nls")
    private DateTimeFormatter retrieveSimpleDateFormat() {

        final DateTimeFormatter simpleDateFormat = getCodice().contains("/") ? D8_YY_BAR_MM_BAR_formatter
                : D8_YYMM_formatter;
        return simpleDateFormat;
    }

    @SuppressWarnings("nls")
    private DateTimeFormatter retrieveI12TimeFormat() {
        final DateTimeFormatter simpleDateFormat = getCodice().contains("\\:") ? I12_HH_COL_MM_COL_formatter
                : I12_HHMMSS_formatter;
        return simpleDateFormat;
    }

    @SuppressWarnings("nls")
    private DateTimeFormatter retrieveI3TimeFormat() {
        final DateTimeFormatter simpleDateFormat = getCodice().contains("\\:") ? I13_HH_COL_MM_COL_formatter
                : I13_HHMMSS_formatter;
        return simpleDateFormat;
    }

    public void setBigDecimalInternalValue(final BigDecimal bigDecimalInternalValue) {
        this.internalBigDecimal = bigDecimalInternalValue;
    }

    @SuppressWarnings("nls")
    public void setCodice(final String codice) {

        final String oldValue = this.codice;
        this.codice = codice;

        // if (getCodice() == null || getCodice().trim().isEmpty()) {
        if (this.codice == null || this.codice.trim().isEmpty()) {
            setTesto("");
        }

        if (!this.disableChangeListener) {

            getPcs().firePropertyChange("codice", oldValue, codice);
        }
    }

    public void setCodiceFromNumber(final BigDecimal v) {
        setCodice(bigDecimalToString(v));
    }

    @SuppressWarnings("nls")
    public String bigDecimalToString(final BigDecimal v) {
        final BigDecimal val = v;
        if (null == val) {
            return "";
        } else {
            final DecimalFormat df = val.compareTo(BigDecimal.ZERO) < 0 ? retrieveOrCreateDecimalFormat2()
                    : retrieveOrCreateDecimalFormat();
            return df.format(val);
        }
    }

    private void setDateInternalValue(final Date internalDate) {
        this.internalDate = internalDate;
    }

    @SuppressWarnings("nls")
    public void setDecimalSeparator(final Character decimalSeparator) {

        final Character oldValue = this.decimalSeparator;
        this.decimalSeparator = decimalSeparator;
        if (!this.disableChangeListener) {

            getPcs().firePropertyChange("decimalSeparator", oldValue, decimalSeparator);
        }
    }

    public void setDisableChangeListener(final boolean disableChangeListener) {
        this.disableChangeListener = disableChangeListener;
    }

    public void setExec(final String exec) {
        this.exec = exec;
    }

    public void setFld(final String fld) {
        this.fld = fld;
    }

    @SuppressWarnings("nls")
    public void setGroupingSeparator(final Character groupingSeparator) {

        final Character oldValue = this.groupingSeparator;
        this.groupingSeparator = groupingSeparator;
        if (!this.disableChangeListener) {

            getPcs().firePropertyChange("groupingSeparator", oldValue, groupingSeparator);
        }
    }

    public void setGrp(final String grp) {
        this.grp = grp;
    }

    public void setI(final String i) {
        this.i = i;
    }

    public void setLeaf(final String leaf) {
        this.leaf = leaf;
    }

    public void setNome(final String nome) {
        this.nome = nome;
    }

    private void setRTrimmedCode(final String rTrimmedCode) {
        this.rTrimmedCode = rTrimmedCode;
    }

    @SuppressWarnings("nls")
    public void setSmeupClass(final SmeupClass smeupClass) {

        final SmeupClass oldValue = this.smeupClass;
        this.smeupClass = smeupClass;
        if (!this.disableChangeListener) {

            getPcs().firePropertyChange("smeupClass", oldValue, smeupClass);
        }
    }

    public void setStyle(final String style) {
        this.style = style;
    }

    public void setTesto(final String testo) {
        this.testo = testo;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        String tipo = "";
        String parametro = "";
        final String cod = this.codice == null ? "" : getCodice();

        if (this.smeupClass != null) {
            tipo = getSmeupClass().getTipo() == null ? "" : getSmeupClass().getTipo();
            parametro = getSmeupClass().getParametro() == null ? "" : getSmeupClass().getParametro();
        }
        return String.format("(%s;%s;%s)", tipo, parametro, cod);
    }

    @SuppressWarnings("nls")
    public String getValueFromProgressBar() {
        String cod = getCodice();

        if (cod.contains(";")) {
            String code = cod.split(";")[1];
            if (code.contains("\\\\")) {
                code = code.split("\\\\")[0];
            }
            cod = code;
        }

        final String dsep = getDecimalSeparator().toString();
        if (null != dsep && !"".equals(dsep)) {
            cod = cod.replace(dsep, ".");
            try {
                cod = String.valueOf((int) Math.round(Double.parseDouble(cod)));
            } catch (final NumberFormatException e) {
                cod = "0";
            }
        }

        return cod;
    }
}