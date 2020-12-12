package patil.rahul.cineboxtma.preferenceutils;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;

import androidx.core.app.ShareCompat;
import androidx.fragment.app.DialogFragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import patil.rahul.cineboxtma.R;
import patil.rahul.cineboxtma.utils.MySingleton;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences,rootKey);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int prefCount = preferenceScreen.getPreferenceCount();

        for (int i = 0; i < prefCount; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }

        Preference clearCachePreference = findPreference(getString(R.string.pref_key_clear_cache));
        clearCachePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                imagePipeline.clearCaches();
                MySingleton.getInstance(getContext()).getRequestQueue().getCache().clear();
                Toast.makeText(getContext(), "Cache Cleared", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        Preference shareAppPreference = findPreference(getString(R.string.pref_key_share));
        shareAppPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=patil.rahul.cineboxtma");
                String mimeType = "text/plane";
                String textToShare = "Checkout CineBox app on Google Play Store. Download it today from " + uri;
                String title = "Complete action using";
                ShareCompat.IntentBuilder.from(getActivity()).setType(mimeType)
                        .setChooserTitle(title)
                        .setText(textToShare)
                        .startChooser();
                return false;
            }
        });

        Preference libraryPreference = findPreference(getString(R.string.pref_key_library_used));
        libraryPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogFragment dialogFragment = new LibraryUsedFragment();
                dialogFragment.show(getFragmentManager(), "library_used");
                return true;
            }
        });

        Preference contactDeveloperPreference = findPreference(getString(R.string.pref_key_contact_developer));
        contactDeveloperPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogFragment developerFragment = new DeveloperFragment();
                developerFragment.show(getFragmentManager(), "contact_developer");
                return true;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (null != preference) {
            if (!(preference instanceof CheckBoxPreference)) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
            }
        }
    }

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();
        String key = preference.getKey();

        if (preference instanceof ListPreference) {
            /* For list preferences, look up the correct display value in */
            /* the preference's 'entries' list (since they have separate labels/values). */
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}