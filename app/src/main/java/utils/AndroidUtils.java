package utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

public class AndroidUtils {

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static void saveBooleanPreference(Context ctx, String name, boolean value){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(name, value);
        editor.commit();
    }

    public static boolean loadBooleanPreference(Context ctx, String name){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ctx.getPackageName(), Context.MODE_PRIVATE);
        boolean res = sharedPreferences.getBoolean(name, false);
        return res;
    }
}
