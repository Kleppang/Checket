package no.checket.checket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore firestore;
    private String userID;



    private static final String TAG = "SignUpActivity";

    EditText eTxt_email;
    EditText eTxt_pw, eTxt_pw2;
    MaterialButton btn_SignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.SignUp);
        }
        
    }

    public void CreateUser(View view) {

        //inputs
        eTxt_email = findViewById(R.id.inp_signup_email);
        eTxt_pw =  findViewById(R.id.inp_signup_pw);
        eTxt_pw2 =  findViewById(R.id.inp_signup_pw2);

        String email = eTxt_email.getText().toString();
        String password = eTxt_pw.getText().toString();
        String password2 = eTxt_pw2.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()) {
            if (password.equals(password2)) {
                btn_SignUp = findViewById(R.id.btn_SignUp);

                btn_SignUp.setEnabled(false);
                btn_SignUp.setText(R.string.Activity_wait);

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signUpWithEmail: success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            finish();
                            // User authenticated, onStart's mAuth.getCurrentUser() will now return this user
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signUpWithEmail: failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Sign up failed.", Toast.LENGTH_SHORT).show();

                            // Clear password
                            eTxt_pw.setText("");
                            eTxt_pw2.setText("");

                        }
                    }
                });
                } else {
                Toast.makeText(getApplicationContext(), "Please makes sure out both password-fields match.", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Please fill out all required fields.", Toast.LENGTH_SHORT).show();
            }
    }//method

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}