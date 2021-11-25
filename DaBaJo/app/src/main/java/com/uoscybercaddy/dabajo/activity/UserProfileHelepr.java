package com.uoscybercaddy.dabajo.activity;

import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileHelepr
{
    // 유저가 프로필을 만들어야 프로필 데이터가 있다.
    public static void GetUserProfile(String userUID, OnCompleteListener<DocumentSnapshot> OnComleteListner)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference targetUserDocRef = db.collection("users").document(userUID);

        targetUserDocRef.get().addOnCompleteListener(OnComleteListner);
    }

}
