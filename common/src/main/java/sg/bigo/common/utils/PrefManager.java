package sg.bigo.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }
}
