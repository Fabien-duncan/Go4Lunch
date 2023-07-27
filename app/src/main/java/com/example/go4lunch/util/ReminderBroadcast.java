package com.example.go4lunch.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.views.ConnectedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReminderBroadcast extends BroadcastReceiver {

    public static String text;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent repeating_intent = new Intent(context, ConnectedActivity.class);
        repeating_intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        CollectionReference collectionReference = db.collection("users");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    String workmates = "";
                    List<User> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User workmate = document.toObject(User.class);
                        list.add(workmate);
                    }
                    User currentUser = new User();
                    for (int i = 0; i < list.size();i++){
                        if(user.getEmail().equals(list.get(i).getEmail())){
                            currentUser = list.get(i);
                            list.remove(i);
                        }
                    }
                    for (int i = 0; i < list.size();i++){
                        if(list.get(i).getLunchChoiceId().equals(currentUser.getLunchChoiceId())) workmates += " " + list.get(i).getDisplayName() + ",";
                    }
                    if(workmates.endsWith(",")) {

                        workmates= workmates.substring(0, workmates.length() - 1);
                    }
                    String address = loadAddress(context, currentUser.getEmail());

                    Log.d("broadcast","in broadcast");
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, repeating_intent, PendingIntent.FLAG_IMMUTABLE);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyRestaurant")
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.drawable.baseline_notifications_active_24)
                            .setContentTitle("Restaurant Choice")
                            //.setContentText("You are attending " + currentUser.getLunchChoiceName() +", " + address + " \n Workmate: " + workmates)
                            .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(currentUser.getLunchChoiceName() +", " + address + "\nWorkmate attending: " + workmates))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true);

                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                    notificationManagerCompat.notify(200, builder.build());
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
        /*Log.d("broadcast","in broadcast");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, repeating_intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyRestaurant")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle("Restaurant Choice")
                .setContentText("You are attending " + name[0])
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(200, builder.build());*/

    }
    public String loadAddress(Context context, String email){
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString(email, "No address found");
    }
}
