package ru.xsrv.ipartner.server.exceptions;

/**
 * Created by Calc on 29.12.2014.
 */
public class BadResponseException extends ServerException {
    public BadResponseException() {
        super("Сервер прислал не верный ответ.");
    }
}
