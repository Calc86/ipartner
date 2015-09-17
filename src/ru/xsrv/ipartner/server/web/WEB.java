package ru.xsrv.ipartner.server.web;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import ru.xsrv.ipartner.interfaces.ICommand;
import ru.xsrv.ipartner.model.Controller;
import ru.xsrv.ipartner.model.Profiler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPInputStream;

/**
 *
 * Created by Calc on 24.11.2014.
 */
public class WEB extends AsyncTask<Void, Long, byte[]> {
    private static final String TAG = WEB.class.toString();

    private Profiler profiler = new Profiler();

    private URL url;
    private Request request;
    private byte[] postData;
    private int timeout = 10000;

    private Map<String, String> headers = new HashMap<String, String>();
    private List<NameValuePair> postVars = new ArrayList<NameValuePair>();

    /**
     * fill only when inBackground
     */
    private byte[] result;

    private ICommand userPre;
    private ICommand systemPre;
    private ICommand userPost;
    private ICommand systemPost;
    private ICommand userProgress;
    private ICommand systemBackground;
    private ICommand userCancel;
    private float elapsedTime = 0.0f;

    public WEB(URL url, Request request, byte[] postData){
        Log.d(TAG, "construct"); //NON-NLS
        this.url = url;
        this.request = request;
        this.postData = postData;   //проброс ссылки на массив
    }

    public WEB(String url, Request request, byte[] postData) throws MalformedURLException {
        Log.d(TAG, "construct"); //NON-NLS
        this.url = new URL(url);
        this.request = request;
        this.postData = postData;   //так же идет проброс массива по классам, ради экономии памяти мы его не копируем
    }

    protected void addHeader(String key, String value){
        headers.put(key, value);
    }

    protected void addPostVar(String key, String value){
        postVars.add(new BasicNameValuePair(key, value));
    }

    public enum Request {
        GET, POST, PUT
    }

    public byte[] process(){
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
        HttpConnectionParams.setSoTimeout(httpParameters, 10000+12000+38000);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);

        byte[] ret = null;

