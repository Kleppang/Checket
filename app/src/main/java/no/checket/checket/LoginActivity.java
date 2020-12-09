package no.checket.checket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    // Firebase, declare instance
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    private ChecketDatabase mDB;

    private static final String TAG = "LoginRegisterActivity";

    EditText eTxt_email;
    EditText eTxt_password;
    MaterialButton btn_login;

    //Transition animation
    TextView CallSignUp;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase, initialize the instance
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        mDB = ChecketDatabase.getDatabase(this);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.login);
        }

        //Hooks
        CallSignUp = findViewById(R.id.link_signup);

        CallSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void loginUser(View view) {

        eTxt_email = findViewById(R.id.inp_login_email);
        eTxt_password = findViewById(R.id.inp_login_pw);

        String email = eTxt_email.getText().toString();
        String password = eTxt_password.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()) {
            if(CommonFunctions.isConnected(this)) {
                btn_login = findViewById(R.id.btn_login);

                btn_login.setEnabled(false);
                btn_login.setText(R.string.Activity_wait);

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();

                            if(user != null) {

                                if(user.isEmailVerified()) {
                                    // Fetches all achievements stored on Firestore
                                    firestore.collection("achievements").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()) {
                                                for(QueryDocumentSnapshot document : task.getResult()) {
                                                    if(document.getString("uid").equals(mAuth.getCurrentUser().getUid())) {
                                                        final Achievement tempach = new Achievement(document.getString("name"), document.getString("desc"), document.getString("category"));
                                                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                mDB.checketDao().insertAchievement(tempach);
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    // Upload all local tasks to firestore
                                    List<no.checket.checket.Task> taskList = mDB.checketDao().loadAllTasks();
                                    for (no.checket.checket.Task t : taskList) {
                                        // HashMap for firebase
                                        Map<String, Object> taskMap = new HashMap<>();
                                        taskMap.put("category", t.getHeader());
                                        taskMap.put("completed", t.getCompleted());
                                        taskMap.put("desc", t.getDetails());
                                        taskMap.put("enddate", String.valueOf(t.getDate()));
                                        taskMap.put("uid", mAuth.getUid());

                                        // Update firebase
                                        DocumentReference documentReference = firestore.collection("tasks").document(mAuth.getUid()+ t.getDate());
                                        documentReference.set(taskMap);
                                    }

                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail: success");
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    finish();
                                    // User authenticated, onStart's mAuth.getCurrentUser() will now return this user
                                } else {
                                    Toast.makeText(getApplicationContext(), "Your email is not verified, another email has been sent.", Toast.LENGTH_LONG).show();

                                    user.sendEmailVerification();

                                    mAuth.signOut();

                                    btn_login.setEnabled(true);
                                    btn_login.setText(R.string.action_sign_in);
                                }

                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail: failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Could not login, please try again.", Toast.LENGTH_SHORT).show();

                            // Clear password
                            eTxt_password.setText("");

                            btn_login.setEnabled(true);
                            btn_login.setText(R.string.action_sign_in);
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "You're not currently connected to the Internet, please try again later.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please fill out both fields.", Toast.LENGTH_SHORT).show();
        }
    }

}