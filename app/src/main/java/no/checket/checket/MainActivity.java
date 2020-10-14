package no.checket.checket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.navigation.NavigationView;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    BottomAppBar main_BottomAppBar;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // Recycler view
    private LinkedList<no.checket.checket.Task> mTaskList = new LinkedList<>();
    private RecyclerView mRecyclerView;
    private TaskListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                main_BottomAppBar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // RecyclerView
        // Populate list
        // TODO
        mTaskList.add(new no.checket.checket.Task("Social", "Drinks with colleagues", "11.11.2020", "ic_misc"));
        mTaskList.add(new no.checket.checket.Task("Cleaning", "Vacuuming", "04.14.2020", "ic_add"));
        mTaskList.add(new no.checket.checket.Task("Exercise", "30 minute cardio", "11.11.2020", "ic_add"));
        mTaskList.add(new no.checket.checket.Task("Cleaning", "Vacuuming", "11.21.2020", "ic_add"));
        mTaskList.add(new no.checket.checket.Task("Miscellaneous", "Pick up dad at the airport", "12.12.2020", "ic_misc"));
        mTaskList.add(new no.checket.checket.Task("Cleaning", "Vacuuming", "11.28.2020", "ic_add"));
        mTaskList.add(new no.checket.checket.Task("Sports", "Football in the park", "11.13.2020", "ic_sports"));
        mTaskList.add(new no.checket.checket.Task("Cleaning", "Vacuuming", "11.14.2020", "ic_add"));
        // Sort list
        Collections.sort(mTaskList, new Comparator<no.checket.checket.Task>() {
            @Override
            public int compare(final no.checket.checket.Task object1, final no.checket.checket.Task object2) {
                return object1.getDate().compareTo(object2.getDate());
            }
        });
        // Get a handle to the RecyclerView.
        mRecyclerView = findViewById(R.id.coming_tasks);
        // Create an adapter and supply the data to be displayed.
        mAdapter = new TaskListAdapter(this, mTaskList);
        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.main_loginRegister:
                        Intent myintent = new Intent(MainActivity.this,LoginRegisterActivity.class);
                        startActivity(myintent);
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
                        Toast.makeText(MainActivity.this, "Open tasks", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.main_BottomAppBar_ach:
                        // Open achievements
                        Toast.makeText(MainActivity.this, "Open achievements", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }

    
    public void newTask(View view) {
        // User has clicked the FAB
        Toast.makeText(this, "Create a new task", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}