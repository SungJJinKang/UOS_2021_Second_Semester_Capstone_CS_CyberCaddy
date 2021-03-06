package com.uoscybercaddy.dabajo.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.uoscybercaddy.dabajo.R;
import com.uoscybercaddy.dabajo.adapter.AdapterPosts;
import com.uoscybercaddy.dabajo.adapter.SliderAdapter;
import com.uoscybercaddy.dabajo.adapter.SliderAdapterforFeed;
import com.uoscybercaddy.dabajo.models.ModelPost;
import com.uoscybercaddy.dabajo.models.URLS;
import com.uoscybercaddy.dabajo.models.UsersCategoriesCount;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    EditText titleEt, descriptionEt;
    //ImageView imageIv;
    SliderAdapterforFeed sliderAdapterforFeed;
    AppCompatButton uploadBtn;
    AppCompatButton imageButton;
    AppCompatButton videoButton;
    private LinearLayout layoutIndicators;
    SliderAdapter sliderAdapter;
    LinearLayout imageuploadBtnLayer;
    private ViewPager2 viewPager2;
    List<List> uris = new ArrayList<>();
    List<URLS> uploadUrls = new ArrayList<>();
    private static final String TAG = "AddPostActivity";
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    private static final int VIDEO_PICK_GALLERY_CODE = 500;
    private static final int VIDEO_PICK_CAMERA_CODE = 600;

    String[] cameraPermissions;
    boolean isVideo = false;
    String[] storagePermissions;

    Uri image_rui = null;
    private Uri videoUri= null;
    ImageButton goBackButton;
    TextView pCategoryEt;

    String editTitle, editDescription;
    List<URLS> editImage = new ArrayList<>();

    //????????????
    String name, email, uid, dp;
    Intent intent;
    ProgressDialog pd;
    String category;
    ActionBar actionBar;
    String tutortuty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();
        viewPager2 = findViewById(R.id.viewPagerImageSlider);
        layoutIndicators = findViewById(R.id.layoutIndicators);
        titleEt = (EditText) findViewById(R.id.pTitleEt);
        descriptionEt = (EditText) findViewById(R.id.pDescriptionEt);
        //imageIv = (ImageView) findViewById(R.id.pImageIv);
        uploadBtn = (AppCompatButton)findViewById(R.id.pUploadBtn);
        imageButton = (AppCompatButton)findViewById(R.id.imageButton);
        videoButton = (AppCompatButton)findViewById(R.id.videoButton);
        imageuploadBtnLayer = (LinearLayout)findViewById(R.id.imageuploadBtnLayer);
        isVideo = false;
        goBackButton = (ImageButton)findViewById(R.id.goBackButton);
        pCategoryEt = (TextView) findViewById(R.id.pCategoryEt);
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        pd = new ProgressDialog(this);
        intent = getIntent();
        String isUpdateKey = ""+intent.getStringExtra("key");
        String editPostId = ""+intent.getStringExtra("editPostId");
        String editCategory = ""+intent.getStringExtra("pCategory");
        String editTutortuty = ""+intent.getStringExtra("pTutortuty");
        if(intent.hasExtra("category")){
            category = intent.getStringExtra("category");
        }else{
            category = "??????";
        }
        if(isUpdateKey.equals("editPost")){
            pCategoryEt.setText(category +" (????????? ??????)");
            imageuploadBtnLayer.setVisibility(View.GONE);
            uploadBtn.setText("??????");
            loadPostData(editPostId, editCategory, editTutortuty);
        }
        else{
            imageuploadBtnLayer.setVisibility(View.VISIBLE);
            pCategoryEt.setText(category);
            uploadBtn.setText("?????????");
        }
        actionBar = getSupportActionBar();
        actionBar.hide();

  /*      viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1-Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);

            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);*/
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVideoPickDialog();
            }
        });

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPostActivity.super.onBackPressed();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String title = titleEt.getText().toString().trim();
                String description = descriptionEt.getText().toString().trim();
                if(title.length()<1){
                    titleEt.setError("????????? ??????????????????");
                    titleEt.setFocusable(true);
                }
                else if(description.length()<1){
                    descriptionEt.setError("????????? ??????????????????.");
                    descriptionEt.setFocusable(true);
                }
                else if(isUpdateKey.equals("editPost")){
                    beginUpdate(title, description, editPostId, editCategory, editTutortuty);
                }
                else{
                    //?????????
                    uploadData(title, description);
                }
            }
        });
        FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData().get("tutortuty"));
                        Object position = document.getData().get("tutortuty");
                        if(position != null){
                            tutortuty = position.toString();
                        }else{
                            tutortuty = "??????";
                        }
                        Log.e("???????????? : ",tutortuty);
                        if(document.getData().get("photoUrl") != null){
                            dp = (document.getData().get("photoUrl").toString());
                        }else{
                            dp = null;
                        }
                        //downloadUrl = (document.getData().get("photoUrl").toString());
                        name = document.getData().get("nickName").toString();
                    } else {
                        Log.d(TAG, "????????????");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void prepareNotification(String pId, String title, String description, String notificationType, String notificationTopic){
        String NOTIFICATION_TOPIC = "/topics/" + notificationTopic;
        String NOTIFICATION_TITLE  = title;
        String NOTIFICATION_MESSAGE = description;
        String NOTIFICATION_TYPE = notificationType;

        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("sender", uid);
            notificationBodyJo.put("pId", pId);
            notificationBodyJo.put("pTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("pDescription", NOTIFICATION_MESSAGE);
            notificationBodyJo.put("pCategory", category);
            notificationBodyJo.put("pTutortuty", tutortuty);


            notificationJo.put("to", NOTIFICATION_TOPIC);

            notificationJo.put("data", notificationBodyJo);
        } catch (JSONException e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        sendPostNotification(notificationJo);
    }

    private void sendPostNotification(JSONObject notificationJo) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("FCM_RESPONSE", "onResponse: "+response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddPostActivity.this, ""+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=AAAA9RV8rQM:APA91bHAUdhZt04zg97Y1I5yzAG3Pq5x7qEhCb8WS0saCHGDlaP8SgjixsE_PvRX8EmEIyEPV0mIdsoQsQj_U29F3yyN1cWveuCslPKW2-zr0lRHVNnP2ZQv_S5RopltAV6r-5xSO72R");

                return headers;
            }
        };
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void beginUpdate(String title, String description, String editPostId, String editCategory, String editTutortuty) {
        pd.setMessage("???????????? ???...");
        pd.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();
        DocumentReference washingtonRef = db.collection("Posts").document(editTutortuty).collection(editCategory).document(editPostId);
        washingtonRef.update("pTitle", title)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        washingtonRef.update("pDescr", description)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pd.dismiss();
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                        startToast("???????????? ??????");
                                        Intent intent1 = new Intent("deletePost");
                                        LocalBroadcastManager.getInstance(AddPostActivity.this).sendBroadcast(intent1);
                                        onBackPressed();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Log.w(TAG, "Error updating document", e);
                    }
                });





    }

    private void loadPostData(String editPostId, String editCategory, String editTutortuty) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Posts").document(editTutortuty).collection(editCategory).document(editPostId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        ModelPost modelPost = document.toObject(ModelPost.class);
                        int arrayCount = modelPost.getArrayCount();
                        editTitle = modelPost.getpTitle();
                        editDescription = modelPost.getpDescr();
                        editImage = modelPost.getpImage();

                        titleEt.setText(editTitle);
                        descriptionEt.setText(editDescription);
                        viewPager2.setVisibility(View.VISIBLE);

                        sliderAdapterforFeed  = (new SliderAdapterforFeed(AddPostActivity.this, editImage, viewPager2));
                        viewPager2.setAdapter(sliderAdapterforFeed);
                        ImageView[] indicators;
                        indicators = setupIndicators(arrayCount);
                        for(int i = 0; i< arrayCount; i++) {

                            layoutIndicators.addView(indicators[i]);
                        }
                        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                            @Override
                            public void onPageSelected(int position) {
                                super.onPageSelected(position);
                                int childCount = layoutIndicators.getChildCount();
                                for (int i =0; i< childCount ; i++){
                                    ImageView imageView = (ImageView) layoutIndicators.getChildAt(i);
                                    if(i==position){
                                        imageView.setImageDrawable(
                                                ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active)
                                        );
                                    }else{
                                        imageView.setImageDrawable(
                                                ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_inactive)
                                        );
                                    }
                                }
                            }
                        });

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    private ImageView[] setupIndicators(int arrayCount){
        ImageView[] indicators = new ImageView[arrayCount];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        for(int i = 0 ; i< arrayCount; i++){
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
        }
        return indicators;
    }
    private void setupIndicators(){
        //ImageView indicators = new ImageView;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        ImageView indicators = new ImageView(getApplicationContext());
        indicators.setImageDrawable(ContextCompat.getDrawable(
                getApplicationContext(),
                R.drawable.indicator_inactive
        ));
        indicators.setLayoutParams(layoutParams);
        layoutIndicators.addView(indicators);
    }
    private void setCurrentIndicator(int index){
        int childCount = layoutIndicators.getChildCount();
        for (int i =0; i< childCount ; i++){
            ImageView imageView = (ImageView) layoutIndicators.getChildAt(i);
            if(i==index){
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_active)
                );
            }else{
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.indicator_inactive)
                );
            }
        }
    }
    private void showVideoPickDialog() {
        String options[] = {"????????? ??????","????????? ?????????"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("???????????????");
        builder.setItems(options, (dialog, which) ->{
            if(which==0){
                isVideo = true;
                if(!checkCameraPermission()){
                    requestCameraPermission();
                    Log.e("???????????? ????????? ????????? ","???????????? ????????? ?????????");
                }
                else{
                    videoPickCamera();
                    Log.e("pickFromCamera","pickFromCamera");
                }
            }else if(which==1){
                //??????????????????
                isVideo = true;
                if(!checkStoragePermission()){
                    requestStoragePermission();
                }
                else{
                    videoPickGallery();
                }
            }
        });
        builder.create().show();
    }
    private void uploadStorage( String documentName, Map<String, Object> city, String title, String description){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference washingtonRef = db.collection("Posts").document(tutortuty).collection(category).document(documentName);
        int urisSize = uris.size();
        uploadUrls.clear();
        for (int i = 0; i< urisSize; i++){
            final int index = i;
            final URLS uri = new URLS();
            uri.setOrder(index);
            final String imagevideo = (String) uris.get(index).get(0);
            final Uri putUri = ((Uri)uris.get(index).get(1));
            final String filePathAndName = "Posts/"+"post_" + documentName + "/"+ index;
            final StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            StorageTask uploadTask;
            if(imagevideo.equals("image")){
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), putUri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();
                    uploadTask  = ref.putBytes(data);
                } catch (IOException e) {
                    e.printStackTrace();
                    startToast("??????");
                    return;
                }
            }
            else{
                uploadTask  = ref.putFile(putUri);
            }
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            try{
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while(!uriTask.isSuccessful());
                                String downloadUri = uriTask.getResult().toString();
                                if (uriTask.isSuccessful()) {
                                    Log.e("downloadUri",""+downloadUri);
                                    uri.setImagevideo(imagevideo);
                                    uri.setUrls(downloadUri);
                                    uploadUrls.add(uri);
                                    if(index == urisSize-1){
                                        Log.e("uploadUrls ",""+uploadUrls);
                                        Collections.sort(uploadUrls);
                                        city.put("pImage", uploadUrls);
                                        washingtonRef.set(city)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                                        Toast.makeText(AddPostActivity.this,"????????? ??????",Toast.LENGTH_LONG).show();
                                                        Intent intent1 = new Intent("deletePost");
                                                        LocalBroadcastManager.getInstance(AddPostActivity.this).sendBroadcast(intent1);
                                                        prepareNotification(documentName,name+"??? ???????????? ???????????????."
                                                                ,""+title+"\n"+description
                                                                ,"PostNotification","POST");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error writing document", e);
                                                        startToast("????????? ??????");
                                                    }
                                                });
                                        //washingtonRef.update("pImage", uploadUrls);
                                    }
                                    //washingtonRef.update("pImage", FieldValue.arrayUnion(uri));
                                }
                            }catch (Exception e){
                                Log.e("??? ?????? ????????? ",""+e.getMessage());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("e ? : ",""+e.getMessage());
                            startToast(""+e.getMessage());
                        }
                    });
        }

    }

    private void uploadData(String title, String description) {
        pd.setMessage("????????? ????????????...");
        pd.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String categoriesCount = "categoriesCount."+category;
        int urisSize = uris.size();
        db.collection("users").document(uid)
                .update(
                        categoriesCount, FieldValue.increment(1)
                );
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String documentName = timeStamp + uid;
        if(urisSize != 0){
            Log.e("uris ?????????",""+urisSize);
            Map<String, Object> city = new HashMap<>();
            city.put("uid", uid);
            city.put("uName", name);
            city.put("uEmail", email);
            city.put("uDp", dp);
            city.put("pId", documentName);
            city.put("pTitle", title);
            city.put("pDescr",description);
            city.put("pTime", timeStamp);
            city.put("arrayCount", urisSize);
            city.put("pCategory", category);
            city.put("pTutortuty", tutortuty);
            city.put("pLikes", 0);
            Log.e("city : ",city+"");
            uploadStorage(documentName, city, title, description);
        }
        else{
            //???????????????
            Map<String, Object> city = new HashMap<>();
            city.put("uid", uid);
            city.put("uName", name);
            city.put("uEmail", email);
            city.put("uDp", dp);
            city.put("pId", documentName);
            city.put("pTitle", title);
            city.put("pDescr",description);
            city.put("pImage", null);
            city.put("pTime", timeStamp);
            city.put("arrayCount", 0);
            city.put("pCategory", category);
            city.put("pTutortuty", tutortuty);
            city.put("pLikes", 0);
            db = FirebaseFirestore.getInstance();
            db.collection("Posts").document(tutortuty).collection(category).document(documentName)
                    .set(city)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            startToast("????????? ??????");
                            Intent intent1 = new Intent("deletePost");
                            LocalBroadcastManager.getInstance(AddPostActivity.this).sendBroadcast(intent1);
                            prepareNotification(documentName,name+"??? ???????????? ???????????????."
                                    ,""+title+"\n"+description
                                    ,"PostNotification","POST");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                            startToast("????????? ??????");

                        }
                    });

        }
        pd.dismiss();
        onBackPressed();
    }
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void showImagePickDialog() {
        String[] options = {"?????????", "?????????"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("???????????????");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    isVideo = false;
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                        Log.e("???????????? ????????? ????????? ", "???????????? ????????? ?????????");
                    } else {
                        pickFromCamera();
                        Log.e("pickFromCamera", "pickFromCamera");
                    }
                } else if (which == 1) {
                    //???????????????
                    isVideo = false;
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
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
        boolean result3 = checkSelfPermission(Manifest.permission.WAKE_LOCK) == (PackageManager.PERMISSION_GRANTED);
        return result1 && result2 && result3;
    }
    private void requestCameraPermission(){
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }
    private void showDialogToGetPermission() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
    private void pickFromCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        image_rui = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }
    private void videoPickCamera(){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE);
    }
    private void videoPickGallery(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "????????? ??????"),VIDEO_PICK_GALLERY_CODE);
    }
    private void pickFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length > 0 ){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        if(isVideo){
                            videoPickCamera();
                        }else{
                            pickFromCamera();
                        }

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
                        if(isVideo){
                            videoPickGallery();
                        }else{
                            pickFromGallery();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("resultCode : ",""+resultCode);
        Log.e("RESULT_OK : ",""+ RESULT_OK);
        Log.e("requestCode : ",""+ requestCode);
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                image_rui = data.getData();
                Log.e("????????? uri",""+image_rui);
                //imageIv.setImageURI(image_rui);

                if(image_rui !=null){
                    List arr1 = new ArrayList();
                    arr1.add("image");
                    arr1.add(image_rui);
                    uris.add(arr1);
                    sliderAdapter  = (new SliderAdapter(AddPostActivity.this, uris, viewPager2));
                    viewPager2.setAdapter(sliderAdapter);
                    setupIndicators();

                    viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            setCurrentIndicator(position);
                        }
                    });
                }
                //sendImageMessage(image_rui);

            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                Log.e("????????? uri",""+image_rui);
                // profileImageView.setImageURI(null);
                // profileImageView.setImageURI(image_rui);
                //imageIv.setImageURI(image_rui);
                //sendImageMessage(image_rui);
                if(image_rui !=null){
                    List arr1 = new ArrayList();
                    arr1.add("image");
                    arr1.add(image_rui);
                    uris.add(arr1);
                    sliderAdapter  = (new SliderAdapter(AddPostActivity.this, uris, viewPager2));
                    viewPager2.setAdapter(sliderAdapter);
                    setupIndicators();
                    viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                            setCurrentIndicator(position);
                        }
                    });
                }

            }
            else if(requestCode == VIDEO_PICK_GALLERY_CODE){
                image_rui = data.getData();
                videoUri = data.getData();
                Log.e("????????? uri",""+videoUri);
                List arr1 = new ArrayList();
                arr1.add("video");
                arr1.add(videoUri);
                uris.add(arr1);
                sliderAdapter  = (new SliderAdapter(AddPostActivity.this, uris, viewPager2));
                viewPager2.setAdapter(sliderAdapter);
                setupIndicators();
                viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        setCurrentIndicator(position);
                    }
                });
                // profileImageView.setImageURI(null);
                // profileImageView.setImageURI(image_rui);
                //sendImageMessage(videoUri);
            }
            else if(requestCode == VIDEO_PICK_CAMERA_CODE){
                image_rui = data.getData();
                videoUri = data.getData();
                List arr1 = new ArrayList();
                arr1.add("video");
                arr1.add(videoUri);
                uris.add(arr1);
                sliderAdapter  = (new SliderAdapter(AddPostActivity.this, uris, viewPager2));
                viewPager2.setAdapter(sliderAdapter);
                setupIndicators();
                viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        setCurrentIndicator(position);
                    }
                });
                //sendImageMessage(videoUri);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }
    private void startToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null){
            email = user.getEmail();
            uid = user.getUid();

        } else{
            startActivity(new Intent(AddPostActivity.this, MainActivity.class));
            finish();
        }
    }
}