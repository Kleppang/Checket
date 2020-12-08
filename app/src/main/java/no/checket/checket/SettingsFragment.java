package no.checket.checket;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends PreferenceFragmentCompat {

    private IntroSlideManager mIntroSlideManager;

    private FirebaseAuth mAuth;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_pref);

        mAuth = FirebaseAuth.getInstance();

        if(getContext() != null) {
            mIntroSlideManager = new IntroSlideManager(getContext());
        }

        Preference redoPref = findPreference(getString(R.string.key_IsFirstTime));

        PreferenceCategory deleteCat = findPreference(getString(R.string.key_DeleteAccount_cat));
        Preference deletePref = findPreference(getString(R.string.key_DeleteAccount));

        if(redoPref != null) {
            redoPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if(getContext() != null) {
                        new MaterialAlertDialogBuilder(getContext()).setTitle(R.string.dialog_intro_title).setMessage(R.string.dialog_intro_msg)
                                .setNegativeButton(R.string.dialog_intro_neg, null)
                                .setPositiveButton(R.string.dialog_intro_pos, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mIntroSlideManager.setFirstTime(true);
                                        Intent intent = new Intent(getContext(), IntroSlideActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }).show();
                    }
                    return true;
                }
            });
        }
        if(deletePref != null && deleteCat != null) {
            if(mAuth.getCurrentUser() != null) {
                deleteCat.setVisible(true);
            }
        }
    }
}