package com.example.arrows_m.util;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.arrows_m.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FirestoreDatabase implements Serializable {

    private static final String TAG = "Firestore Database";
    private final String currentUserId;
    private FirebaseFirestore firebaseFirestore = null;

    private MainActivity mainActivity;

    private int existScore = 0;
    private long existTotalGamePlayTime = 0;

    public FirestoreDatabase(MainActivity activity) {
        this.mainActivity = activity;
        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getUid();
    }

    public void checkPlayer(final GoogleSignInAccount acct) {
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseField.COLLECTION_NAME).document(currentUserId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    getExistScoreFromDatabase();
                    String message = "Welcome back! " + acct.getDisplayName();
                    toastStatus(message);
                    return;
                } else {
                    addPlayer(acct);
                }
            }
        });
    }

    private void addPlayer(final GoogleSignInAccount acct) {
        Map<String, Object> playerMap = new HashMap<>();
        playerMap.put(DatabaseField.GIVEN_NAME, acct.getGivenName());
        playerMap.put(DatabaseField.FAMILY_NAME, acct.getFamilyName());
        playerMap.put(DatabaseField.DISPLAY_NAME, acct.getDisplayName());
        playerMap.put(DatabaseField.EMAIL, acct.getEmail());
        playerMap.put(DatabaseField.ACCT_ID, acct.getId());
        playerMap.put(DatabaseField.TOTAL_GAME_PLAY_TIME, 0);
        playerMap.put(DatabaseField.SCORE, 0);

        firebaseFirestore.collection(DatabaseField.COLLECTION_NAME).document(currentUserId)
                .set(playerMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String message = "Welcome! " + acct.getDisplayName();
                getExistScoreFromDatabase();
                toastStatus(message);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error creating document", e);

            }
        });
    }

    public void updatePlayerData(int score, long gamePlayTime) {
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseField.COLLECTION_NAME).document(currentUserId);
        if (score > existScore || (score == existScore && gamePlayTime < existTotalGamePlayTime)) {
            updateScoreAndTime(documentReference, score, gamePlayTime);
            existScore = score;
            existTotalGamePlayTime = gamePlayTime;
        }
    }

    private void getExistScoreFromDatabase() {
        DocumentReference documentReference = firebaseFirestore.collection(DatabaseField.COLLECTION_NAME).document(currentUserId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        existScore = document.getLong(DatabaseField.SCORE).intValue();
                        existTotalGamePlayTime = document.getLong(DatabaseField.TOTAL_GAME_PLAY_TIME);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void updateScoreAndTime(DocumentReference documentReference, int score, long gamePlayTime) {
        Map<String, Object> scoreMap = new HashMap<>();
        scoreMap.put(DatabaseField.TOTAL_GAME_PLAY_TIME, gamePlayTime);
        scoreMap.put(DatabaseField.SCORE, score);

        documentReference.update(scoreMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Score and game play time successfully updated!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
            }
        });
    }

    private void toastStatus(String message) {
        Toast.makeText(mainActivity, message, Toast.LENGTH_LONG).show();
    }

    protected FirebaseFirestore getFirestore() {
        return firebaseFirestore;
    }

    public int getExistScore() {
        return existScore;
    }

    public long getExistTotalGamePlayTime() {
        return existTotalGamePlayTime;
    }
}


