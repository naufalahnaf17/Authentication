package app.android.loginactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class SetProfile extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    StorageTask uploadTask;
    ImageView image;
    EditText eTxtNama;
    Button btnFinish , btnLogout;
    ProgressBar loading;
    String profileUrl;
    TextView emailVerify;

    Uri uriProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        image = (ImageView) findViewById(R.id.imageView);
        eTxtNama = (EditText) findViewById(R.id.txtNama);
        btnFinish = (Button) findViewById(R.id.btnFinish);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        loading = (ProgressBar) findViewById(R.id.loadingImage);
        emailVerify = (TextView) findViewById(R.id.emailVerify);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("images");

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihGambar();
            }
        });
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(SetProfile.this , LoginActivity.class));
            }
        });

        loadUserInformation();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this , LoginActivity.class));
        }

        final FirebaseUser user = mAuth.getCurrentUser();

        if (user.isEmailVerified()){
            emailVerify.setText("Email Sudah Terverifikasi");
        }else {
            emailVerify.setText("Email Belum Terverifikasi (Klik Disini Untuk Verifikasi)");
            emailVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(SetProfile.this, "Periksa Email Anda", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

    }

    private void loadUserInformation() {

        final FirebaseUser user = mAuth.getCurrentUser();

        if (user.getPhotoUrl()!=null) {
            Glide.with(this )
                    .load(user.getPhotoUrl())
                    .into(image);
        }

        if (user.getDisplayName()!=null) {
            eTxtNama.setText(user.getDisplayName());
        }


    }

    private void saveUserInformation() {
        String displayName = eTxtNama.getText().toString().trim();

        if (displayName.isEmpty()){
            eTxtNama.setError("Nama Tidak Boleh Kosong");
            eTxtNama.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && profileUrl != null){
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileUrl))
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SetProfile.this, "Berhasil Menyimpan Data Profile", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void pilihGambar() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent , CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data!= null && data.getData()!= null){

            uriProfileImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver() , uriProfileImage);
                image.setImageBitmap(bitmap);

                uploadImage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void uploadImage() {
        if (uriProfileImage!= null){

            final StorageReference storageReference =
                    mStorageRef.child(System.currentTimeMillis()+".jpg");

            loading.setVisibility(View.VISIBLE);
            uploadTask = storageReference.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            loading.setVisibility(View.GONE);

                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    profileUrl = url;
                                }
                            });
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }
    }
}
