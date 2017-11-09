package com.smeup.provider.smeup.connector.as400;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class SmeupClass implements Serializable, Comparable<SmeupClass>, Cloneable {

	private static final long serialVersionUID = 1L;

	private final static int TYPE_LENGTH = 2;

	private static String[] extractTipoPar(final String type) {

		final String[] tipoPar = new String[2];
		tipoPar[0] = type.substring(0, TYPE_LENGTH);
		tipoPar[1] = type.substring(TYPE_LENGTH);
		return tipoPar;
	}

	private String tipo;
	@SuppressWarnings("nls")
	private String parametro = "";

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private boolean disableChangeListener;

	@SuppressWarnings("nls")
	public SmeupClass() {
		this("", "");
	}

	public SmeupClass(final String tipo, final String parametro) {

		super();
		this.tipo = tipo;
		this.parametro = parametro;
	}

	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	@Override
	public SmeupClass clone() {

		SmeupClass c = null;
		try {

			c = (SmeupClass) super.clone();

		} catch (final CloneNotSupportedException e) {

			e.printStackTrace();
			throw new Error();
		}

		c.pcs = new PropertyChangeSupport(c);

		return c;
	}

	@Override
	public int compareTo(final SmeupClass o) {

		int result = getTipo().compareTo(o.getTipo());
		if (0 != result) {

			result = getParametro().compareTo(o.getParametro());
		}
		return result;
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
		final SmeupClass other = (SmeupClass) obj;
		if (this.parametro == null) {
			if (other.parametro != null) {
				return false;
			}
		} else if (!this.parametro.equals(other.parametro)) {
			return false;
		}
		if (this.tipo == null) {
			if (other.tipo != null) {
				return false;
			}
		} else if (!this.tipo.equals(other.tipo)) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("nls")
	public String getCanonicalForm() {

		return getTipo() + ";" + getParametro();
	}

	@SuppressWarnings("nls")
	public String getClassObject() {
		return "OG;;" + getTipo() + getParametro();
	}

	/**
	 * @return the parametro
	 */
	public String getParametro() {
		return this.parametro;
	}

	private PropertyChangeSupport getPcs() {
		return this.pcs;
	}

	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return this.tipo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.parametro == null) ? 0 : this.parametro.hashCode());
		result = prime * result + ((this.tipo == null) ? 0 : this.tipo.hashCode());
		return result;
	}

	public boolean isDisableChangeListener() {
		return this.disableChangeListener;
	}

	public boolean isOggetto() {
		return "OG".equals(this.tipo);
	}

	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	public void setDisableChangeListener(final boolean disableChangeListener) {
		this.disableChangeListener = disableChangeListener;
	}

	/**
	 * @param parametro
	 *            the parametro to set
	 */
	@SuppressWarnings("nls")
	public void setParametro(final String parametro) {

		final String oldValue = this.parametro;
		this.parametro = parametro;
		if (!isDisableChangeListener()) {
			getPcs().firePropertyChange("parametro", oldValue, parametro);
		}
	}

	/**
	 * @param tipo
	 *            the tipo to set
	 */
	@SuppressWarnings("nls")
	public void setTipo(final String tipo) {

		final String oldValue = this.tipo;
		String[] tipoPar = null;
		if (tipo.length() > TYPE_LENGTH) {
			tipoPar = extractTipoPar(tipo);
			this.tipo = tipoPar[0];
			setParametro(tipoPar[1]);
		} else {
			this.tipo = tipo;
		}
		if (!isDisableChangeListener()) {
			getPcs().firePropertyChange("tipo", oldValue, this.tipo);
		}
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {

		return getTipo() + ((null == getParametro() || getParametro().trim().isEmpty()) ? ";" : ";" + getParametro().trim()); //$NON-NLS-2$
	}
}
