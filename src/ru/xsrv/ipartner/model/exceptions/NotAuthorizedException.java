package ru.xsrv.ipartner.model.exceptions;

/**
 *
 * Created by Calc on 28.11.2014.
 */
public class NotAuthorizedException extends Exception{
    public NotAuthorizedException() {
        super("Попытка выполнить операцию для которой требуется авторизация");
    }
}