        Log.d(TAG, "WEB: " + getUrl().toString());
        HttpResponse response;
        try {
            if (request.equals(Request.PUT)){
                HttpPut httpPut = new HttpPut(url.toString());
                httpPut.addHeader("Accept-Encoding", "gzip");
                httpPut.setHeader("User-Agent", Controller.getInstance().getDeviceName());
                httpPut.setEntity(new ByteArrayEntity(postData));
                response = httpClient.execute(httpPut);
                postData = null;
            }
            else if(request.equals(Request.POST)) {
                HttpPost httpPost = new HttpPost(url.toString());
                httpPost.addHeader("Accept-Encoding", "gzip");
                for (Map.Entry<String, String> entry : headers.entrySet())
                {
                    System.out.println(entry.getKey() + "/" + entry.getValue());
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
                httpPost.setHeader("User-Agent", Controller.getInstance().getDeviceName());
                //httpPost.setEntity(new ByteArrayEntity(postData));
                httpPost.setEntity(new UrlEncodedFormEntity(postVars));
                response = httpClient.execute(httpPost);
                postData = null;
            }
            else{
                HttpGet httpGet = new HttpGet(url.toString());
                httpGet.addHeader("Accept-Encoding", "gzip");
                httpGet.setHeader("User-Agent", Controller.getInstance().getDeviceName());
                response = httpClient.execute(httpGet);
            }
            //todo throw exception on 404 and another or return null
            Log.d(TAG,response.getStatusLine().toString());
            if(response.getStatusLine().getStatusCode() != 200){
                Log.e(TAG,response.getStatusLine().toString());
                return null;
            }

            HttpEntity entity = response.getEntity();

            if(entity != null){
                InputStream in = entity.getContent();
                Header contentEncoding = response.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    in = new GZIPInputStream(in);
                }
                ret = streamToByteArray(in);
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * @deprecated стандартная библиотека имеем баг
     * @return byte[] с данными с сервера или null, если произошла ошибка работы с сетью
     */
    public byte[] __process(){
        HttpURLConnection connection = null;
        byte[] ret = null;
        try {
            profiler.measure("web_open_connection"); //NON-NLS
            System.setProperty("http.keepAlive", "false");  // to avoid bug
            connection = (HttpURLConnection) url.openConnection();
            profiler.measure("web_open_connection"); //NON-NLS
            connection.setConnectTimeout(timeout);
            connection.setRequestProperty("Accept-Encoding",""); //NON-NLS
            connection.setRequestMethod(request.toString());
            //http://stackoverflow.com/questions/15411213/android-httpsurlconnection-eofexception
            connection.setRequestProperty("Connection", "close"); //android bug!!! NON-NLS NON-NLS NON-NLS
            connection.setDoInput(true);
            Log.d(TAG, "it is a " + request + " request"); //NON-NLS
            if (request.equals(Request.POST) || request.equals(Request.PUT)) {
                connection.setUseCaches(false);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type","application/json"); //NON-NLS
                Log.d(TAG, "try to get output stream"); //NON-NLS
                profiler.measure("web_get_out"); //NON-NLS
                OutputStream out = connection.getOutputStream();
                profiler.measure("web_get_out"); //NON-NLS
                Log.d(TAG, "try to write out"); //NON-NLS
                profiler.measure("web_write"); //NON-NLS
                out.write(postData);
                out.flush();
                //out.close();

                out = null; // free mem
                postData = null; // free mem
                profiler.measure("web_write"); //NON-NLS
            }

            Log.d(TAG, "try to get response"); //NON-NLS

            profiler.measure("web_get_input"); //NON-NLS

            try {
                ret = streamToByteArray(connection.getInputStream());
            } catch (EOFException e) {
                Log.w(TAG, "==EOF exception, code:" + connection.getResponseCode());
                e.printStackTrace();
            } finally {
                if(ret == null)
                ret = streamToByteArray(connection.getInputStream());
            }
            profiler.measure("web_get_input"); //NON-NLS
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if(connection != null) connection.disconnect();
        }
        return ret;
    }

    @Override
    protected byte[] doInBackground(Void... params) {
        Log.d(TAG, "doInBackground start"); //NON-NLS

        if(systemBackground != null){
            profiler.measure("web_system_background"); //NON-NLS
            systemBackground.execute();
            profiler.measure("web_system_background"); //NON-NLS
        }

        profiler.measure("web_process"); //NON-NLS
        result = process();
        profiler.measure("web_process"); //NON-NLS

        return result;
    }

    private static byte[] streamToByteArray(InputStream stream) throws IOException {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();

        int ch;

        while((ch = stream.read()) != -1){
            ba.write(ch);
        }


        return ba.toByteArray();
    }

    public static byte[] getUrlContent(String url) throws MalformedURLException, ExecutionException, InterruptedException {
        WEB task = new WEB(url, Request.GET, null);
        task.execute();

        return task.get();
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setPostData(byte[] postData) {
        this.postData = postData;   // проброс ссылки на массив
    }

    /**
     * call first systemPre, the userPre
     * @param bytes
     */
    @Override
    protected void onPostExecute(byte[] bytes) {
        //выполнение системных функций
        if(systemPost != null){
            profiler.measure("web_system_post"); //NON-NLS
            systemPost.execute();
            profiler.measure("web_system_post"); //NON-NLS
        }
        elapsedTime = Profiler.getTime() - elapsedTime;
        //выполнение пользовательских функций
        if(userPost != null){
            profiler.measure("web_user_post"); //NON-NLS
            userPost.execute();
            profiler.measure("web_user_post"); //NON-NLS
        }

        profiler.print();
        profiler.clear();
    }

    /**
     * call first userPre, then systemPre
     */
    @Override
    protected void onPreExecute() {
        elapsedTime = Profiler.getTime();
        if(userPre != null){
            profiler.measure("web_user_pre"); //NON-NLS
            userPre.execute();
            profiler.measure("web_user_pre"); //NON-NLS
        }
        if(systemPre != null){
            profiler.measure("web_system_pre"); //NON-NLS
            systemPre.execute();
            profiler.measure("web_system_pre"); //NON-NLS
        }
    }

    @Override
    protected void onCancelled(byte[] bytes) {
        elapsedTime = Profiler.getTime() - elapsedTime;
        if(userCancel != null){
            profiler.measure("web_user_cancel"); //NON-NLS
            userCancel.execute();
            profiler.measure("web_user_cancel"); //NON-NLS
        }
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        if(userProgress != null) userProgress.execute();
    }

    public void setUserPre(ICommand userPre) {
        this.userPre = userPre;
    }

    public void setUserPost(ICommand userPost) {
        this.userPost = userPost;
    }

    public void setUserProgress(ICommand userProgress) {
        this.userProgress = userProgress;
    }

    public void setSystemPost(ICommand systemPost) {
        this.systemPost = systemPost;
    }

    public void setSystemPre(ICommand systemPre) {
        this.systemPre = systemPre;
    }

    public byte[] getResult() {
        return result;
    }

    public void setSystemBackground(ICommand systemBackground) {
        this.systemBackground = systemBackground;
    }

    public URL getUrl() {
        return url;
    }

    public void setUserCancel(ICommand userCancel) {
        this.userCancel = userCancel;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }
}
