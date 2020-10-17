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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginRegisterActivity extends AppCompatActivity {

    // Firebase, declare instance
    private FirebaseAuth mAuth;

    private static final String TAG = "LoginRegisterActivity";

    EditText eTxt_email;
    EditText eTxt_password;
    MaterialButton btn_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        // Firebase, initialize the instance
        mAuth = FirebaseAuth.getInstance();
    }

    public void loginUser(View view) {

        eTxt_email = findViewById(R.id.inp_login_email);
        eTxt_password = findViewById(R.id.inp_login_pw);

        String email = eTxt_email.getText().toString();
        String password = eTxt_password.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()) {
            btn_login = findViewById(R.id.btn_login);

            btn_login.setClickable(false);
            btn_login.setText(R.string.LoginRegisterActivity_wait);
            btn_login.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_login.setTextColor(getResources().getColor(R.color.colorDarkGray));

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail: success");
                        //FirebaseUser user = mAuth.getCurrentUser();

                        // User authenticated, onStart's mAuth.getCurrentUser() will now return this user
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail: failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();

                        // Clear password
                        eTxt_password.setText("");

                        btn_login.setClickable(true);
                        btn_login.setText(R.string.action_sign_in);

                        btn_login.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        btn_login.setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Please fill out both fields.", Toast.LENGTH_SHORT).show();
        }
    }


    public void newTask(View view) {
        // User has clicked the FAB
        Toast.makeText(this, "Create a new task", Toast.LENGTH_SHORT).show();
    }
}