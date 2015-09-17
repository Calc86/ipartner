package ru.xsrv.ipartner.model.exceptions;

/**
 *
 * Created by Calc on 28.11.2014.
 */
public class AlreadyAuthorizedException extends Exception {
    public AlreadyAuthorizedException() {
        super("Невозможно выполнить операцию, так как пользователь авторизован");
    }
}
