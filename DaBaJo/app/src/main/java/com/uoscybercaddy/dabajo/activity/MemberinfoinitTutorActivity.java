package com.uoscybercaddy.dabajo.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.fragment.CategoryFragmentTutor;
import com.uoscybercaddy.dabajo.models.MemberInfoTutor;

public class MemberinfoinitTutorActivity extends AppCompatActivity {
    private ImageView profileImageView;
    private String profilePath, downloadUrl=null;
    private FirebaseUser user;
    EditText editTextNickName, editTextField, nameEditText, editTextIntroduction;
    RadioGroup sexRadiGroup;
    RadioButton rb_woman;
    Button infoSubmitButton;
    String sex="";
    private FirebaseAuth mAuth;
    private static final String TAG = "MemberinfoinitTutorActivity";
    private static final int RC_SIGN_IN = 100;
    //
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    String fromProfile = null;
    String cameraPermissions[];
    String storagePermissions[];
    Uri image_url;
    //


    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info_init_tutor);
        mAuth = FirebaseAuth.getInstance();
        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        firebaseAuth = FirebaseAuth.getInstance();
        editTextNickName = (EditText)findViewById(R.id.editTextNickName);
        nameEditText = (EditText)findViewById(R.id.nameEditText);
        editTextIntroduction = (EditText)findViewById(R.id.editTextIntroduction);
        editTextField = (EditText)findViewById(R.id.editTextField);
        sexRadiGroup = (RadioGroup)findViewById(R.id.sexRadiGroup);
        infoSubmitButton = (Button)findViewById(R.id.infoSubmitButton);
        profileImageView = (ImageView)findViewById(R.id.profileImageView);
        infoSubmitButton.setOnClickListener(onClickListener);
        profileImageView.setOnClickListener(onClickListener);
        rb_woman = (RadioButton)findViewById(R.id.rb_woman);

        sexRadiGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_man:
                        sex = "??????";
                        break;
                    case R.id.rb_woman:
                        sex = "??????";
                        break;
                }
            }
        });

        Intent intent = getIntent();
        if(intent.hasExtra("fromProfileEdit")){
            fromProfile = "fromProfileEdit";
        }
        //findViewById(R.id.gotoSignUpTV).setOnClickListener(onClickListener);
        //
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //


        //

        //?????? ????????? ????????????
        FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users_tutor").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if(document.getData().get("photoUrl") != null){
                            Glide.with(MemberinfoinitTutorActivity.this).load(document.getData().get("photoUrl")).centerCrop().override(500).into(profileImageView);
                            downloadUrl = (document.getData().get("photoUrl").toString());
                        }
                        editTextNickName.setText(document.getData().get("nickName").toString());
                        nameEditText.setText(document.getData().get("name").toString());
                        editTextField.setText(document.getData().get("field").toString());
                        editTextIntroduction.setText(document.getData().get("introduction").toString());
                        sex = document.getData().get("sex").toString();
                        Log.e("??????? : ",""+sex);

                        if(sex.equals("??????")){
                            sexRadiGroup.check(R.id.rb_man);
                        }else{
                            sexRadiGroup.check(R.id.rb_woman);
                        }

                    } else {
                        Log.d(TAG, "????????? ??????????????????");
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
        String options[] = {"?????? ??????", "?????????"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("???????????????");
        builder.setItems(options, (dialog, which) ->{
            if(which == 0){
                if(!checkCameraPermission()){
                    requestCameraPermission();
                    Log.e("???????????? ????????? ????????? ","???????????? ????????? ?????????");
                }
                else{
                    pickFromCamera();
                    Log.e("pickFromCamera","pickFromCamera");
                }
            }else if(which ==1){
                //?????????
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
        try{
            requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
        }catch(Exception e){
            Log.e("??????",""+e.getMessage());
        }

    }
    private boolean checkCameraPermission(){
        boolean result1 = checkSelfPermission(Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result1 && result2;
    }
    private void requestCameraPermission(){
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }
    private void showDialogToGetPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("?????? ??????")
                .setMessage("????????? ???????????????. " +
                        "???????????? ?????? ??????????????????.");

        builder.setTitle("?????? ??????")
                .setMessage("????????? ???????????????. " +
                        "???????????? ?????? ??????????????????.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        builder.create().show();

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
                        if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) && shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                            startToast("?????????&???????????? ??????????????????.");
                            Log.e("", "User declined, but i can still ask for more");

                        }else{
                            startToast("?????????&???????????? ??????????????????.");
                            Log.e("", "User declined and i can't ask");
                            showDialogToGetPermission();
                        }


                    }
                }
                else{
                    if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) && shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        startToast("?????????&???????????? ??????????????????.");
                        Log.e("", "User declined, but i can still ask for more");

                    }else{
                        startToast("?????????&???????????? ??????????????????.");
                        Log.e("", "User declined and i can't ask");
                        showDialogToGetPermission();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if(grantResults.length > 0 ){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickFromGallery();
                    }else{
                        if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                            startToast("???????????? ??????????????????.");
                            Log.e("", "User declined, but i can still ask for more");

                        }else{
                            startToast("???????????? ??????????????????.");
                            Log.e("", "User declined and i can't ask");
                            showDialogToGetPermission();
                        }

                    }
                }else{
                    if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        startToast("???????????? ??????????????????.");
                        Log.e("", "User declined, but i can still ask for more");

                    }else{
                        startToast("???????????? ??????????????????.");
                        Log.e("", "User declined and i can't ask");
                        showDialogToGetPermission();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("resultCode : ",""+resultCode);
        Log.e("RESULT_OK : ",""+ RESULT_OK);
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                image_url = data.getData();
                Log.e("????????? uri",""+image_url);
                profileImageView.setImageURI(null);
                profileImageView.setImageURI(image_url);

            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE){
                Log.e("????????? uri",""+image_url);
                profileImageView.setImageURI(null);
                profileImageView.setImageURI(image_url);

            }
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
                    showImagePicDialog();
                    break;
            }
        }
    };


    private void profileUpdate() {
        final String nickName = editTextNickName.getText().toString().trim();
        final String field = editTextField.getText().toString().trim();
        final String name = nameEditText.getText().toString().trim();
        final String introduction = editTextIntroduction.getText().toString().trim();
        progressDialog.setMessage("?????? ???????????????");
        progressDialog.show();
        rb_woman.setError(null);
        if(nickName.length()<1){
            editTextNickName.setError("???????????? ??????????????????.");
            editTextNickName.setFocusable(true);
        }
        else if(field.length()<1){
            editTextField.setError("????????? ??????????????????.");
            editTextField.setFocusable(true);

        }
        else if(name.length()<1){
            nameEditText.setError("????????? ??????????????????.");
            nameEditText.setFocusable(true);

        }
        else if(introduction.length()<1){
            editTextIntroduction.setError("????????? ??????????????????.");
            editTextIntroduction.setFocusable(true);
        }
        else if(sex.length()<1){
            rb_woman.setError("????????? ???????????????");
        }
        else{
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();
            // Create a reference to 'images/mountains.jpg'
            user = FirebaseAuth.getInstance().getCurrentUser();

            final StorageReference mountainImagesRef = storageRef.child("users_tutor/"+user.getUid()+"/profileImage.jpg");

            if(image_url == null && downloadUrl==null){ //image_url == null
                MemberInfoTutor memberInfo = new MemberInfoTutor(nickName, name, field, introduction, sex,null);
                uploader(memberInfo);
            }else{
                //  try{
                //InputStream stream = new FileInputStream(new File(profilePath));
                //UploadTask uploadTask = mountainImagesRef.putStream(stream);
                if(image_url != null){
                    UploadTask uploadTask = mountainImagesRef.putFile(image_url);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                progressDialog.dismiss();
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

                                MemberInfoTutor memberInfo = new MemberInfoTutor(nickName, name, field, introduction, sex, downloadUri.toString());
                                uploader(memberInfo);
                            } else {
                                // Handle failures
                                // ...
                                progressDialog.dismiss();
                                Log.e("??????","??????");
                                startToast("?????? ?????? ????????? ??????");
                            }
                        }
                    });
                }else{
                    MemberInfoTutor memberInfo = new MemberInfoTutor(nickName, name, field, introduction, sex, downloadUrl);
                    uploader(memberInfo);
                }

            }

        }
        progressDialog.dismiss();
    }
    private void uploader(MemberInfoTutor memberInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users_tutor").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        startToast("???????????? ?????? ??????");
                        progressDialog.dismiss();
                        Intent intent = new Intent(MemberinfoinitTutorActivity.this, DashboardActivityTutor.class);
                        if(fromProfile != null){
                            intent.putExtra("fromProfileEdit","fromProfileEdit");
                        }
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        progressDialog.dismiss();
                        startToast("???????????? ?????? ??????");

                    }
                });
    }


    private void startToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

}
