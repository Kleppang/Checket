package no.checket.checket;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

public class TasksActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Populate list
        // TODO: pass list from MainActivity
        mTaskList.add(new no.checket.checket.Task("Social", "Drinks with colleagues", new Date(2020, 11, 11, 21, 30), "ic_misc"));
        mTaskList.add(new no.checket.checket.Task("Cleaning", "Vacuuming", new Date (2020, 11, 12, 21, 30), "ic_add"));
        mTaskList.add(new no.checket.checket.Task("Exercise", "30 minute cardio", new Date (2020, 11, 14, 21, 30), "ic_add"));
        mTaskList.add(new no.checket.checket.Task("Cleaning", "Vacuuming", new Date (2020, 11, 19, 21, 30), "ic_add"));
        mTaskList.add(new no.checket.checket.Task("Miscellaneous", "Pick dad up at the airport", new Date (2020, 12, 21, 20, 30), "ic_misc"));
        mTaskList.add(new no.checket.checket.Task("Cleaning", "Vacuuming", new Date (2020, 11, 5, 21, 30), "ic_add"));
        mTaskList.add(new no.checket.checket.Task("Sports", "Football in the park", new Date (2020, 10, 22, 20, 0), "ic_sports"));
        mTaskList.add(new no.checket.checket.Task("Cleaning", "Vacuuming", new Date (2020, 10, 28, 12, 30), "ic_add"));
        mTaskList.add(new no.checket.checket.Task("Cleaning", "Vacuuming", new Date (2020, 10, 28, 13, 30), "ic_add"));
        mTaskList.add(new no.checket.checket.Task("Cleaning", "Vacuuming", new Date (2020, 10, 28, 14, 30), "ic_add"));
        mTaskList.add(new no.checket.checket.Task("Cleaning", "Vacuuming. This is getting psychotic...", new Date (2020, 10, 28, 15, 30), "ic_add"));
        mTaskList.add(new no.checket.checket.Task("Cleaning", "Vacuuming", new Date (2020, 10, 28, 16, 30), "ic_add"));
        mTaskList.add(new no.checket.checket.Task("Cleaning", "Vacuuming", new Date (2020, 10, 28, 17, 30), "ic_add"));
        mTaskList.add(new no.checket.checket.Task("Cleaning", "Vacuuming", new Date (2020, 10, 28, 18, 30), "ic_add"));
        mTaskList.add(new no.checket.checket.Task("Cleaning", "Vacuuming. Apartment's REAAALLY clean now.", new Date (2020, 10, 28, 19, 30), "ic_add"));
        mTaskList.add(new no.checket.checket.Task("Cleaning", "Vacuuming. Here we go again.", new Date (2020, 10, 28, 21, 30), "ic_add"));
        mTaskList.add(new no.checket.checket.Task("Cleaning", "Vacuuming", new Date (2020, 10, 28, 21, 30), "ic_add"));
        // Sort list
        Collections.sort(mTaskList, new Comparator<Task>() {
            @Override
            public int compare(final no.checket.checket.Task object1, final no.checket.checket.Task object2) {
                return object1.getDate().compareTo(object2.getDate());
            }
        });


        // Firebase, initialize the instance
        mAuth = FirebaseAuth.getInstance();

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
                            // TODO
                            // Starts the LoginRegisterActivity, Switch case with putExtra to determine which layout we're showing?
                            Intent intent = new Intent(TasksActivity.this, LoginRegisterActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case R.id.nav_settings:
                        // Starts the SettingsActivity
                        Intent intent = new Intent(TasksActivity.this, SettingsActivity.class);
                        startActivity(intent);
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
                        Toast.makeText(TasksActivity.this, "Open tasks", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.main_BottomAppBar_ach:
                        // Open achievements
                        Toast.makeText(TasksActivity.this, "Open achievements", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
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
        Intent intent = new Intent(TasksActivity.this, NewTaskActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}
