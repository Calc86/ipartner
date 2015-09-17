package ru.xsrv.ipartner.server.exceptions;

import ru.xsrv.ipartner.server.v1.responses.Response;

/**
 * Created by Calc on 29.12.2014.
 */
public class ServerException extends Exception {
    protected Response response = null;

    public ServerException(String detailMessage) {
        super(detailMessage);
    }

    public ServerException(Response response) {
        super(response.getError());
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }
}
