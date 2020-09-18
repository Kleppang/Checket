package no.checket.checket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import no.checket.checket.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private TextView main_txt_title;

    // Declaring an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_txt_title = findViewById(R.id.main_txt_title);

        // Initializing the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            // User is logged in
            //updateUI(currentUser);
            main_txt_title.setText("Currently logged in as:\n" + currentUser.getEmail());
        } else {
            // User is not currently logged in, prompt
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    public void startLogin(View view) {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }
}