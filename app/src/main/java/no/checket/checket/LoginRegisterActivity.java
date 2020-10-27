package no.checket.checket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

    //Transition animation
    TextView tran_title, tran_text, CallSignUp;
    TextInputLayout tran_email, tran_password;
    Button tran_button;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        // Firebase, initialize the instance
        mAuth = FirebaseAuth.getInstance();



        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.login);
        }

        //Hooks
        tran_title = findViewById(R.id.logoTitle);
        tran_text = findViewById(R.id.logoText);
        CallSignUp = findViewById(R.id.link_signup);
        tran_email = findViewById(R.id.lgn_email);
        tran_password = findViewById(R.id.lgn_pw);
        tran_button = findViewById(R.id.btn_login);

        CallSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginRegisterActivity.this, SignUpActivity.class);

               /* Pair[] pairs = new Pair[7];

                pairs[0] = new Pair<View,String>(tran_title,"logo_title");
                pairs[1] = new Pair<View,String>(tran_text,"logo_text");
                pairs[2] = new Pair<View,String>(tran_email,"input_email");
                pairs[3] = new Pair<View,String>(tran_password,"input_pw");
                pairs[4] = new Pair<View,String>(tran_button,"button_action");
                pairs[5] = new Pair<View,String>(tran_Btntxt,"btn_text");

                // Check if we're running on Android 5.0 or higher
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Apply activity transition
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginRegisterActivity.this,pairs);
                    startActivity(intent,options.toBundle());
                } else {
                    // Swap without transition
                    startActivity(intent);
                } */
                startActivity(intent);
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
            btn_login = findViewById(R.id.btn_login);

            btn_login.setEnabled(false);
            btn_login.setText(R.string.Activity_wait);

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail: success");
                        //FirebaseUser user = mAuth.getCurrentUser();
                        finish();
                        // User authenticated, onStart's mAuth.getCurrentUser() will now return this user
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail: failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();

                        // Clear password
                        eTxt_password.setText("");

                        btn_login.setEnabled(true);
                        btn_login.setText(R.string.action_sign_in);
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Please fill out both fields.", Toast.LENGTH_SHORT).show();
        }
    }

}