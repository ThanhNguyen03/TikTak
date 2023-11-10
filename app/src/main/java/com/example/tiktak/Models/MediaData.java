package com.example.tiktak.Models;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;

public class MediaData {

    private static FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private static CollectionReference mediaObjectsRef = firebaseFirestore.collection("media_objects");
    public static ArrayList<MediaObject> mediaObjects= new ArrayList<>();

    public static void LoadData() {
        mediaObjectsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        MediaObject object = documentSnapshot.toObject(MediaObject.class);
                        mediaObjects.add(object);
                    }
                    Collections.shuffle(mediaObjects);
                })
                .addOnFailureListener(e -> {
                    Log.e("LoadData", "Error getting data: " + e.getMessage());
                });

    }

}
