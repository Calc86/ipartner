package ru.xsrv.ipartner.model.cache;

import android.net.Uri;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.FileUtils;
import ru.xsrv.ipartner.IpartnerApplication;
import ru.xsrv.ipartner.model.gson.UriDeserializer;
import ru.xsrv.ipartner.model.gson.UriSerializer;

import java.io.*;
import java.util.List;

/**
 * Created by Calc on 16.12.2014.
 */
public class Cache {
    private static final String TAG = Cache.class.toString();
    public static final String EXT = ".cache"; //NON-NLS
    public static final String SLASH = "/";

    private File cacheDirectory = IpartnerApplication.getAppContext().getCacheDir();
    private Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriDeserializer()).disableHtmlEscaping().create();

    public File getCacheDirectory() {
        return cacheDirectory;
    }

    protected Gson getGson() {
        return gson;
    }

    protected String getCachePath(String path){
        return SLASH + path + EXT;
    }

    public String getFullCachePath(String path){
        return getCacheDirectory() + getCachePath(path);
    }

    public void clean(){
        File f = new File(getFullCachePath("").replace(EXT, ""));
        Log.d(TAG, "try clean cache: " + f.toString()); //NON-NLS
        try {
            FileUtils.cleanDirectory(f);
            Log.d(TAG, "cache cleaned: " + f.toString()); //NON-NLS
        } catch (IOException e) {
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
        } catch (Exception e){
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(String path){
        Log.d(TAG, "delete " + path); //NON-NLS
        File f = new File(getFullCachePath(path));
        if(f.exists()){
            if(f.delete()){
                Log.d(TAG, "file: " + f.toString() + " deleted"); //NON-NLS
            }
        }
        else{
            Log.w(TAG, "file: " + f.toString() + " not exists"); //NON-NLS
        }
    }

    public boolean contains(String path){
        File f = new File(getFullCachePath(path));
        return f.exists();
    }

    private void absoluteDelete(String path){
        Log.d(TAG, "absoluteDelete " + path); //NON-NLS
        File f = new File(getFullCachePath(path));
        if(f.exists()){
            if(f.delete()){
                Log.d(TAG, "file: " + f.toString() + " deleted");
            }
        }
        else{
            Log.d(TAG, "file: " + f.toString() + " not exists"); //NON-NLS
        }
    }

    public void createFile(String path, byte[] data){
        if(data == null) return;
        //удалить предыдущий, если есть
        absoluteDelete(path);

        //создаем файл
        Log.d(TAG, "create " + path); //NON-NLS

        try {
            //File outputFile = File.createTempFile(getCacheFileName(type), EXT, outputDir);
            File outputFile = new File(getFullCachePath(path));
            Log.d(TAG, "outputFile " + outputFile); //NON-NLS
            File outputDir = new File(outputFile.getParent());
            Log.d(TAG, "outputDir " + outputDir); //NON-NLS
            boolean bRet = outputDir.mkdirs();
            if(!bRet)
                Log.w(TAG, "can not create dirs: " + outputDir);
            FileOutputStream out = new FileOutputStream(outputFile);

            out.write(data);
            Log.d(TAG, "cache file: " + outputFile + " written"); //NON-NLS
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void create(String path, Object object){
        //удалить предыдущий, если есть
        absoluteDelete(path);

        //создаем файл
        Log.d(TAG, "create " + path); //NON-NLS
        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriSerializer()).disableHtmlEscaping().create();
        String json = gson.toJson(object);
        Log.d(TAG, "cache: " + json); //NON-NLS
        try {
            //File outputFile = File.createTempFile(getCacheFileName(type), EXT, outputDir);
            File outputFile = new File(getFullCachePath(path));
            Log.d(TAG, "outputFile " + outputFile); //NON-NLS
            File outputDir = new File(outputFile.getParent());
            Log.d(TAG, "outputDir " + outputDir); //NON-NLS
            boolean bRet =outputDir.mkdirs();
            if(!bRet)
                Log.w(TAG, "can not create dirs: " + outputDir);
            FileOutputStream out = new FileOutputStream(outputFile);

            out.write(json.getBytes());
            Log.d(TAG, "cache file: " + outputFile + " written"); //NON-NLS
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> T get(String path, Class<T> c){
        Log.d(TAG, "get " + path); //NON-NLS

        File f = new File(getFullCachePath(path));
        if(!f.exists()){
            Log.w(TAG, "file: " + f.toString() + " not exists"); //NON-NLS
            return null;
        }

        T object;
        try {
            StringBuffer fileData = new StringBuffer();
            BufferedReader reader = new BufferedReader(
                    new FileReader(f));
            char[] buf = new char[1024];
            int numRead=0;
            while((numRead=reader.read(buf)) != -1){
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }
            reader.close();
            String json = fileData.toString();
            Log.d(TAG, "cache: " + json); //NON-NLS

            object = getGson().fromJson(json, c);
        } catch (FileNotFoundException e) {
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        } catch (StreamCorruptedException e) {
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        } catch (IOException e) {
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        } catch (JsonSyntaxException e) {
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        } catch (IllegalArgumentException e){
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        } catch (RuntimeException e){
            //не позволим кэшу убить нашу программу
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        } catch (Exception e){
            //не позволим кэшу убить нашу программу
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        }

        if(object.getClass().toString().equals(c.toString()))
            return object;
        else
            return null;
    }

    /**
     *
     * @param path
     * @param t
     * @return
     */
    public List<?> getList(String path, java.lang.reflect.Type t){
        Log.d(TAG, "get list " + path); //NON-NLS

        File f = new File(getFullCachePath(path));
        if(!f.exists()){
            Log.w(TAG, "file: " + f.toString() + " not exists"); //NON-NLS
            return null;
        }

        List<?> list;
        try {
            StringBuffer fileData = new StringBuffer();
            BufferedReader reader = new BufferedReader(
                    new FileReader(f));
            char[] buf = new char[1024];
            int numRead=0;
            while((numRead=reader.read(buf)) != -1){
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }
            reader.close();
            String json = fileData.toString();
            Log.d(TAG, "cache: " + json); //NON-NLS

            list = gson.fromJson(json, t);
        } catch (FileNotFoundException e) {
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        } catch (StreamCorruptedException e) {
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        } catch (IOException e) {
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        } catch (JsonSyntaxException e) {
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        } catch (IllegalArgumentException e){
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        } catch (RuntimeException e){
            //не позволим кэшу убить нашу программу
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        } catch (Exception e){
            //не позволим кэшу убить нашу программу
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        }

        return list;
    }

    /**
     *
     * @param path
     * @return byte or null
     */
    public byte[] getFile(String path){
        Log.d(TAG, "get file " + path); //NON-NLS

        File f = new File(getFullCachePath(path));
        if(!f.exists()){
            Log.w(TAG, "file: " + f.toString() + " not exists"); //NON-NLS
            return null;
        }

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FileInputStream fileInputStream = new FileInputStream(f);
            byte[] buf = new byte[1024];
            int numRead = 0;
            while((numRead=fileInputStream.read(buf)) != -1)
                out.write(buf, 0, numRead);

            fileInputStream.close();

            return out.toByteArray();

        } catch (FileNotFoundException e) {
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        } catch (StreamCorruptedException e) {
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        } catch (IOException e) {
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        } catch (IllegalArgumentException e){
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        } catch (RuntimeException e){
            //не позволим кэшу убить нашу программу
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        } catch (Exception e){
            //не позволим кэшу убить нашу программу
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
            absoluteDelete(path);
            return null;
        }
    }

}
