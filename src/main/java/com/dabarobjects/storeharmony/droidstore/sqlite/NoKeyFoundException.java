package com.dabarobjects.storeharmony.droidstore.sqlite;

/**
 * Created by dabarobjects on 29/08/2018.
 */

public class NoKeyFoundException extends Exception {

    public NoKeyFoundException(String message) {
        super(message);
    }

    public NoKeyFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
