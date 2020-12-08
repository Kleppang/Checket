package no.checket.checket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
                        case R.id.nav_tasks:
                            // Starts the AchievementsActivity
                            Intent intentTasks = new Intent(MainActivity.this, TasksActivity.class);
                            startActivity(intentTasks);
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
        txtV_name = navigationView.getHeaderView(0).findViewById(R.id.nav_name);
        MI_LoginReg = navigationMenu.findItem(R.id.nav_LoginReg);
        profileImage = navigationView.getHeaderView(0).findViewById(R.id.imageView);;
        MI_Profile = navigationMenu.findItem(R.id.nav_profile);

        if(currentUser != null) {
            txtV_email.setText(currentUser.getEmail());

            firestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
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
                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDB.checketDao().insertTask(newTask);
                                    }
                                });
                                // Testing that the task is not complete,
                                // as well as weeding out unfinished tasks in the past
                                Calendar cal = Calendar.getInstance();
                                if (newTask.getCompleted() != true && newTask.getDate() > cal.getTimeInMillis()) {
                                    mTaskList.add(newTask);
                                }
                            }
                        }
                        // Call the method to initialize and inflate the recycler
                        recyclerView();
                    }
                }
            });

        } else {
            // If the user is not logged in, fetch the tasks from the room DB
            MI_LoginReg.setTitle("Login / Register");
            MI_Profile.setVisible(false);

            mTaskList.clear();
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    List<Task> templist = mDB.checketDao().loadAllTasks();
                    Calendar c = Calendar.getInstance();
                    for(Task temptask : templist) {
                        if (!temptask.getCompleted() && temptask.getDate() > c.getTimeInMillis()) {
                            // Eligible tasks
                            mTaskList.add(temptask);
                        }
                    }
                    // Call the method to initialize and inflate the recycler
                    recyclerView();
                }
            });
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

    public void check(View view) {
        // The String date is stored as the checkbox's content description.
        // Admittedly not ideal, and merely a workaround.
        CharSequence cs = view.getContentDescription();
        String s = cs.toString();
        Log.i("Test", s);
        // Extract substrings from TextView to represent a date
        String sDay = s.substring(0,2);
        String sMonth = s.substring(3,5);
        String sYear = s.substring(6,8);
        String sHour = s.substring(9,11);
        String sMinute = s.substring(12);
        // Parse to integers
        int day = Integer.parseInt(sDay);
        // NB! Strange thing where this int is an index (range 0-11), not the actual month, so deduct 1
        int month = Integer.parseInt(sMonth) - 1;
        // NB! the year fetched from the string is only 2 digits
        int year = Integer.parseInt(sYear) + 2000;
        int hour = Integer.parseInt(sHour);
        int minute = Integer.parseInt(sMinute);
        // Build calendar
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, hour, minute);
        long date1 = c.getTimeInMillis();
        // Unreachable index
        int index = -1;
        // Counter to mark corresponding task when comparing
        int i = 0;
        // Compare
        for (final Task t : mTaskList) {
            long date2 = t.getDate();
            if (date1 <= date2 + 60000 && date1 >= date2 - 60000) {
                // Mark for deletion, as items cannot be removed while looping through
                index = i;
                // Update object
                t.setCompleted(true);
                // Update locally
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDB.checketDao().insertTask(t);
                    }
                });
                // Test if the user is connected
                boolean hasConnection = CommonFunctions.isConnected(getApplicationContext());
                // Upload to firebase if the user is logged in
                if (mAuth.getCurrentUser() != null && hasConnection && index > -1) {
                    String header = t.getHeader();
                    boolean completed = t.getCompleted();
                    String details = t.getDetails();
                    Long date = t.getDate();
                    // HashMap for firebase
                    Map<String, Object> taskMap = new HashMap<>();
                    taskMap.put("category", header);
                    taskMap.put("completed", completed);
                    taskMap.put("desc", details);
                    taskMap.put("enddate", String.valueOf(date));
                    taskMap.put("uid", mAuth.getUid());

                    // Update firebase
                    DocumentReference documentReference = firestore.collection("tasks").document(mAuth.getUid()+date);
                    documentReference.set(taskMap);
                }
            }
            i++;
        }
        if (index != -1) {
            mTaskList.remove(index);
            recyclerView();
        }
    }

    // Listener for clicking of the save button
    // Had to create these from an error dialog when implementing the interface,
    // just to get the overrides right.
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String header, String details, long date, String icon, Boolean completed) {
        // Test if the user is connected
        boolean hasConnection = CommonFunctions.isConnected(getApplicationContext());

        final Task task = new Task(header, details, date, icon, completed);
        // Add the new task to the list

        if (header.equals("")) {
            // Inform user of mistake
            Toast.makeText(this, "Please select a category", Toast.LENGTH_LONG).show();
            // TODO: Reload dialog with any input
            newTask(drawerLayout);
        } else {
            // Make sure the date and time is not already used
            Boolean used = false;
            for (Task t : mTaskList) {
                if (task.getDate() <= t.getDate() + 60000 && task.getDate() >= t.getDate() - 60000) {
                    used = true;
                }
            }
            if (used) {
                Toast.makeText(this, "You already have something planned for that time slot", Toast.LENGTH_LONG).show();
                // TODO: Reload dialog with any input
                newTask(drawerLayout);
            } else if (mAuth.getCurrentUser() != null && hasConnection) {
                mTaskList.add(task);
                // HashMap for firebase
                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("category", header);
                taskMap.put("completed", completed);
                taskMap.put("desc", details);
                taskMap.put("enddate", String.valueOf(date));
                taskMap.put("uid", mAuth.getUid());

                // Update firebase and locally
                DocumentReference documentReference = firestore.collection("tasks").document(mAuth.getUid()+date);
                documentReference.set(taskMap);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDB.checketDao().insertTask(task);
                    }
                });
                // Calling the function to refresh the RecyclerView
                recyclerView();
            } else {
                // Upload locally
                mTaskList.add(task);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDB.checketDao().insertTask(task);
                    }
                });
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