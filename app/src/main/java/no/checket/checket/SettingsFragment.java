package no.checket.checket;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingsFragment extends PreferenceFragmentCompat {

    private IntroSlideManager mIntroSlideManager;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_pref);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

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

                deletePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if(getContext() != null) {
                            new MaterialAlertDialogBuilder(getContext()).setTitle(R.string.dialog_delete_title).setMessage(R.string.dialog_delete_msg)
                                    .setNegativeButton(R.string.dialog_delete_neg, null)
                                    .setPositiveButton(R.string.dialog_delete_pos, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            deleteAccount();
                                        }
                                    }).show();
                        }
                        return true;
                    }
                });
            }
        }
    }

    private void deleteAccount() {
        // A function that will delete all saved data from a user on Google Firebase
        // This includes any task in the future, present or past, profile any achievements, any custom name and authentication
        FirebaseUser currentUser = mAuth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");

        fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //An error occured

            }
        });

        if(currentUser != null) {
            firestore.collection("achievements").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot thisDoc : task.getResult()) {
                            // Check if the UID matches logged in users' UID
                            if (thisDoc.getString("uid").equals(mAuth.getCurrentUser().getUid())) {
                                firestore.collection("achievements").document(thisDoc.getId()).delete();
                            }
                        }
                    }
                }
            });

            firestore.collection("tasks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot thisDoc : task.getResult()) {
                            // Check if the UID matches logged in users' UID
                            if (thisDoc.getString("uid").equals(mAuth.getCurrentUser().getUid())) {
                                firestore.collection("tasks").document(thisDoc.getId()).delete();
                            }
                        }
                    }
                }
            });

            firestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot thisDoc : task.getResult()) {
                            // Check if the UID matches logged in users' UID
                            if (thisDoc.getString("uid").equals(mAuth.getCurrentUser().getUid())) {
                                firestore.collection("users").document(thisDoc.getId()).delete();
                            }
                        }
                    }
                }
            });

            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "An error occurred while deleting data, please try again.", Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}