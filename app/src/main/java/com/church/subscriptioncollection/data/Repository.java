package com.church.subscriptioncollection.data;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.church.subscriptioncollection.PaymentPage;
import com.church.subscriptioncollection.R;
import com.church.subscriptioncollection.model.Volunteer;
import com.church.subscriptioncollection.result.CreateUserResult;
import com.church.subscriptioncollection.result.VolunteerResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

public class Repository {
    private static final String TAG = "DataSource";
    private static volatile Repository instance;
    private final Volunteer volunteer = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Repository() {

    }

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public void createUser(String volunteer,
                           String cardNo,
                           String paymentDate,
                           String amountPaid,
                           String modeOfPayment,
                           MutableLiveData<CreateUserResult> createUserResultLiveData) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("Volunteer", volunteer);
        userMap.put("CardNumber", cardNo);
        userMap.put("PaymentDate", paymentDate);
        userMap.put("Amount", amountPaid);
        userMap.put("ModeOfPayment", modeOfPayment);

        db.collection("Users").document(volunteer).set(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User successfully added!");
                        createUserResultLiveData.setValue(new CreateUserResult(cardNo));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error creating user", e);
                        createUserResultLiveData.setValue(new CreateUserResult("User Failed to Create"));
                    }

                });
    }

    public void getVolunteers(MutableLiveData<VolunteerResult> volunteerResultMutableLiveData) {
        db.collection("Volunteers").get()
                .addOnCompleteListener(task-> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        List<Volunteer> volunteerList = new ArrayList<Volunteer>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Volunteer volunteer = new Volunteer(document.getId(), document.getString("Name"));
                            volunteerList.add(volunteer);
                        }
                        volunteerResultMutableLiveData.setValue(new VolunteerResult(volunteerList));
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        volunteerResultMutableLiveData.setValue(new VolunteerResult(R.string.login_failed));
                    }
                });
    }
}