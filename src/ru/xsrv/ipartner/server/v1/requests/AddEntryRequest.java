package ru.xsrv.ipartner.server.v1.requests;

import ru.xsrv.ipartner.server.v1.Acts;

/**
 * asdfsd
 * Created by Calc on 05.12.2014.
 */
public class AddEntryRequest extends Request {

    public AddEntryRequest(String session, String body) {
        super(Acts.ADD_ENTRY);
        addPostVar(FIELD_SESSION, session);
        addPostVar(FIELD_BODY, body);
    }

    @Override
    public void build() {
        //addPostVar("a", act.toString());
        /*root.addProperty(FIELD_ACT, act.toString());
        root.addProperty(FIELD_TYPE, "android");    //not ios
        root.addProperty("screen", "dpi320");   //пока 320 dpi.
        root.addProperty(FIELD_TIME, Long.toString(time));*/
    }
}
