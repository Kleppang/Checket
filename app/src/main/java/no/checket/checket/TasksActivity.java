package no.checket.checket;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TasksActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NewTaskFragment.NewTaskDialogListener {

    private CoordinatorLayout coordinatorLayout;

    // Recycler view
    private LinkedList<Task> mTasksList = new LinkedList<>();
    private LinkedList<Task> mTasksListFinished = new LinkedList<>();
    private RecyclerView mRecyclerViewTasks;
    private RecyclerView mRecyclerViewTasksFinished;
    private TaskListAdapter mAdapterTasks;
    private TaskListAdapter mAdapterTasksFinished;
    private TextView mEmptyListText;

    // Firebase & Auth, declare instance
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;

    private ChecketDatabase mDB;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        // Top action bar with UP
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.tasks_header);
            actionBar.setElevation(0);
        }

        // Firebase, initialize the instance
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mDB = ChecketDatabase.getDatabase(this);

        // Fill mTasksList
        fillTaskList();

        // Get a handle to the tabLayout
        TabLayout tabLayout = findViewById(R.id.tasks_tabLayout);

        // Set listener for tabbing between them
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        mRecyclerViewTasks.setVisibility(View.VISIBLE);
                        mRecyclerViewTasksFinished.setVisibility(View.GONE);
                        break;
                    case 1:
                        mRecyclerViewTasks.setVisibility(View.GONE);
                        mRecyclerViewTasksFinished.setVisibility(View.VISIBLE);
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

    public void onStart() {
        super.onStart();

        //Declares storage reference
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void newTask(View view) {
        // User has clicked the FAB
        DialogFragment dialog = new NewTaskFragment();
        dialog.show(getSupportFragmentManager(), "newTaskFragment");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    public void fillTaskList () {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Task> templist = mDB.checketDao().loadAllTasks();
                for(Task temptask : templist) {
                    if (temptask.getCompleted()) {
                        // Finished tasks
                        mTasksListFinished.add(temptask);
                    } else {
                        // Ongoing tasks
                        mTasksList.add(temptask);
                    }
                }
                // Call the method to initialize and inflate the recycler
                recyclerView();
            }
        });
    }

    public void recyclerView() {
        // Sort list
        Collections.sort(mTasksList, new Comparator<no.checket.checket.Task>() {
            @Override
            public int compare(final no.checket.checket.Task object1, final no.checket.checket.Task object2) {
                Calendar o1 = Calendar.getInstance();
                Calendar o2 = Calendar.getInstance();

                o1.setTimeInMillis(object1.getDate());
                o2.setTimeInMillis(object2.getDate());

                return o1.compareTo(o2);
            }
        });

        Collections.sort(mTasksListFinished, new Comparator<no.checket.checket.Task>() {
            @Override
            public int compare(final no.checket.checket.Task object1, final no.checket.checket.Task object2) {
                Calendar o1 = Calendar.getInstance();
                Calendar o2 = Calendar.getInstance();

                o1.setTimeInMillis(object1.getDate());
                o2.setTimeInMillis(object2.getDate());

                return o1.compareTo(o2);
            }
        });

        // Get a handle to the RecyclerViews...
        mRecyclerViewTasks = findViewById(R.id.tasks);
        mRecyclerViewTasksFinished = findViewById(R.id.tasksFinished);
        // ... As well as the potential message
        mEmptyListText = findViewById(R.id.emptyMainText);
        // Specify the length of the list for this activity
        // This lets us use the same TaskListAdapter class for multiple activities showing different lengths.
        int length = mTasksList.size();
        int lengthFinished = mTasksListFinished.size();
        // Create an adapter and supply the data to be displayed.
        mAdapterTasks = new TaskListAdapter(this, mTasksList, length);
        mAdapterTasksFinished = new TaskListAdapter(this, mTasksListFinished, lengthFinished);
        // Connect the adapter with the RecyclerView.
        mRecyclerViewTasks.setAdapter(mAdapterTasks);
        mRecyclerViewTasksFinished.setAdapter(mAdapterTasksFinished);
        // Add dividing lines to the recyclerView
        mRecyclerViewTasks.addItemDecoration(new DividerItemDecoration(mRecyclerViewTasks.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerViewTasksFinished.addItemDecoration(new DividerItemDecoration(mRecyclerViewTasksFinished.getContext(), DividerItemDecoration.VERTICAL));
        // Give the RecyclerView a default layout manager.
        mRecyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewTasksFinished.setLayoutManager(new LinearLayoutManager(this));
        // If mTaskList is empty, display a helpful message
        if (mTasksList.size() <= 0) {
            mRecyclerViewTasks.setVisibility(View.GONE);
            mEmptyListText.setVisibility(View.VISIBLE);
        } else {
            mRecyclerViewTasks.setVisibility(View.VISIBLE);
            mEmptyListText.setVisibility(View.GONE);
        }
    }

    // Listener for clicking of the save button...
    // Listener for clicking of the save button
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
            newTask(coordinatorLayout);
        } else {
            // Make sure the date and time is not already used
            Boolean used = false;
            for (Task t : mTasksList) {
                if (task.getDate() <= t.getDate() + 60000 && task.getDate() >= t.getDate() - 60000) {
                    used = true;
                }
            }
            if (used) {
                Toast.makeText(this, "You already have something planned for that time slot", Toast.LENGTH_LONG).show();
                // TODO: Reload dialog with any input
                newTask(coordinatorLayout);
            } else if (mAuth.getCurrentUser() != null && hasConnection) {
                mTasksList.add(task);
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
                mTasksList.add(task);
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

    public void check(View view) {
        // The String date is stored as the checkbox's content description.
        // Admittedly not ideal, and merely a workaround.
        CharSequence cs = view.getContentDescription();
        String s = cs.toString();
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
        for (final Task t : mTasksList) {
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
            mTasksList.remove(index);
            recyclerView();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
