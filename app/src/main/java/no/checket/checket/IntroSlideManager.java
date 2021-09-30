package no.checket.checket;

import android.content.Context;
import android.content.SharedPreferences;

public class IntroSlideManager {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;

    private static String IS_FIRST_TIME;

    public IntroSlideManager(Context context) {
        this.context = context;
        IS_FIRST_TIME =  context.getResources().getString(R.string.key_IsFirstTime);
        preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public void setFirstTime(boolean isFirstTime) {
        editor = preferences.edit();
        editor.putBoolean(IS_FIRST_TIME, isFirstTime);
        editor.apply();
    }

    public boolean isFirstTime() {
        // Fetches the stored boolean, if none is found we return true
        return preferences.getBoolean(IS_FIRST_TIME, true);
    }
}
