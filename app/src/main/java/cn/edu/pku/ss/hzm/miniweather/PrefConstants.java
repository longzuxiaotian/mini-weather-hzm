package cn.edu.pku.ss.hzm.miniweather;

import android.content.Context;
import android.content.SharedPreferences;
public class PrefConstants {
    
    public static int getAppPrefInt(Context context, String prefName){
    	int result = 0;
		if(context != null){
			SharedPreferences sharedPreferences = context.getSharedPreferences(prefName,context.MODE_PRIVATE);
			if(sharedPreferences!=null){
				result = sharedPreferences.getInt(prefName, 0);
			}
		}
		return result;
    }


	public static void putAppPrefInt(Context context, String prefName, int value) {
		if(context!=null){
			SharedPreferences.Editor editor = context.getSharedPreferences(prefName, context.MODE_PRIVATE).edit();
			editor.putInt(prefName, value);
			editor.apply();;
		}
	}
}
