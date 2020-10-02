package no.checket.checket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;

public class MainActivity extends AppCompatActivity {

    BottomAppBar main_BottomAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_BottomAppBar = findViewById(R.id.main_BottomAppBar);

        main_BottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Jasså, åpne Navigation Drawer ja", Toast.LENGTH_SHORT).show();
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
}