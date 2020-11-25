package no.checket.checket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    ImageView callEditprofile;

    private CircleImageView profileImageView;
    private DatabaseReference databaseReference;


    private TextView txtV_name, txtV_email, taskNr, achNr;

    // Firebase, declare instance of Firestore and Auth
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private Uri imageUri;
    private StorageTask uploadTask;
    private StorageReference storageReference;


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
        profileImageView = findViewById(R.id.pb_pic);
        taskNr = findViewById(R.id.taskCount);
        achNr = findViewById(R.id.achCount);

        //Init
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImageView);
            }
        });


        callEditprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });


        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");

        //Displaying information for logged in users Profile
        //Email
        FirebaseUser currentUser = mAuth.getCurrentUser();
        txtV_email = findViewById(R.id.profile_profileemail);
        txtV_email.setText(currentUser.getEmail());

        firestore.collection("tasks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                  if (task.isSuccessful()) {
                      int Tcount = 0;

                      for (QueryDocumentSnapshot thisDoc : task.getResult()) {
                          // Check if the UID matches logged in users' UID
                          if (thisDoc.getString("uid").equals(mAuth.getCurrentUser().getUid())) {
                              Tcount++;

                          }
                      }
                      String finalTcount = String.valueOf(Tcount);
                      taskNr.setText(finalTcount);
                  }
              }
          });

        firestore.collection("achievements").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int Acount = 0;

                    for (QueryDocumentSnapshot thisDoc : task.getResult()) {
                        // Check if the UID matches logged in users' UID
                        if (thisDoc.getString("uid").equals(mAuth.getCurrentUser().getUid())) {
                            Acount++;

                        }
                    }
                    String finalAcount = String.valueOf(Acount);
                    achNr.setText(finalAcount);
                }
            }
        });


        firestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot thisDoc : task.getResult()) {
                        // Check if the UID matches logged in users' UID
                        if (thisDoc.getString("uid").equals(mAuth.getCurrentUser().getUid())) {
                            txtV_name = findViewById(R.id.profile_name);
                            txtV_name.setText(thisDoc.getString("name"));

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