package no.checket.checket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginRegisterActivity extends AppCompatActivity {

    // Firebase, declare instance
    private FirebaseAuth mAuth;

    private static final String TAG = "LoginRegisterActivity";

    EditText eTxt_email;
    EditText eTxt_password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        // Firebase, initialize the instance
        mAuth = FirebaseAuth.getInstance();

        Toast.makeText(this, "Du er nå i login/registrer", Toast.LENGTH_LONG).show();
    }

    public void loginUser(View view) {

        eTxt_email = findViewById(R.id.inp_login_email);
        eTxt_password = findViewById(R.id.inp_login_pw);
        String email = eTxt_email.getText().toString();
        String password = eTxt_password.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail: success");
                    //FirebaseUser user = mAuth.getCurrentUser();

                    // User authenticated, onStart's mAuth.getCurrentUser() will now return this user
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    setResult(RESULT_OK, intent);
                    // Finishes activity
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail: failure", task.getException());
                    Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();

                    // Clear password
                    eTxt_password.setText("");
                }
            }
        });
    }


    public void newTask(View view) {
        // User has clicked the FAB
        Toast.makeText(this, "Create a new task", Toast.LENGTH_SHORT).show();
    }
}