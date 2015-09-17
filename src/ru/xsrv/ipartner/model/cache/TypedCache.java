package ru.xsrv.ipartner.model.cache;

import java.util.List;

/**
 * Created by Calc on 16.12.2014.
 */
public class TypedCache extends Cache{
    public static final String DEFAULT = "default"; //NON-NLS
    /**
     * Тип кэша
     */
    private Type type;

    public TypedCache(Type type) {
        this.type = type;
    }

    /**
     * ./type/path
     * @param path
     * @return
     */
    @Override
    protected String getCachePath(String path) {
        return  getType() + super.getCachePath(path);
    }

    protected String getType(){
        return SLASH + type.toString().toLowerCase();
    }

    public enum Type{
        CONTROLLER, INSTAGRAM, VK, FACEBOOK, ORDER_WEB, BANNERS
    }

    public void delete(String path, String name) {
        super.delete(path + SLASH + name);
    }

    public boolean contains(String path, String name) {
        return super.contains(path + SLASH + name);
    }

    public void create(String path, String name, Object object) {
        super.create(path + SLASH + name, object);
    }

    public void createFile(String path, String name, byte[] data) {
        super.createFile(path + SLASH + name, data);
    }

    public <T> T get(String path, String name, Class<T> c) {
        return super.get(path + SLASH + name, c);
    }

    public List<?> getList(String path, String name, java.lang.reflect.Type t) {
        return super.getList(path + SLASH + name, t);
    }

    public byte[] getFile(String path, String name) {
        return super.getFile(path + SLASH + name);
    }

    public String getFilePath(String path){
        return getFilePath(path, DEFAULT);
    }

    public String getFilePath(String path, String name){
        return getCacheDirectory() + "/" + type.toString().toLowerCase() + "/" + path + "/" + name + EXT;
    }

    @Override
    public void delete(String path) {
        delete(path, DEFAULT);
    }

    @Override
    public boolean contains(String path) {
        return contains(path, DEFAULT);
    }

    @Override
    public void create(String path, Object object) {
        create(path, DEFAULT, object);
    }

    @Override
    public <T> T get(String path, Class<T> c) {
        return get(path, DEFAULT, c);
    }

    @Override
    public List<?> getList(String path, java.lang.reflect.Type t) {
        return getList(path, DEFAULT, t);
    }

    @Override
    public byte[] getFile(String path) {
        return getFile(path, DEFAULT);
    }

    @Override
    public void createFile(String path, byte[] data) {
        createFile(path, DEFAULT, data);
    }
}
