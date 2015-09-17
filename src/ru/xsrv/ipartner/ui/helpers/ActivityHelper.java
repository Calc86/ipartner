package ru.xsrv.ipartner.ui.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.apache.http.protocol.HTTP;
import ru.xsrv.ipartner.IpartnerApplication;
import ru.xsrv.ipartner.R;
import ru.xsrv.ipartner.model.Controller;
import ru.xsrv.ipartner.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * Created by Calc on 07.11.2014.
 */
public class ActivityHelper {
    // используются в AuthActivity для дальнейшего вызоыва Activity
    private static Context lastContext = null;
    private static Class<?> next = null;

    //TODO перенести в контроллер
    //private static ItemType currentItemType;

    private ActivityHelper() {
    }

    public static Class<?> getNext() {
        if(next == null) return MainActivity.class;
        return next;
    }

    public static Context getLastContext() {
        return lastContext;
    }

    public static void backToMain(Context context){
        Log.d(ActivityHelper.class.toString(), "back to main with clear task stack"); //NON-NLS
        startActivity(context, MainActivity.class, true);
    }

    public static void startActivity(Context context, Class<?> c, boolean clearStack){
        Intent intent = new Intent(context, c);
        if(clearStack) intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Class<?> c){
        startActivity(context, c, false);
    }


