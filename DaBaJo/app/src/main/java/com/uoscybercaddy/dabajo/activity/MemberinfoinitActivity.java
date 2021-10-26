package com.uoscybercaddy.dabajo.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uoscybercaddy.dabajo.MemberInfo;
import com.uoscybercaddy.dabajo.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MemberinfoinitActivity extends AppCompatActivity {
    private ImageView profileImageView;
    private String profilePath;
    private FirebaseUser user;
    EditText editTextNickName, nameEditText,editTextIntroduction;
    RadioGroup sexRadiGroup, tutortutyRadiGroup;
    Button infoSubmitButton, takePicture, gotoGallery;
    String sex, tutortuty;
    CardView pictureGallerySelect;
    private FirebaseAuth mAuth;
    private static final String TAG = "MemberinfoinitActivity";
    private static final int RC_SIGN_IN = 100;


    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info_init);
        mAuth = FirebaseAuth.getInstance();
        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("회원 정보 업데이트");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();
        editTextNickName = (EditText)findViewById(R.id.editTextNickName);
        nameEditText = (EditText)findViewById(R.id.nameEditText);
        editTextIntroduction = (EditText)findViewById(R.id.editTextIntroduction);
        sexRadiGroup = (RadioGroup)findViewById(R.id.sexRadiGroup);
        tutortutyRadiGroup = (RadioGroup)findViewById(R.id.tutortutyRadiGroup);
        infoSubmitButton = (Button)findViewById(R.id.infoSubmitButton);
        profileImageView = (ImageView)findViewById(R.id.profileImageView);
        takePicture=(Button)findViewById(R.id.takePicture);
        gotoGallery = (Button)findViewById(R.id.gotoGallery);
        pictureGallerySelect = (CardView)findViewById(R.id.pictureGallerySelect);


        takePicture.setOnClickListener(onClickListener);
        gotoGallery.setOnClickListener(onClickListener);
        infoSubmitButton.setOnClickListener(onClickListener);
        profileImageView.setOnClickListener(onClickListener);

        sexRadiGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_man:
                        sex = "남자";
                    case R.id.rb_woman:
                        sex = "여자";
                }
            }
        });
        tutortutyRadiGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_tuty:
                        tutortuty = "튜티";
                    case R.id.rb_tutor:
                        tutortuty = "튜터";
                }
            }
        });


        //findViewById(R.id.gotoSignUpTV).setOnClickListener(onClickListener);
        progressDialog = new ProgressDialog(this);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 0:
                if(resultCode == Activity.RESULT_OK){
                    profilePath = data.getStringExtra("profilePath");
                    Log.e("로그: ","profilePath: "+profilePath);
                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(profilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                    Bitmap bmRotated = rotateBitmap(bmp, orientation);



                    profileImageView.setImageBitmap(bmRotated);

                }
                break;
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return super.onSupportNavigateUp();
    }
    /*
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }*/
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id== R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null){

        } else{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.infoSubmitButton:
                    profileUpdate();
                    break;
                case R.id.profileImageView:
                    //startActivityShortcut(CameraActivity.class);
                    if(pictureGallerySelect.getVisibility() == View.VISIBLE){
                        pictureGallerySelect.setVisibility(View.GONE);
                    }else{
                        pictureGallerySelect.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.takePicture:
                    startActivityShortcut(CameraActivity.class);
                    break;
                case R.id.gotoGallery:
                    break;
            }
        }
    };


    private void profileUpdate() {
        final String nickName = editTextNickName.getText().toString().trim();
        final String name = nameEditText.getText().toString().trim();
        final String introduction = editTextIntroduction.getText().toString().trim();

        if( name.length()>0 && nickName.length()>0 && introduction.length()>7 ){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();
            // Create a reference to 'images/mountains.jpg'
            user = FirebaseAuth.getInstance().getCurrentUser();

            final StorageReference mountainImagesRef = storageRef.child("users/"+user.getUid()+"/profileImage.jpg");

            if(profilePath == null){
                MemberInfo memberInfo = new MemberInfo(nickName, name, introduction, sex,  tutortuty);
                uploader(memberInfo);
            }else{
                try{
                    InputStream stream = new FileInputStream(new File(profilePath));
                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();

                            }
                            // Continue with the task to get the download URL
                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                MemberInfo memberInfo = new MemberInfo(nickName, name, introduction, sex,  tutortuty,downloadUri.toString());
                                uploader(memberInfo);
                            } else {
                                // Handle failures
                                // ...
                                Log.e("로그","실패");
                                startToast("회원 정보 업로드 실패");
                            }
                        }
                    });

                }catch(FileNotFoundException e){
                    Log.e("로그","에러: "+e.toString());
                }
                finish();
            }
        } else{
            startToast("회원 정보를 입력해주세요...");
        }

    }
    private void uploader(MemberInfo memberInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        startToast("회원정보 등록 성공");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        startToast("회원정보 등록 실패");
                    }
                });
    }


    private void startToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void startActivityShortcut(Class c) {
        Intent intent = new Intent(this, c);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 0);
    } // startactivity 한번에 사용하기
    private void startSignupActivity() {
        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }
}