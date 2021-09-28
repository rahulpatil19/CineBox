package patil.rahul.cineboxtma.preferenceutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import patil.rahul.cineboxtma.R;

public class CinePreferences {

    private static final boolean PREF_VIDEO_MODE_DEFAULT_VALUE = true;

    private static final String PREF_IMAGE_QUALITY_DEFAULT_VALUE = "w342";

    public static boolean getVideoMode(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String prefKeyVideoMode = context.getString(R.string.pref_key_video_mode);
        return sharedPreferences.getBoolean(prefKeyVideoMode, PREF_VIDEO_MODE_DEFAULT_VALUE);
    }

    public static String getImageQualityValue(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String prefKeyImageQuality = context.getString(R.string.pref_key_image_quality);
        return sharedPreferences.getString(prefKeyImageQuality, PREF_IMAGE_QUALITY_DEFAULT_VALUE);
    }
}
