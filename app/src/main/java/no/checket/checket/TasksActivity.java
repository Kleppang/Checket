package no.checket.checket;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class TasksActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NewTaskFragment.NewTaskDialogListener {

    BottomAppBar main_BottomAppBar;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Menu navigationMenu;

    private TextView txtV_email;
    private MenuItem MI_LoginReg;

    // Recycler view
    private LinkedList<Task> mTaskList = new LinkedList<Task>();
    private RecyclerView mRecyclerView;
    private TaskListAdapter mAdapter;

    // Firebase, declare instance
    private FirebaseAuth mAuth;

    private ChecketDatabase mDB;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase, initialize the instance
        mAuth = FirebaseAuth.getInstance();

        mDB = ChecketDatabase.getDatabase(this);

        setContentView(R.layout.activity_tasks);

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
                            new MaterialAlertDialogBuilder(TasksActivity.this).setTitle(R.string.dialog_logout_title).setMessage(R.string.dialog_logout_msg)
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
                            Intent intent = new Intent(TasksActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case R.id.nav_settings:
                        // Starts the SettingsActivity
                        Intent intent = new Intent(TasksActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_profile:
                        //Starts the ProfileActivity
                        Intent intentP = new Intent(TasksActivity.this, ProfileActivity.class);
                        startActivity(intentP);
                        break;

                    case R.id.nav_achievements:
                        // Starts the AchievementsActivity
                        Intent intentAch = new Intent(TasksActivity.this, AchievementsActivity.class);
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
                        // Open Main
                        Intent intentTasks = new Intent(TasksActivity.this, MainActivity.class);
                        startActivity(intentTasks);
                        break;
                    case R.id.main_BottomAppBar_ach:
                        // Open achievements
                        Intent intentAch = new Intent(TasksActivity.this, AchievementsActivity.class);
                        startActivity(intentAch);
                        break;
                }
                return true;
            }
        });

        // Fill mTaskList
        fillTaskList();
    }

    public void onStart() {
        super.onStart();

        // Check if a user is currently signed in, update UI
        // A function to update the UI accordingly, (Logout / Sign in / Register)
        FirebaseUser currentUser = mAuth.getCurrentUser();

        txtV_email = navigationView.getHeaderView(0).findViewById(R.id.nav_email);
        MI_LoginReg = navigationMenu.findItem(R.id.nav_LoginReg);

        if(currentUser != null) {
            txtV_email.setText(currentUser.getEmail());
            MI_LoginReg.setTitle("Logout");
        } else {
            MI_LoginReg.setTitle("Login / Register");
        }
    }

    public void newTask(View view) {
        // User has clicked the FAB
        DialogFragment dialog = new NewTaskFragment();
        dialog.show(getSupportFragmentManager(), "NewTaskFragment");

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    public void fillTaskList () {
        // RecyclerView
        // Populate list
        // TODO: Get list from DB


        // NB! The year, month, etc. constructor is deprecated
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Task> templist = mDB.checketDao().loadAllTasks();
                for(Task temptask : templist) {
                    mTaskList.add(temptask);
                }

                // Call the method to initialize and inflate the recycler
                recyclerView();

            }
        });

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
        mRecyclerView = findViewById(R.id.tasks);
        // Specify the length of the list for this activity
        // This lets us use the same TaskListAdapter class for multiple activities showing different lengths.
        int length = mTaskList.size();
        // Create an adapter and supply the data to be displayed.
        mAdapter = new TaskListAdapter(this, mTaskList, length);
        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Listener for clicking of the save button
    // Had to create these from an error dialog when implementing the interface,
    // just to get the overrides right.
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String header, String details, long date, String icon) {
        Task task = new Task(header, details, date, icon);
        // Add the new task to the list
        int index = 0;
        if (!header.equals("")) {
            mTaskList.add(index, task);
            // TODO: Upload new Task to DB
            Log.i("Petter", header + ", " + details + ", " + icon);
            // Calling the function to refresh the RecyclerView
            recyclerView();
        } else {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_LONG).show();
            // TODO: Unsure whether this is the right view to give
            newTask(drawerLayout);
        }

    }

    // ... or the cancel button
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.i("Petter", "MainActivity.onNegativeDialogClick()");
    }

    // Used for accessing a time picker in the new task dialog
    public void showTimePickerFragment(View view) {
        DialogFragment time = new NewTaskFragment.TimePickerFragment();
        time.show(getSupportFragmentManager(), "timePickerFragment");
    }

    // Same as above, for a date picker
    public void showDatePickerFragment(View view) {
        DialogFragment newFragment = new NewTaskFragment.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}
