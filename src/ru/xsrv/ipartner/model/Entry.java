package ru.xsrv.ipartner.model;

/**
 * {"id": "4klJeiCKTs", "body": "Вторая запись", "da": "1442236233", "dm": "1442236233"},
 * Created by calc on 17.09.2015.
 */
public class Entry {
    protected String id;
    protected String body;
    protected int da;
    protected int dm;

    public String getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public int getDa() {
        return da;
    }

    public int getDm() {
        return dm;
    }
}
