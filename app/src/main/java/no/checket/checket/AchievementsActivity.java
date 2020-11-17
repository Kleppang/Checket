package no.checket.checket;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewLocked;

    // Firebase, declare instance of Firestore and Auth
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    private List<Achievement> achList;
    private List<Achievement> achListLocked;

    private static final String TAG = "AchievementsActivity";

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

            achListLocked.add(new Achievement("Germaphobe", "Cleaned 7 days in a row"));
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
}