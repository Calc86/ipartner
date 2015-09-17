package ru.xsrv.ipartner.server.exceptions;

/**
 * Created by Calc on 29.12.2014.
 */
public class NoResponseException extends ServerException {
    public NoResponseException() {
        super("Сервер не прислал ответ.");
    }
}
