/**
 * 
 */
package com.llsfw.core.exception;

/**
 * @author Administrator
 *
 */
public class SystemException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     */
    public SystemException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public SystemException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

}
