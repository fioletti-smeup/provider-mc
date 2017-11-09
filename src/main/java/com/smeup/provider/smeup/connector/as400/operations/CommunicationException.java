/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smeup.provider.smeup.connector.as400.operations;

/**
 *
 * @author gianluca
 */
public class CommunicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CommunicationException(final Throwable cause) {

        super(cause);
    }

    public CommunicationException(final String message, final Throwable cause) {

        super(message, cause);
    }

    public CommunicationException(final String message) {

        super(message);
    }

    public CommunicationException() {

        super();
    }
}
