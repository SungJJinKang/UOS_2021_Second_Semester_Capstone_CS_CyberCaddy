package com.uoscybercaddy.dabajo.activity;

import static com.uoscybercaddy.dabajo.activity.ImageDirectoryHelper.GetImageDirecotry;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.view.WriteInfo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "WritePostActivity";
    private FirebaseUser user;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_IMAGE_CODE = 301;
    private static final int IMAGE_PICK_GALLERY_VIDEO_CODE = 302;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;


    private LinearLayout parent;

    String cameraPermissions[];
    String storagePermissions[];
    Uri image_url;
    Uri video_url;

    int pathCount = 0;
    int successCount = 0;

    ArrayList<ImageView> uploadedImageList = new ArrayList<ImageView>();



    private int imgCount = 0;
    private void UpdateImage(WriteInfo writeInfo)
    {
        if(uploadedImageList.isEmpty())
        {
            return ;
        }


        writeInfo.imageCount = uploadedImageList.size();
        writeInfo.imageSize = new int[uploadedImageList.size()];
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

            writeInfo.imageSize[i] = data.length;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        parent = findViewById(R.id.contentsLayout);
        findViewById(R.id.postButton).setOnClickListener(onClickListener);
        findViewById(R.id.imageButton).setOnClickListener(onClickListener);
        findViewById(R.id.videoButton).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.postButton:
                    startToast("works");
                    writePost();
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
                ImageView imageView = new ImageView(PostActivity.this);
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
            if(requestCode == IMAGE_PICK_CAMERA_CODE){
                Log.e("이미지 uri",""+image_url);
//                profileImageView.setImageURI(null);
//                profileImageView.setImageURI(image_url);

            }
        }
    }
    private void writePost() {
        final String title = ((EditText) findViewById(R.id.postTitle)).getText().toString();
        final String contents = ((EditText) findViewById(R.id.postBody)).getText().toString();

        if (title.length() > 0 && contents.length() > 0) {



            user = FirebaseAuth.getInstance().getCurrentUser();
            WriteInfo writeInfo = new WriteInfo(title, contents, user.getUid(), new Date());
            UpdateImage(writeInfo);

            uploadPost(writeInfo);

        } else {
            startToast("내용을 입력해주세요.");
        }
    }

    //TODO : 이거 나중에 글 보는 액티비티로 옮겨야한다.
    public Bitmap[] GetImageFromWriteInfo(WriteInfo writeinfo)
    {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

// Create a reference to "mountains.jpg"
        Bitmap[] bitmaps = new Bitmap[writeinfo.imageCount];

        ArrayList<String> imgDirectoryList = ImageDirectoryHelper.GetImageDirecotry(writeinfo);

        for(int i = 0 ; i < writeinfo.imageCount ; i++)
        {
            final StorageReference imageRef = storageRef.child(imgDirectoryList.get(i));

            final int imageSize = writeinfo.imageSize[i];

            Task<byte[]> tasks = imageRef.getBytes(imageSize);

            try {
                com.google.android.gms.tasks.Tasks.await(tasks);

                Bitmap bitmap = BitmapFactory.decodeByteArray( tasks.getResult(), 0, imageSize ) ;
                bitmaps[i] = bitmap;

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return bitmaps;
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
        startActivity(intent);
    } // startactivity 한번에 사용하기

    private void pickFromGalleryImage(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_IMAGE_CODE);
    }

    private void pickFromGalleryVideo(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("video/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_VIDEO_CODE);
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