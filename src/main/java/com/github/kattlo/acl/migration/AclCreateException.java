package com.github.kattlo.acl.migration;

/**
 * @author fabiojose
 */
public class AclCreateException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AclCreateException(String message) {
        super(message);
    }

    public AclCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
