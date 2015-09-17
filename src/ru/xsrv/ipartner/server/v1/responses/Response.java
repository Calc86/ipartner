package ru.xsrv.ipartner.server.v1.responses;


import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by calc on 03.11.14.
 */
abstract public class Response {
    public static final String OK = "1"; //NON-NLS
    public static final String FAIL = "0"; //NON-NLS


    protected final static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    //json
    protected String status = FAIL;  //json var
    protected String error = "UNKNOWN_ERROR"; //NON-NLS

    /**
     *
     * @param json
     * @param c
     * @param <T>
     * @return if gson exception - return null
     */
    public static <T extends Response> T fromJson(String json, Class<T> c){
        if(json == null) return null;

        Log.d(Response.class.toString(), json);
        try {
            return gson.fromJson(json, c);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            Log.w(Response.class.toString(), e.getMessage());
            //TODO запихать информацию о том, что пришли "левые данные"
            json = "{\"error\":\"" + e.getMessage() + "\"}"; //NON-NLS
            return gson.fromJson(json, c);
        }
    }

    /**
     *
     * @param data may be null
     * @param c
     * @param <T>
     * @return if data is null - return null, if gson exception - return null
     */
    public static <T extends Response> T fromJson(byte[] data, Class<T> c){
        if(data == null) return null;
        String json = new String(data);
        return fromJson(json, c);
    }

    protected String statusFieldName;
    private long timestamp;
    Map<String, String> other = new HashMap<String, String>();

    protected String getStatus(){
        return status;
    }

    public boolean isOk(){
        String status = getStatus();

        //noinspection SimplifiableIfStatement
        if(status == null) return false;
        else return getStatus().compareTo(OK) == 0;
    }

    public void process(){
        // just do nothing
    }

    public String getError() {
        return error;
    }

}