    public static Point getDisplaySize(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    /*public static ItemType getCurrentItemType() {
        return currentItemType;
    }*/

    abstract public static class YesNoDialogListener{
        abstract public void onAnswer(boolean yes);
    }

    public static void yesNoDialog(final Context context, String title, String message, final YesNoDialogListener listener){
        alertDialog(context, title, message, context.getResources().getString(R.string.yes), context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onAnswer(true);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onAnswer(false);
            }
        });
    }

    public static void alertDialog(final Context context, String title, String message,
                                   String positiveButton, String negativeButton,
                                   DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener){
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение
        ad.setPositiveButton(positiveButton, positiveListener);
        ad.setNegativeButton(negativeButton, negativeListener);
        ad.setCancelable(false);

        ad.show();
    }

    public static void alertDialog(final Context context, String title, String message){
        final AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение
        ad.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ad.setCancelable(false);
        ad.show();
    }

    /*/**
     * not bold font
     * @param activity
     * @param layoutID
     */
    /*public static void setFont(Activity activity, int layoutID){
        setFont(activity, layoutID, false);
    }

    public static void setFont(Activity activity, int layoutID, boolean bold){
        ViewGroup group = (ViewGroup) activity.findViewById(layoutID);
        if(group == null) return;

        setFont(group, bold);
    }*/

    // Sets the font on all TextViews in the ViewGroup.
    /*public static void setFont(ViewGroup group, boolean bold) {
        Typeface font = PhotoApplication.getFont(bold);
        int count = group.getChildCount();
        View v;
        for(int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView)v).setTypeface(font);
            } else if(v instanceof ViewGroup) {
                setFont((ViewGroup) v, bold);
            }
        }
    }*/

    public static ProgressDialog createWaitDialog(Context context){
        ProgressDialog mProgressDialog = new ProgressDialog(context);
        //mProgressDialog.setMax(1);
        mProgressDialog.setMessage(context.getResources().getString(R.string.wait));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        return mProgressDialog;
    }

    public static void listAlertDialog(final Context context, String title, List<String> values, DialogInterface.OnClickListener listener){
        listAlertDialog(context, title, values, listener, false);
    }

    public static void listAlertDialog(final Context context, String title, List<String> values, DialogInterface.OnClickListener listener, boolean cancelable){
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        CharSequence[] chs = new CharSequence[values.size()];
        for (int i = 0; i < values.size(); i++) {
            chs[i] = values.get(i);
        }
        ad.setItems(chs, listener);
        ad.setCancelable(cancelable);
        ad.show();
    }

    public static String money(String value){
        return value + " RUB";
    }

    public static String money(int value){
        return money(value + "");
    }

    public static String money(long value){
        return money(value + "");
    }

    public static String moneyAdd(String text, String additional){
        if(additional.equals("0")) return text;
        return text + "(+" + money(additional) + ")";
    }

    public static String moneyAdd(String text, int additional){
        return moneyAdd(text, Integer.toString(additional));
    }

    public static void moneyAdd(TextView view, String text, int additional){
        moneyAdd(view, text, Integer.toString(additional));
    }

    public static void moneyAdd(TextView view, String text, String additional){
        String s = moneyAdd(text, additional);
        Spannable span = new SpannableString(s);
        span.setSpan(new RelativeSizeSpan(0.6f), text.length() , s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(span);
    }

    public static int pxToDp(int px) {
        DisplayMetrics displayMetrics = IpartnerApplication.getAppContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = IpartnerApplication.getAppContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static boolean isWifiEnabled(){
        WifiManager wifi = (WifiManager)IpartnerApplication.getAppContext().getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    public static void openUrl(Context context, Uri url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(url);
        context.startActivity(i);
    }

    public static void openUrlAndActivity(Context context, Uri url,  Class<?> c){
        Intent[] is = new Intent[2];

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(url);
        is[0] = i;

        i = new Intent(context, c);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        is[1] = i;
        context.startActivities(is);
    }

    public static String getNumeral(String numeral, int num){
        String cc = Locale.getDefault().getCountry();
        if(cc.equals("RU")){
            String ending = numeral.substring(numeral.length()-2);
            if(ending.equals("ия")){
                int ost = num % 10;
                if(num >= 11 && num <= 19)
                    return numeral.substring(0, numeral.length()-2) + "ий";
                if(ost == 1)
                    return numeral.substring(0, numeral.length()-2) + "ия";
                if(ost >= 2 && ost <= 4)
                    return numeral.substring(0, numeral.length()-2) + "ии";
                if(ost == 0 || ost >= 5 && ost <= 9)
                    return numeral.substring(0, numeral.length()-2) + "ий";
            }
            else if(ending.substring(1).equals("а")){   //секунда
                int ost = num % 10;
                if(num >= 11 && num <= 19)
                    return numeral.substring(0, numeral.length()-1) + "";
                if(ost == 1)
                    return numeral.substring(0, numeral.length()-1) + "а";
                if(ost >= 2 && ost <= 4)
                    return numeral.substring(0, numeral.length()-1) + "ы";
                if(ost == 0 || ost >= 5 && ost <= 9)
                    return numeral.substring(0, numeral.length()-1) + "";
            }
            return numeral;
        }
        return numeral;
    }

    public static void call(Context context, String num){
        Uri number = Uri.parse("tel:"+num);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        if(isIntentSafe(context, callIntent)) context.startActivity(callIntent);
    }

    /**
     *
     * @param context
     * @param geo ex: "geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California" or "geo:37.422219,-122.08364?z=14"
     */
    public static void map(Context context, String geo){
        Uri location = Uri.parse(geo);
        // Or map point based on latitude/longitude
        // Uri location = Uri.parse("geo:37.422219,-122.08364?z=14"); // z param is zoom level
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
        try {
            if(isIntentSafe(context, mapIntent)) context.startActivity(mapIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            //todo вывести сообщение о не найденной программе
        }
    }

    public static void emailWithAttachmet(Context context, String to, String subject, String text, ArrayList<Uri> attachments){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // The intent does not have a URI, so declare the "text/plain" MIME type
        emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {to}); // recipients
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        emailIntent.putExtra(Intent.EXTRA_STREAM, attachments);

        try {
            if(isIntentSafe(context, emailIntent)) context.startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            //todo вывести сообщение о не найденной программе
        }
    }

    public static boolean isIntentSafe(Context context, Intent intent){
        // Verify it resolves
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;
        return isIntentSafe;
    }
}
