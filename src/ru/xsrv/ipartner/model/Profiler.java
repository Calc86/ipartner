package ru.xsrv.ipartner.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Calc on 16.12.2014.
 */
public class Profiler {
    private Map<String, Float> time = new HashMap<String, Float>();

    public void start(String name){
        time.put(name, new Float(getTime()));
    }

    public static float getTime(){
        return (float)System.nanoTime() / 1000000000f;
    }

    public void stop(String name){
        if(time.get(name) == null){
            time.put(name, new Float(-1f));
        }
        time.put(name, new Float(getTime() - time.get(name)));
    }

    public void print(){
        System.out.println("Profile: ====================="); //NON-NLS
        for(Map.Entry<String, Float> entry : time.entrySet()){    //тут всего несколько значений(обычно не больше 7ми)d
            System.out.println(String.format("%10s: %6.2f", entry.getKey(), entry.getValue())); //NON-NLS
        }
        System.out.println("==============================");
        clear();
    }

    public void clear(){
        time.clear();
    }

    public void measure(String name){
        if(time.get(name) == null){
            time.put(name, new Float(getTime()));
        }
        else{
            time.put(name, getTime() - time.get(name));
        }
    }

    private static float staticTime = 0;

    public static void start(){
        staticTime = getTime();
    }

    public static String stop(){
        return String.format("Profiler: %.2f", getTime() - staticTime); //NON-NLS
    }
}
