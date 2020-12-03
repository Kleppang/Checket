package no.checket.checket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        // Interface for communication with NewTaskFragment
        NewTaskFragment.NewTaskDialogListener{

    private IntroSlideManager mIntroSlideManager;

    BottomAppBar main_BottomAppBar;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Menu navigationMenu;
    CircleImageView profileImage;

    private TextView txtV_email;
    private TextView txtV_name;
    private MenuItem MI_LoginReg;
    private MenuItem MI_Profile;

    // Recycler view
    private LinkedList<no.checket.checket.Task> mTaskList = new LinkedList<>();
    private RecyclerView mRecyclerView;
    private TaskListAdapter mAdapter;

    // Firebase & Auth, declare instance
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;

    private ChecketDatabase mDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase, initialize the instance
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mDB = ChecketDatabase.getDatabase(this);

        // Start by checking if this is the first launch, decides which view to show
        mIntroSlideManager = new IntroSlideManager(this);
        if(mIntroSlideManager.isFirstTime()) {
            // If first time, launch the intro slider

            startActivity(new Intent(this, IntroSlideActivity.class));
            finish();
        } else {
            // This is not the first time the app has been launched, continue as normal
            setContentView(R.layout.activity_main);

            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            navigationMenu = navigationView.getMenu();

            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    main_BottomAppBar,
                    R.string.openNavDrawer,
                    R.string.closeNavDrawer
            );

            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId())
                    {
                        case R.id.nav_LoginReg:
                            // User has selected to login / logout
                            if(mAuth.getCurrentUser() != null) {
                                // User is currently logged in, show logout dialog
                                new MaterialAlertDialogBuilder(MainActivity.this).setTitle(R.string.dialog_logout_title).setMessage(R.string.dialog_logout_msg)
                                    .setNegativeButton(R.string.dialog_logout_neg, null)
                                    .setPositiveButton(R.string.dialog_logout_pos, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // User has confirmed that they want to log out
                                            mAuth.signOut();
                                            // Reload the activity once we've signed out the user
                                            finish();
                                            startActivity(getIntent());
                                        }
                                    }).show();
                            } else {
                                // Starts the LoginRegisterActivity
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                            break;
                        case R.id.nav_settings:
                            // Starts the SettingsActivity
                            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(intent);
                            break;

                        case R.id.nav_profile:
                            //Starts the ProfileActivity
                            Intent intentP = new Intent(MainActivity.this, ProfileActivity.class);
                            startActivity(intentP);
                            break;

                        case R.id.nav_achievements:
                            // Starts the AchievementsActivity
                            Intent intentAch = new Intent(MainActivity.this, AchievementsActivity.class);
                            startActivity(intentAch);
                            break;

                    }
                    return false;
                }
            });

            main_BottomAppBar = findViewById(R.id.main_BottomAppBar);

            main_BottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });

            main_BottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch(menuItem.getItemId()) {
                        case R.id.main_BottomAppBar_tasks:
                            // Open tasks
                            Intent intentTasks = new Intent(MainActivity.this, TasksActivity.class);
                            startActivity(intentTasks);
                            break;
                        case R.id.main_BottomAppBar_ach:
                            // Open achievements
                            Intent intentAch = new Intent(MainActivity.this, AchievementsActivity.class);
                            startActivity(intentAch);
                            break;
                    }
                    return true;
                }
            });
        }
    }

    public void onStart() {
        super.onStart();

        // Check if a user is currently signed in, update UI
        // A function to update the UI accordingly, (Logout / Sign in / Register)
        FirebaseUser currentUser = mAuth.getCurrentUser();

        txtV_email = navigationView.getHeaderView(0).findViewById(R.id.nav_email);
        MI_LoginReg = navigationMenu.findItem(R.id.nav_LoginReg);
        profileImage = navigationView.getHeaderView(0).findViewById(R.id.imageView);;
        MI_Profile = navigationMenu.findItem(R.id.nav_profile);

        if(currentUser != null) {
            txtV_email.setText(currentUser.getEmail());

            firestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        txtV_name = findViewById(R.id.nav_name);
                        txtV_name.setText(R.string.placeholder_customName);
                        for (QueryDocumentSnapshot thisDoc : task.getResult()) {
                            // Check if the UID matches logged in users' UID
                            if(thisDoc.getString("uid").equals(mAuth.getCurrentUser().getUid())) {
                                txtV_name.setText(thisDoc.getString("name"));
                            }
                        }
                    }
                }
            });

            //Declares storage reference
            storageReference = FirebaseStorage.getInstance().getReference();

            //Finds Uri and loads profile picture to nav_header
            try {
                StorageReference profileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });

            } catch (Exception e) {
                System.out.println(e);
                System.out.println("User does not have a profile picture");
            }


            MI_LoginReg.setTitle("Logout");
            MI_Profile.setVisible(true);

            // TODO: Bare legg til nye tasks, bruker Clear() for å unnvike dobbel tasks
            mTaskList.clear();

            // Fetch a users tasks, then add them to the local database
            firestore.collection("tasks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for (QueryDocumentSnapshot thisDoc : task.getResult()) {
                            // Check if the UID matches logged in users' UID
                            if(thisDoc.getString("uid").equals(mAuth.getCurrentUser().getUid())) {
                                final Task newTask = new Task(thisDoc.getString("category"), thisDoc.getString("desc"), Long.parseLong(thisDoc.getString("enddate")), "ic_add", thisDoc.getBoolean("completed"));
                                // Testing that the task is not complete,
                                // as well as weeding out unfinished past tasks
                                Calendar cal = Calendar.getInstance();
                                if (newTask.getCompleted() != true && newTask.getDate() > cal.getTimeInMillis()) {
                                    mTaskList.add(newTask);
                                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            mDB.checketDao().insertTask(newTask);
                                        }
                                    });
                                }
                            }
                        }

                        // Call the method to initialize and inflate the recycler
                        recyclerView();
                    }
                }
            });

        } else {
            MI_LoginReg.setTitle("Login / Register");
            MI_Profile.setVisible(false);
        }
    }

    public void recyclerView() {
        // Sort list
        Collections.sort(mTaskList, new Comparator<no.checket.checket.Task>() {
            @Override
            public int compare(final no.checket.checket.Task object1, final no.checket.checket.Task object2) {
                Calendar o1 = Calendar.getInstance();
                Calendar o2 = Calendar.getInstance();

                o1.setTimeInMillis(object1.getDate());
                o2.setTimeInMillis(object2.getDate());

                return o1.compareTo(o2);
            }
        });
        // Get a handle to the RecyclerView.
        mRecyclerView = findViewById(R.id.coming_tasks);
        // Specify the length of the list for this activity
        // This lets us use the same TaskListAdapter class for multiple activities showing different lengths.
        int length;
        if(mTaskList.size() >= 6) {
            length = 6;
        } else {
            length = mTaskList.size();
        }
        // Create an adapter and supply the data to be displayed.
        mAdapter = new TaskListAdapter(this, mTaskList, length);
        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    
    public void newTask(View view) {
        // User has clicked the FAB
        DialogFragment dialog = new NewTaskFragment();
        dialog.show(getSupportFragmentManager(), "newTaskFragment");
    }

    // Listener for clicking of the save button
    // Had to create these from an error dialog when implementing the interface,
    // just to get the overrides right.
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String header, String details, long date, String icon, Boolean completed) {
        Task task = new Task(header, details, date, icon, completed);
        // Add the new task to the list
        int index = 0;
        if (header.equals("")) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_LONG).show();
            // TODO: Reload dialog with any input
            newTask(drawerLayout);
        } else {
            // Make sure the date and time is not already used
            Boolean used = false;
            for (Task t : mTaskList) {
                Log.i("PETTER", String.valueOf(t.getDate()) + ", " + String.valueOf(task.getDate()));
                if (task.getDate() <= t.getDate() + 60000 && task.getDate() >= t.getDate() - 60000) {
                    used = true;
                }
            }
            if (used) {
                Toast.makeText(this, "You already have something planned for that time slot", Toast.LENGTH_LONG).show();
                // TODO: Reload dialog with any input
                newTask(drawerLayout);
            } else {
                mTaskList.add(index, task);
                // TODO: Upload new Task to DB
                // Calling the function to refresh the RecyclerView
                recyclerView();
            }
        }
    }

    // ... or the cancel button
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }



}