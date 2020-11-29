package no.checket.checket;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AchievementsActivity extends AppCompatActivity {

    // Firebase, declare instance of Firestore and Auth
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    private List<no.checket.checket.Task> taskList;

    private List<Achievement> achList;
    private AchievementRecAdapter achAdapter;
    private List<Achievement> achListLocked;
    private AchievementRecAdapter achAdapterLocked;

    private ChecketDatabase mDB;

    /*

    Hidden achievements:
    Name            | Description               | Requirements
    -----------------------------------------------------------
    Klimate         | A true environmentalist   | Custom name = Klimate
    It's over 9000! | Created 9001 tasks        | 9000+ tasks


    Shown achievements:
    Name                | Description               | Requirements
    -----------------------------------------------------------
    Customizer          | Set a custom name         | Custom name
    Germaphobe          | Cleaned 7 days in a row   | At least 1 daily Cleaning task in the past 7 days
    Gotta go fast       | 10 tasks in a single day  | At least 10 tasks in the past 24 hours
    Taskmaster (10+)    | Finished 10 tasks         | At least 10 tasks with a end date in the
    Taskmaster (100+)   | Finished 100 tasks        | At least 100 tasks with a end date in the past
    Taskmaster (1000+)  | Finished 1000 tasks       | At least 1000 tasks with a end date in the past


     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        // Firebase, initialize the instance
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mDB = ChecketDatabase.getDatabase(this);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.achievements);
            actionBar.setElevation(0);
        }

        achList = new ArrayList<>();
        achAdapter = new AchievementRecAdapter(achList);

        achListLocked = new ArrayList<>();
        achAdapterLocked = new AchievementRecAdapter(achListLocked);

        final RecyclerView recyclerView = findViewById(R.id.ach_recView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(achAdapter);

        final RecyclerView recyclerViewLocked = findViewById(R.id.ach_recViewLocked);
        recyclerViewLocked.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLocked.setAdapter(achAdapterLocked);

        achListLocked.add(new Achievement("Customizer", "Set a custom name", "User profile"));
        achListLocked.add(new Achievement("Germaphobe", "Cleaned 7 days in a row", "Cleaning"));
        achListLocked.add(new Achievement("Gotta go fast", "10 tasks in a single day", "Miscellaneous"));
        achListLocked.add(new Achievement("Taskmaster (10+)", "Finished 10 tasks", "Miscellaneous"));
        achListLocked.add(new Achievement("Taskmaster (100+)", "Finished 100 tasks", "Miscellaneous"));
        achListLocked.add(new Achievement("Taskmaster (1000+)", "Finished 1000 tasks", "Miscellaneous"));

        TabLayout ach_tabLayout = findViewById(R.id.ach_tabLayout);

        ach_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerViewLocked.setVisibility(View.GONE);
                        break;
                    case 1:
                        recyclerView.setVisibility(View.GONE);
                        recyclerViewLocked.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                achList = mDB.checketDao().loadAllAchievements();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        achAdapter.setData(achList);

                        // Remove any achievements that are already unlocked from the locked list
                        for(Achievement ach : achList) {
                            Achievement checkAgainst = findAchievementLocked(ach.getName());

                            if(checkAgainst != null) {
                                achListLocked.remove(checkAgainst);
                            }
                        }

                        achAdapterLocked.notifyDataSetChanged();

                        checkAchievements();
                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean existsAchievement(String checkName) {
        for(Achievement ach : achList) {
            if(ach.getName().equals(checkName)) {
                return true;
            }
        }
        return false;
    }

    private Achievement findAchievementLocked(String checkName) {
        for(Achievement ach : achListLocked) {
            if(ach.getName().equals(checkName)) {
                return ach;
            }
        }
        return null;
    }

    // Add an achievement locally
    private void addAchievementLocal(final String achName, String achDesc, String achCat) {
        final Achievement newAchievement = new Achievement(achName, achDesc, achCat);

        achList.add(newAchievement);
        achListLocked.remove(findAchievementLocked(achName));

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDB.checketDao().insertAchievement(newAchievement);
            }
        });

        // Sort the lists before displaying
        Collections.sort(achList);
        Collections.sort(achListLocked);

        achAdapter.notifyDataSetChanged();
        achAdapterLocked.notifyDataSetChanged();
    }

    // Add an achievement to Firestore
    private void addAchievementFB(final String achName, String achDesc, String achCat) {

        Map<String, Object> achMap = new HashMap<>();
        achMap.put("name", achName);
        achMap.put("desc", achDesc);
        achMap.put("category", achCat);
        achMap.put("uid", mAuth.getUid());

        final Achievement newAchievement = new Achievement(achName, achDesc, achCat);

        achList.add(newAchievement);
        achListLocked.remove(findAchievementLocked(achName));

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDB.checketDao().insertAchievement(newAchievement);
            }
        });

        // Sort the lists before displaying
        Collections.sort(achList);
        Collections.sort(achListLocked);

        achAdapter.notifyDataSetChanged();
        achAdapterLocked.notifyDataSetChanged();

        firestore.collection("achievements").add(achMap);
    }

    private void checkAchievements() {
        // This function serves as the primary tool to check if a user has unlocked an achievement

        // First check if we have an Internet connection
        boolean hasConnection = CommonFunctions.isConnected(getApplicationContext());

        taskList = new ArrayList<>();

        // Fetch all tasks we'll test on
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                taskList = mDB.checketDao().loadAllTasks();
            }

        });

        // User is logged in and has an active Internet connection
        if(mAuth.getCurrentUser() != null && hasConnection) {
            // Fetches all achievements stored on Firestore
            firestore.collection("achievements").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            if(document.getString("uid").equals(mAuth.getCurrentUser().getUid())) {
                                final Achievement tempach = new Achievement(document.getString("name"), document.getString("desc"), document.getString("category"));
                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDB.checketDao().insertAchievement(tempach);
                                    }
                                });
                            }
                        }
                    }
                }
            });

            // Achievements related to the users collection
            if(!existsAchievement("Customizer") || !existsAchievement("Klimate")) {
                firestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot thisDoc : task.getResult()) {
                                // Check if the UID matches logged in users' UID
                                if(thisDoc.getString("uid").equals(mAuth.getCurrentUser().getUid())) {
                                    if(!existsAchievement("Customizer")) {
                                        // User has set a custom name, award the achievement
                                        addAchievementFB("Customizer", "Set a custom name", "User profile");
                                    }
                                    if(!existsAchievement("Klimate")) {
                                        // Checking for the additional hidden achievement "Klimate"
                                        if(thisDoc.getString("name").toLowerCase().equals("klimate")) {
                                            addAchievementFB("Klimate", "A true environmentalist", "Hidden");
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }

        // Germaphobe & Gotta go fast
        if(!existsAchievement("Germaphobe") || !existsAchievement("Gotta go fast") || !existsAchievement("Taskmaster (10+)") || !existsAchievement("Taskmaster (100+)") || !existsAchievement("Taskmaster (1000+)")) {

            // Booleans used by the achievement "Germaphobe"
            boolean d1 = false;
            boolean d2 = false;
            boolean d3 = false;
            boolean d4 = false;
            boolean d5 = false;
            boolean d6 = false;
            boolean d7 = false;

            // Counter used by the achievement "Gotta go fast"
            int GGF_count = 0;

            for(no.checket.checket.Task thisTask : taskList) {
                // Germaphobe

                if(!existsAchievement("Germaphobe") && thisTask.getHeader().equals("Cleaning") && thisTask.getDate() >= (System.currentTimeMillis() - 604800000)) {
                    // Comparing todays date and the task's date, setting booleans to indicate whether all 7 days had a Cleaning task

                    long tasktime = thisTask.getDate();
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
                        if(hasConnection) {
                            addAchievementFB("Germaphobe", "Cleaned 7 days in a row", "Cleaning");
                        } else {
                            addAchievementLocal("Germaphobe", "Cleaned 7 days in a row", "Cleaning");
                        }
                    }

                } else if(!existsAchievement("Gotta go fast") && thisTask.getDate() >= (System.currentTimeMillis() - 86400000)) {
                    // Gets all of the tasks for a logged in user in the past 24 hours
                    GGF_count++;

                    if(GGF_count >= 10) {
                        if(hasConnection) {
                            addAchievementFB("Gotta go fast", "10 tasks in a single day", "Miscellaneous");
                        } else {
                            addAchievementLocal("Gotta go fast", "10 tasks in a single day", "Miscellaneous");
                        }
                    }
                } else if(!existsAchievement("Taskmaster (10+)") || !existsAchievement("Taskmaster (100+)") || !existsAchievement("Taskmaster (1000+)")) {
                    int taskAmount = taskList.size();

                    if(taskAmount >= 10 && !existsAchievement("Taskmaster (10+)")) {
                        if(hasConnection) {
                            addAchievementFB("Taskmaster (10+)", "Finished 10 tasks", "Miscellaneous");
                        } else {
                            addAchievementLocal("Taskmaster (10+)", "Finished 10 tasks", "Miscellaneous");
                        }
                    } else if(taskAmount >= 100 && !existsAchievement("Taskmaster (100+)")) {
                        if(hasConnection) {
                            addAchievementFB("Taskmaster (100+)", "Finished 100 tasks", "Miscellaneous");
                        } else {
                            addAchievementLocal("Taskmaster (100+)", "Finished 100 tasks", "Miscellaneous");
                        }
                    } else if(taskAmount >= 1000 && !existsAchievement("Taskmaster (1000+)")) {
                        if(hasConnection) {
                            addAchievementFB("Taskmaster (1000+)", "Finished 1000 tasks", "Miscellaneous");
                        } else {
                            addAchievementLocal("Taskmaster (1000+)", "Finished 1000 tasks", "Miscellaneous");
                        }
                    } else if(taskAmount >= 9001 && !existsAchievement("It's over 9000!")) {
                        if(hasConnection) {
                            // The hidden achievement "It's over 9000!"
                            addAchievementFB("It's over 9000!", "Finished 9001 tasks", "Hidden");
                        } else {
                            // The hidden achievement "It's over 9000!"
                            addAchievementLocal("It's over 9000!", "Finished 9001 tasks", "Hidden");
                        }
                    }
                }
            }
        }

        Collections.sort(achList);
        Collections.sort(achListLocked);

        achAdapter.notifyDataSetChanged();
        achAdapterLocked.notifyDataSetChanged();

    }
}