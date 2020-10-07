package no.checket.checket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class LoginRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);


        Toast.makeText(this, "Du er nå i login/registrer", Toast.LENGTH_LONG).show();
    }
    public void newTask(View view) {
        // User has clicked the FAB
        Toast.makeText(this, "Create a new task", Toast.LENGTH_SHORT).show();
    }
}