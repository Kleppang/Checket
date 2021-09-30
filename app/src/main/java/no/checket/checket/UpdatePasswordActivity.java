package no.checket.checket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePasswordActivity extends AppCompatActivity {

    private MaterialButton btn_update;
    private TextInputEditText txt_password;
    private TextInputEditText txt_password2;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.UpdatePassword);
        }

        btn_update = findViewById(R.id.btnUpdate);
        txt_password = findViewById(R.id.inp_password);
        txt_password2 = findViewById(R.id.inp_password2);

        mAuth = FirebaseAuth.getInstance();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txt_password.getText() != null && txt_password2.getText() != null) {
                    if(txt_password.getText().toString().equals(txt_password2.getText().toString())) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null) {
                            try {
                                user.updatePassword(txt_password.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(UpdatePasswordActivity.this, "Successfully updated your password.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UpdatePasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch(Exception e) {
                                Toast.makeText(UpdatePasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(UpdatePasswordActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UpdatePasswordActivity.this, "Please fill in both fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}