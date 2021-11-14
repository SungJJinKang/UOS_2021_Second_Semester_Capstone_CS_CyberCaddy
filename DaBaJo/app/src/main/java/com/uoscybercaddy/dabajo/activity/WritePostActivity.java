package com.uoscybercaddy.dabajo.activity;

import static com.uoscybercaddy.dabajo.activity.ImageDirectoryHelper.GetImageDirecotry;
import static com.uoscybercaddy.dabajo.activity.ImageDirectoryHelper.GetVideoDirecotry;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.Write;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.view.WriteInfo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class WritePostActivity extends AppCompatActivity {

    private static final String TAG = "WritePostActivity";
    private FirebaseUser user;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_IMAGE_CODE = 301;
    private static final int IMAGE_PICK_GALLERY_VIDEO_CODE = 302;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    ActionBar actionBar;
    EditText postTitle, postBody;
    ImageButton goBackFeed;

    private LinearLayout parent;

    String cameraPermissions[];
    String storagePermissions[];
    Uri image_url;
    Uri video_url;

    int pathCount = 0;
    int successCount = 0;

    ArrayList<ImageView> uploadedImageList = new ArrayList<ImageView>();
    ArrayList<Uri> uploadedVideoUriList = new ArrayList<Uri>();



    private void UpdateImage(WriteInfo writeInfo)
    {
        if(uploadedImageList.isEmpty())
        {
            return ;
        }


        writeInfo.imageCount = uploadedImageList.size();
        ArrayList<String> imgDirectoryList =  GetImageDirecotry(writeInfo);

        for(int i = 0 ; i < uploadedImageList.size() ; i++)
        {
            ImageView imgView = uploadedImageList.get(i);

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

// Create a reference to "mountains.jpg"
            StorageReference imageRef = storageRef.child(imgDirectoryList.get(i));

            imgView.setDrawingCacheEnabled(true);
            imgView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });
        }
    }

    private String getfiletype(Uri videouri) {
        ContentResolver r = getContentResolver();
        // get the file type ,in this case its mp4
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri));
    }

    private void CompreeVideo()
    {
    }


    private void UpdateVideo(WriteInfo writeInfo)
    {
        if(uploadedVideoUriList.isEmpty())
        {
            return ;
        }


        writeInfo.videoCount = uploadedVideoUriList.size();
        //TODO : 비디오 사이즈 수정 필요.
        //writeInfo.videoSize = new int[uploadedVideoUriList.size()];
        writeInfo.videoExtensions = new ArrayList<String>();

        for(int i = 0 ; i < uploadedVideoUriList.size() ; i++)
        {
            writeInfo.videoExtensions.add(getfiletype(uploadedVideoUriList.get(i)));
        }

        ArrayList<String> videoDirectoryList =  GetVideoDirecotry(writeInfo);

        for(int i = 0 ; i < uploadedVideoUriList.size() ; i++)
        {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

// Create a reference to "mountains.jpg"
            StorageReference videoRef = storageRef.child(videoDirectoryList.get(i));

            UploadTask uploadTask = videoRef.putFile(uploadedVideoUriList.get(i));
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);
        parent = findViewById(R.id.contentsLayout);
        actionBar = getSupportActionBar();
        actionBar.hide();
        postTitle = findViewById(R.id.postTitle);
        postBody = findViewById(R.id.postBody);
        goBackFeed = findViewById(R.id.goBackButton);
        findViewById(R.id.postButton).setOnClickListener(onClickListener);
        findViewById(R.id.imageButton).setOnClickListener(onClickListener);
        findViewById(R.id.videoButton).setOnClickListener(onClickListener);

        Intent intent = getIntent();
        if(intent.hasExtra("튜티")) {
            goBackFeed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(WritePostActivity.this, FeedActivity.class);
                    intent.putExtra("튜티", 1);
                    startActivity(intent);
                }
            });
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.postButton:
                    startToast("works");
                    writePost();
                    startActivityShortcut(FeedActivity.class);
                    break;
                case R.id.imageButton:
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else{
                        pickFromGalleryImage();
                    }
                    break;
                case R.id.videoButton:
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else{
                        pickFromGalleryVideo();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("resultCode : ",""+resultCode);
        Log.e("RESULT_OK : ",""+ RESULT_OK);
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_IMAGE_CODE){
                image_url = data.getData();
                String imagePath = data.getStringExtra("imagePath");
                Log.e("이미지 uri",""+image_url);

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ImageView imageView = new ImageView(WritePostActivity.this);
                imageView.setLayoutParams(layoutParams);
                Glide.with(this).load(image_url).override(1000).into(imageView);
                parent.addView(imageView);
                uploadedImageList.add(imageView);
                // 사진 하나하나 설명 가능하게 editText 생성
//                EditText editText = new EditText(PostActivity.this);
//                editText.setLayoutParams(layoutParams);
//                editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
//                parent.addView(editText);

            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                Log.e("이미지 uri",""+image_url);
//                profileImageView.setImageURI(null);
//                profileImageView.setImageURI(image_url);

            }
            else if(requestCode == IMAGE_PICK_GALLERY_VIDEO_CODE)
            {
                Uri selecteVideoUri = data.getData();
                if(selecteVideoUri != null)
                {
                    uploadedVideoUriList.add(selecteVideoUri);
                }
            }
        }
    }
    private void writePost() {
        final String title = ((EditText) findViewById(R.id.postTitle)).getText().toString();
        final String contents = ((EditText) findViewById(R.id.postBody)).getText().toString();
        postTitle.setFocusable(true);
        postBody.setFocusable(true);

        if (title.length() > 0 && contents.length() > 0) {

            user = FirebaseAuth.getInstance().getCurrentUser();
            WriteInfo writeInfo = new WriteInfo(title, contents, user.getUid(), new Date());
            UpdateImage(writeInfo);
            UpdateVideo(writeInfo);

            uploadPost(writeInfo);

        } else {
            startToast("내용을 입력해주세요.");
        }
    }



    private void uploadPost(WriteInfo writeInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("posts").add(writeInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startActivityShortcut(Class c) {
        Intent intent = new Intent(this, c);
        intent.putExtra("튜티", 1);
        startActivity(intent);
    } // startactivity 한번에 사용하기

    private void pickFromGalleryImage(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_IMAGE_CODE);
    }

    private void pickFromGalleryVideo(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_PICK_GALLERY_VIDEO_CODE);
    }

    private String gteVideoPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
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


}