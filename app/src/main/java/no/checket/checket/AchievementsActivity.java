package no.checket.checket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AchievementsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewLocked;

    // Firebase, declare instance of Firestore and Auth
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    private List<Achievement> achList;
    private List<Achievement> achListLocked;

    private static final String TAG = "AchievementsActivity";

    // Achievements
    boolean hasGermaphobe = false;
    boolean hasCustomizer = false;
    boolean hasTaskmaster10 = false;
    boolean hasTaskmaster100 = false;
    boolean hasTaskmaster1000 = false;

    /* TODO:
    As a user may still get achievements while not being logged in, connect list of achievements with local database

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        // Firebase, initialize the instance
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null) {
            // User is not logged in, will show local achievements only, for now, kick back to where the user came from
            onBackPressed();
            Toast.makeText(this, "Not logged in", Toast.LENGTH_LONG).show();

            // checkAchievements();
        } else {
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(R.string.achievements);
            }

            achList = new ArrayList<>();
            final AchievementRecAdapter achAdapter = new AchievementRecAdapter(achList);

            achListLocked = new ArrayList<>();
            final AchievementRecAdapter achAdapterLocked = new AchievementRecAdapter(achListLocked);

            recyclerView = findViewById(R.id.achievements_recView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(achAdapter);

            recyclerViewLocked = findViewById(R.id.achievements_recViewLocked);
            recyclerViewLocked.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewLocked.setAdapter(achAdapterLocked);

            /* TODO
            Ensure final list of achievements have the correct checks

             */

            achListLocked.add(new Achievement("Germaphobe", "Cleaned 7 days in a row"));
            achListLocked.add(new Achievement("Customizer", "Set a custom name"));
            achListLocked.add(new Achievement("Taskmaster (10+)", "Created 10 tasks"));
            achListLocked.add(new Achievement("Taskmaster (100+)", "Created 100 tasks"));
            achListLocked.add(new Achievement("Taskmaster (1000+)", "Created 1000 tasks"));

            firestore = FirebaseFirestore.getInstance();
            firestore.collection("achievements").addSnapshotListener(new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(@Nullable QuerySnapshot documents, @Nullable FirebaseFirestoreException err) {
                    if(err == null) {
                        for(DocumentChange thisDoc:documents.getDocumentChanges()) {
                            if(thisDoc.getType() == DocumentChange.Type.ADDED) {
                                // Check if the UID matches logged in users' UID
                                if(thisDoc.getDocument().getString("uid").equals(mAuth.getCurrentUser().getUid())) {
                                    Achievement newAchievement = new Achievement(thisDoc.getDocument().getString("name"), thisDoc.getDocument().getString("desc"));

                                    Achievement checkAgainst = findAchievement(thisDoc.getDocument().getString("name"));

                                    if(checkAgainst != null) {
                                        achListLocked.remove(checkAgainst);
                                    }

                                    if(newAchievement.getName().equals("Germaphobe")) {
                                        Log.d(TAG, "User already has Germaphobe");
                                        hasGermaphobe = true;
                                    } else if(newAchievement.getName().equals("Customizer")) {
                                        Log.d(TAG, "User already has Customizer");
                                        hasCustomizer = true;
                                    } else if(newAchievement.getName().equals("Taskmaster (10+)")) {
                                        Log.d(TAG, "User already has Taskmaster (10+)");
                                        hasTaskmaster10 = true;
                                    } else if(newAchievement.getName().equals("Taskmaster (100+)")) {
                                        Log.d(TAG, "User already has Taskmaster (100+)");
                                        hasTaskmaster100 = true;
                                    } else if(newAchievement.getName().equals("Taskmaster (1000+)")) {
                                        Log.d(TAG, "User already has Taskmaster (1000+)");
                                        hasTaskmaster1000 = true;
                                    }

                                    achList.add(newAchievement);
                                    achAdapter.notifyDataSetChanged();
                                    achAdapterLocked.notifyDataSetChanged();
                                }
                            }
                        }
                    } else {
                        // If an error occurred
                        Log.e(TAG, "Error occurred: " + err.getMessage());
                    }
                }

            });

            checkAchievements();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private Achievement findAchievement(String checkName) {
        for(Achievement ach : achListLocked) {
            if(ach.getName().equals(checkName)) {
                return ach;
            }
        }
        return null;
    }

    private void addAchievementFB(final String achName, String achDesc) {

        Map<String, Object> achMap = new HashMap<>();
        achMap.put("name", achName);
        achMap.put("desc", achDesc);
        achMap.put("uid", mAuth.getUid());

        firestore.collection("achievements").add(achMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // TODO
                // Notification or similar to show the user that they have achieved something
                Toast.makeText(AchievementsActivity.this, "An achievement was added " + achName, Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Unable to add the achievement
            }
        });
    }

    private void checkAchievements() {
        // This function serves as the primary tool to check if a user has unlocked an achievement
        if(mAuth.getCurrentUser() == null) {
            // User not logged in
        } else {
            // User logged in

            firestore = FirebaseFirestore.getInstance();
            firestore.collection("tasks").addSnapshotListener(new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(@Nullable QuerySnapshot documents, @Nullable FirebaseFirestoreException err) {
                    boolean d1 = false;
                    boolean d2 = false;
                    boolean d3 = false;
                    boolean d4 = false;
                    boolean d5 = false;
                    boolean d6 = false;
                    boolean d7 = false;
                    if(err == null) {
                        for(DocumentChange thisDoc:documents.getDocumentChanges()) {
                            if(thisDoc.getType() == DocumentChange.Type.ADDED) {
                                // Firstly, ensure user does not already have the achievement, then check if the UID matches logged in users' UID, check if the Category is Cleaning and only include results from the past 7 days
                                if(!hasGermaphobe && thisDoc.getDocument().getString("uid").equals(mAuth.getCurrentUser().getUid()) && thisDoc.getDocument().getString("category").equals("Cleaning") && Long.parseLong(thisDoc.getDocument().getString("enddate")) >= (System.currentTimeMillis() - 604800000)) {

                                    Calendar taskdate = Calendar.getInstance();
                                    taskdate.setTimeInMillis(Long.parseLong(thisDoc.getDocument().getString("enddate")));

                                    // Comparing todays date and the task's date, setting booleans to indicate whether all 7 days had a Cleaning task

                                    long tasktime = taskdate.getTimeInMillis();
                                    long now = System.currentTimeMillis();

                                    if(tasktime <= (now - 518400000)) {
                                        d7 = true;
                                    } else if(tasktime <= (now - 432000000)) {
                                        d6 = true;
                                    } else if(tasktime <= (now - 345600000)) {
                                        d5 = true;
                                    } else if(tasktime <= (now - 259200000)) {
                                        d4 = true;
                                    } else if(tasktime <= (now - 172800000)) {
                                        d3 = true;
                                    } else if(tasktime <= (now - 86400000)) {
                                        d2 = true;
                                    } else if(tasktime <= now) {
                                        d1 = true;
                                    }

                                    if(d7 && d6 && d5 && d4 && d3 && d2 && d1) {
                                        // If all dates are a-okay
                                        addAchievementFB("Germaphobe", "Cleaned 7 days in a row");
                                        hasGermaphobe = true;
                                    }

                                }
                                // If the user does not already have the achievement Customizer, the UID equals logged in UID, and the task is in the past
                                else if(!hasCustomizer && thisDoc.getDocument().getString("uid").equals(mAuth.getCurrentUser().getUid()) && Long.parseLong(thisDoc.getDocument().getString("enddate")) <= (System.currentTimeMillis())) {

                                    DocumentReference usersReference = firestore.collection("users").document(mAuth.getUid());

                                    usersReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            // Successfully found
                                            addAchievementFB("Customizer", "Set a custom name");
                                            hasCustomizer = true;
                                        }
                                    });
                                }
                            }
                        }
                    } else {
                        // If an error occurred
                        Log.e(TAG, "Error occurred: " + err.getMessage());
                    }
                }

            });
        }
    }
}