package no.checket.checket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileActivity extends AppCompatActivity {
    ImageView callEditprofile;

    private TextView txtV_name;
    private TextView txtV_email;

    // Firebase, declare instance of Firestore and Auth
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.Profile);
        }

        callEditprofile = findViewById(R.id.link_Editprofile);

        callEditprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        //Displaying information for logged in users Profile
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        firestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot thisDoc : task.getResult()) {
                        // Check if the UID matches logged in users' UID
                        if(thisDoc.getString("uid").equals(mAuth.getCurrentUser().getUid())) {
                            //Name
                            txtV_name = findViewById(R.id.profile_name);
                            txtV_name.setText(thisDoc.getString("name"));

                            //Email
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            txtV_email = findViewById(R.id.profile_profileemail);
                            txtV_email.setText(currentUser.getEmail());
                        }
                    }
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