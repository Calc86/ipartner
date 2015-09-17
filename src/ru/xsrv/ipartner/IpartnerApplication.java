package ru.xsrv.ipartner;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

/**
 *
 * Created by Calc on 18.11.2014.
 */
public class IpartnerApplication extends Application {
    private final static String TAG = IpartnerApplication.class.toString();

    private static Context context;

    private static Typeface font;
    private static Typeface fontBold;

    @Override
    public void onCreate() {
        super.onCreate();

        build(this);
        //setLocale();

    }

    private static void build(Application app){
        context = app.getApplicationContext();

        /*font = Typeface.createFromAsset(context.getAssets(),   context.getResources().getString(R.string.app_font));
        fontBold = Typeface.createFromAsset(context.getAssets(),   context.getResources().getString(R.string.app_font_bold));*/
    }

    /**
     * hard set locale
     */
    private void setLocale(){
        Locale locale = new Locale("ru", "RU");
        //Locale locale = new Locale("de", "DE");
        //Locale locale = new Locale("en", "EN");
        //Locale locale = new Locale("fr", "FR");
        //Locale locale = new Locale("zh", "ZH");
        //Locale locale = new Locale("ar", "AR");
        Locale.setDefault(locale);

        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
    }

    public static Typeface getFont(boolean bold){
        if(bold)
            return fontBold;
        else
            return font;
    }


    public static Context getAppContext(){
        return context;
    }

    public static String getResourceString(int res){
        if(context == null) return "UNKNOWN_STRING"; //NON-NLS
        try {
            return context.getResources().getString(res);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "resource " + res + "not found: " + e.getMessage()); //NON-NLS NON-NLS
            return "NO_STRING"; //NON-NLS
        }
    }

    public static NotificationManager getNotificationManager(){
        return (NotificationManager) getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }




}
