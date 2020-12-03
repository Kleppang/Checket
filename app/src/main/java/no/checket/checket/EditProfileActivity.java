package no.checket.checket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private CircleImageView profileImage;
    private MaterialButton saveButton;
    private FloatingActionButton profileChangeBtn;
    private EditText inp_name;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String userID;

    protected static final int CAMERA_PICTURE= 10;
    protected static final int GALLERY_PICTURE = 11;
    protected static final int CAMERA_PERMS = 20;

    private Uri imageUri;
    private StorageReference storageReference;

    private static final String TAG = "EDIT_PROFILE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.EditProfile);
        }

        profileImage = findViewById(R.id.editProfile_image);
        saveButton = findViewById(R.id.btnSave);
        profileChangeBtn = findViewById(R.id.fabEditProfil);
        inp_name = findViewById(R.id.inp_customName);


        //Init
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        profileChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMS);
                    }
                }

                MaterialAlertDialogBuilder profilePicDialog = new MaterialAlertDialogBuilder(view.getContext());
                profilePicDialog.setTitle("Custom profile picture");
                profilePicDialog.setMessage("How do you want to select a picture?");
                profilePicDialog.setPositiveButton("Gallery",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent pictureActionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
                            }
                        });
                profilePicDialog.setNegativeButton("Camera",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);

                                Calendar time = Calendar.getInstance();
                                String filename = "profile_" + time.getTimeInMillis() + ".jpg";

                                File pic = new File(getBaseContext().getFilesDir(), filename);

                                Uri uri = FileProvider.getUriForFile(getBaseContext(), "no.checket.checket.provider", pic);

                                imageUri = uri;

                                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                                startActivityForResult(intent, CAMERA_PICTURE);

                            }
                        });
                profilePicDialog.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCustomName();
            }
        });

    }

    private void addCustomName() {
        //adds custom name to current user
        String customName = inp_name.getText().toString();

        if(!customName.isEmpty()) {

            userID = mAuth.getCurrentUser().getUid();
            //
            DocumentReference documentReference = firestore.collection("users").document(userID);

            Map<String, Object> user = new HashMap<>();
            user.put("name", customName);
            user.put("uid", userID);
            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: user profile is created for " + userID);
                    Toast.makeText(EditProfileActivity.this, "Successfully changed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(EditProfileActivity.this, "No new information has been filled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Gets image uri and displays image in imageview
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_PICTURE) {
            if(resultCode == Activity.RESULT_OK) {
                try {
                    Log.d(TAG, imageUri.toString());
                    Bitmap bitmap = handleSamplingAndRotationBitmap(getApplicationContext(), imageUri);
                    uploadBitmapToFirebase(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //uploadUriToFirebase(imageUri);
            }
        } else if(requestCode == GALLERY_PICTURE) {
            if(resultCode == Activity.RESULT_OK) {
                imageUri = data.getData();
                uploadUriToFirebase(imageUri);
            }
        }
    }

    /*
    In order to solve the issue with images being uploaded rotated these functions serve to correct that.
    512x512 resolution
    */
    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage) throws IOException {
        int MAX_HEIGHT = 512;
        int MAX_WIDTH = 512;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImage);
        return img;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private void uploadUriToFirebase(Uri imageUri) {
        //upload image to firebase storage
        final StorageReference fileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Error occurred while uploading image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadBitmapToFirebase(Bitmap bitmap) {
        final StorageReference fileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = fileRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(EditProfileActivity.this, "Error occurred while uploading image", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference = FirebaseStorage.getInstance().getReference();

                StorageReference profileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });

                Toast.makeText(EditProfileActivity.this, "Image successfully uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}