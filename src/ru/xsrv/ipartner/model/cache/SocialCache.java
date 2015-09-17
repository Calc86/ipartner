package ru.xsrv.ipartner.model.cache;

/**
 * Created by Calc on 16.12.2014.
 */
public class SocialCache extends TypedCache {
    public static final String ORIGINAL = "original"; //NON-NLS
    public static final String THUMBNAIL = "thumbnail"; //NON-NLS

    public SocialCache(Type type) {
        super(type);
    }

    @Override
    public byte[] getFile(String id) {
        return super.getFile(ORIGINAL, id);
    }

    @Override
    public boolean contains(String id) {
        return super.contains(ORIGINAL, id);
    }

    @Override
    public void createFile(String id, byte[] data) {
        super.createFile(ORIGINAL, id, data);
    }

    public byte[] getThumbFile(String id) {
        return super.getFile(THUMBNAIL, id);
    }

    public boolean containsThumb(String id) {
        return super.contains(THUMBNAIL, id);
    }

    public void createThumbFile(String id, byte[] data) {
        super.createFile(THUMBNAIL, id, data);
    }

    /**
     * Возвращает последнюю составляющую пути в строке
     * @param url
     * @return
     */
    public String getID(String url){
        String[] ss = url.split("/");
        String id = ss[ss.length - 1];
        return id;
    }

    public static class Instagram extends SocialCache {
        public Instagram() {
            super(Type.INSTAGRAM);
        }
    }

    public static class VK extends SocialCache {
        public VK() {
            super(Type.VK);
        }
    }

    public static class Facebook extends SocialCache {
        public Facebook() {
            super(Type.FACEBOOK);
        }
    }
}
