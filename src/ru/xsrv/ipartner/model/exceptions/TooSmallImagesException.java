package ru.xsrv.ipartner.model.exceptions;

/**
 *
 * Created by Calc on 05.11.2014.
 */
public class TooSmallImagesException extends Exception {
    public TooSmallImagesException(int count, int needed) {
        super("Too small images in request - " + count + " needed " + needed);
    }
}
