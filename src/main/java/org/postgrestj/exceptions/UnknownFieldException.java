package org.postgrestj.exceptions;

/**
 * Created by @romansergey on 4/12/16.
 */
public class UnknownFieldException extends RuntimeException {
    public UnknownFieldException(String message) {
        super(message);
    }
}
