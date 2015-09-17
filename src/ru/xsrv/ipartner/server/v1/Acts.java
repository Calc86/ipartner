package ru.xsrv.ipartner.server.v1;

/**
 *
 * Created by Calc on 29.10.2014.
 */
public enum Acts {
    TEST,
    NEW_SESSION,
    GET_ENTRIES,
    ADD_ENTRY,
    EDIT_ENTRY,
    REMOVE_ENTRY
    ;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
