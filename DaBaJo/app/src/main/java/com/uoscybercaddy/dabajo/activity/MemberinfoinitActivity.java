package com.uoscybercaddy.dabajo.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    Button infoSubmitButton;
    String sex, tutortuty;
    private FirebaseAuth mAuth;
    private static final String TAG = "MemberinfoinitActivity";
    private static final int RC_SIGN_IN = 100;

    //
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    String cameraPermissions[];
    String storagePermissions[];
    Uri image_url;
    //


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
        infoSubmitButton.setOnClickListener(onClickListener);
        profileImageView.setOnClickListener(onClickListener);

        sexRadiGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_man:
                        sex = "남자";
                        break;
                    case R.id.rb_woman:
                        sex = "여자";
                        break;
                }
            }
        });
        tutortutyRadiGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_tuty:
                        tutortuty = "튜티";
                        break;
                    case R.id.rb_tutor:
                        tutortuty = "튜터";
                        break;
                }
            }
        });
        //findViewById(R.id.gotoSignUpTV).setOnClickListener(onClickListener);
        //
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //


        //

        //기존 데이터 불러오기
        FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if(document.getData().get("photoUrl") != null){
                            Glide.with(MemberinfoinitActivity.this).load(document.getData().get("photoUrl")).centerCrop().override(500).into(profileImageView);
                        }
                        editTextNickName.setText(document.getData().get("nickName").toString());
                        nameEditText.setText(document.getData().get("name").toString());
                        editTextIntroduction.setText(document.getData().get("introduction").toString());
                        sex = document.getData().get("sex").toString();
                        Log.e("남자? : ",""+sex);
                        tutortuty = document.getData().get("tutortuty").toString();
                        Log.e("튜터튜티",""+tutortuty);
                        if(sex.equals("남자")){
                            sexRadiGroup.check(R.id.rb_man);
                        }else{
                            sexRadiGroup.check(R.id.rb_woman);
                        }
                        if(tutortuty.equals("튜터")){
                            tutortutyRadiGroup.check(R.id.rb_tutor);
                        }
                        else{
                            tutortutyRadiGroup.check(R.id.rb_tuty);
                        }
                    } else {
                        Log.d(TAG, "정보를 등록해주세요");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        progressDialog = new ProgressDialog(this);
    }
    //
    private void showImagePicDialog(){
        String options[] = {"사진 촬영", "갤러리"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("골라주세요");
        builder.setItems(options, (dialog, which) ->{
            if(which == 0){
                if(!checkCameraPermission()){
                    requestCameraPermission();
                }
                else{
                    pickFromCamera();
                }
            }else if(which ==1){
                //갤러리
                if(!checkStoragePermission()){
                    requestStoragePermission();
                }
                else{
                    pickFromGallery();
                }
            }
        });
        builder.create().show();
    }
    private boolean checkStoragePermission(){
        boolean result = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermission(){
        boolean result1 = checkSelfPermission(Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result1 && result2;
    }
    private void requestCameraPermission(){
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length > 0 ){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }else{
                        startToast("카메라&저장소를 승인해주세요.");
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if(grantResults.length > 0 ){
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickFromGallery();
                    }else{
                        startToast("저장소를 승인해주세요.");
                    }
                }
                break;
        }

    }
    private void pickFromCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        image_url = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_url);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }
    private void pickFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }
    //

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
        Log.e("resultCode : ",""+resultCode);
        Log.e("RESULT_OK : ",""+ RESULT_OK);
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                image_url = data.getData();
                Log.e("이미지 uri",""+image_url);
                profileImageView.setImageURI(null);
                profileImageView.setImageURI(image_url);

            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE){
                Log.e("이미지 uri",""+image_url);
                profileImageView.setImageURI(null);
                profileImageView.setImageURI(image_url);

            }
        }
    }

/*
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
*/
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
                    showImagePicDialog();
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

            if(image_url == null){ //image_url == null
                MemberInfo memberInfo = new MemberInfo(nickName, name, introduction, sex,  tutortuty);
                uploader(memberInfo);
            }else{
              //  try{
                    //InputStream stream = new FileInputStream(new File(profilePath));
                    //UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    UploadTask uploadTask = mountainImagesRef.putFile(image_url);
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

               // }catch(FileNotFoundException e){
              //      Log.e("로그","에러: "+e.toString());
                //}
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