package ru.xsrv.ipartner.ui.helpers;

import android.content.Context;
import android.util.Log;

/**
 *
 * Created by Calc on 07.11.2014.
 */
public class ResourceMap {
    protected ResourceMap() {
    }

    /**
     *
     * @param context
     * @param name
     * @param type
     * @return resource id or 0
     */
    public static int get(Context context, String name, String type){
        int res = context.getResources().getIdentifier(name, type, context.getPackageName());
        if(res == 0){
            Log.d(ResourceMap.class.toString(), "resource: " + type + "." + name + " not found"); //NON-NLS
        }
        else{
            Log.d(ResourceMap.class.toString(), "resource: " + type + "." + name + " mapped to " + res); //NON-NLS
        }
        return res;
    }
}
