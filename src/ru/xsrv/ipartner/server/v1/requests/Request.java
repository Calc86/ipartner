package ru.xsrv.ipartner.server.v1.requests;


import android.content.res.Resources;
import android.util.Base64;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import ru.xsrv.ipartner.IpartnerApplication;
import ru.xsrv.ipartner.server.v1.Acts;
import ru.xsrv.ipartner.server.web.WEB;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Корневой класс запроса на сервер.
 * Использование CustomRequest r = Request.build("{json}", CustomRequest.class)
 *
 * https://docs.google.com/document/d/1PiPtk922c-ol3YVWJye7CPMfEV BUQb425rnt_Vg-qq4/edit?pli=1
 *
 * TODO: Скрестить Request c Response
 * Created by Calc on 29.10.2014.
 */
abstract public class Request extends WEB {
    private static final String TAG = Request.class.toString();
    public static boolean debug = false;

    public static final String FIELD_SESSION = "session";
    public static final String FIELD_BODY = "body";

    @SuppressWarnings("MagicCharacter")
    public static final byte JSON_START = '{';
    @SuppressWarnings("MagicCharacter")
    public static final byte JSON_END = '}';

    public static final String version = "1";
    public static final String HASH = "MD5"; //NON-NLS

    //public static final String SERVER_ADDRESS = "http://bnet.i-partner.ru/testAPI/?token=GXLVzDR-DM-MkpFKHn&a=new_session";
    public static final String TOKEN = "O5wNp1I-xQ-3K7mdst";
    public static final String SERVER_ADDRESS = "https://bnet.i-partner.ru/testAPI/";
    public static final String EMAIL = "calc@list.ru";
    public static final String NAME = "Calc";

    protected Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping().create();

    protected final Acts act;
    protected long time;

    protected byte[] binary;
    protected ByteArrayOutputStream byteBinaryStream;

    protected JsonObject root = new JsonObject();

    //обязательные параметры
    private String sig;
    public static final String SALT = "wOwsAlTsoSaLt$o$aFeVERYsecure"; //NON-NLS

    protected int defaultStringResourceResponse = 0;

    private static URL serverUrl(){
        try {
            return new URL(SERVER_ADDRESS);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Request(Acts act) {
        super(serverUrl(), Request.POST, null);
        addHeader("token", TOKEN);
        //addPostVar("token", TOKEN);
        addPostVar("a", act.toString());
        this.act = act;
        this.time = timestamp();
    }

    private static String createHexString(byte[] bytes){
        // Create Hex String
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String h = Integer.toHexString(0xFF & b);
            while (h.length() < 2)
                h = "0" + h;
            hexString.append(h);
        }
        return hexString.toString();
    }

    public static String md5(final String s){
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(HASH);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            return createHexString(messageDigest);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String base64(byte[] bytes){
        return new String(Base64.encode(bytes, Base64.DEFAULT));
    }

    public static String base64(String str){
        return base64(str.getBytes());
    }

    public static String base64Decode(String string){
        return new String(Base64.decode(string, Base64.DEFAULT));
    }

    protected long timestamp(){
        return System.currentTimeMillis() / 1000 + 60*60;
    }

    protected String createToken(Map<String, String> inputParams){
        String hash = "";
        for (String key : inputParams.keySet()){
            String val = inputParams.get(key);
            hash += key + "=" + val;
        }

        String saltHash = md5(SALT);

        return md5(hash + saltHash);
    }

    abstract public void build();

    @Override
    public String toString() {
        this.build();

        return gson.toJson(root);
    }

    /*private class Task extends AsyncTask<String, Void, String>{
        private IOException exception;
        private boolean work = true;
        private String result;

        @Override
        protected String doInBackground(String... params) {
            try{
                result = _send();
                work = false;
                return result;
            }catch (IOException e){
                work = false;
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            work = false;
        }
    }*/

    public String send() throws IOException {
        /*Task task = new Task();

        task.execute("");

        //wait for task
        while(task.work){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.w(Request.class.toString(), e.getMessage());
                e.printStackTrace();
            }
        }

        if(task.exception != null) throw task.exception;

        return task.result;*/

        execute();

        //wait for task
        byte[] ret;
        try {
            //byte[] ret = process();
            ret = get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "";
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "";
        }

        if(ret == null) return "";
        else return new String(ret);
    }

    /**
     *
     * @return byte[] array of received data, or default string from defaultStringResourceResponse or null if defaultStringResourceResponse not set
     */
    @Override
    public byte[] process(){
        byte[] ret = _send();
        if(ret == null && defaultStringResourceResponse != 0){
            try {
                String def = IpartnerApplication.getAppContext().getResources().getString(defaultStringResourceResponse);
                return def.getBytes();
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                return ret;
            }
        }

        return ret;
    }

    /**
     *
     * @return byte array or null on error
     */
    private byte[] _send(){
        String json = this.toString();
        //String data;
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try{
            if(hasBinary()){
                data.write("--with-binary\n".getBytes()); //NON-NLS
                data.write(json.getBytes());
                data.write("\n".getBytes());
                data.write("--binary\n".getBytes()); //NON-NLS
                if(binary == null && byteBinaryStream != null){
                    data.write(byteBinaryStream.toByteArray());
                    byteBinaryStream = null;    //убираем из памяти, так как уже отправили
                }else if(binary != null){
                    data.write(binary);
                    binary = null;
                }else{
                    Log.w(TAG, "try to upload null data"); //NON-NLS
                }
            }
            else {
                data.write(json.getBytes());
            }
            setPostData(data.toByteArray());
        } catch (IOException e){
            return null;
        }

        Log.d(TAG, this.toString());

        byte[] ret = super.process();

        if(ret == null || ret.length == 0){
            //TODO http error 500 возвращает пустой ret
            return null;
        }
        //убрать костыль когда сервер перестанет слать мусор
        //(а можно и не убирать, пусть будет)
        Log.d(TAG, new String(ret));
        if(ret[0] != JSON_START){
            int jsonStart = Arrays.asList(ret).indexOf(JSON_START);
            //TODO тут крашится еслт нет "{"
            //TODO вход не будет удачным, нужно выдать сообщение о проблемах сервера
            if(jsonStart == -1) return null;
            byte[] newRet = new byte[ret.length - jsonStart];
            System.arraycopy(ret, jsonStart, newRet, 0, newRet.length);
            ret = newRet;
        }

        Log.d(TAG, new String(ret));

        return ret;
    }

    protected String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/))); //NON-NLS
    }

    public byte[] getBinary() {
        return binary;  //binary имеет большой размер, так что его возвращаем как есть
    }

    public boolean hasBinary() {
        return binary != null || byteBinaryStream != null;
    }

    public void setByteBinaryStream(ByteArrayOutputStream byteBinaryStream) {
        this.byteBinaryStream = byteBinaryStream;
    }
}
