package com.example.go4lunch.util;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.views.ConnectedActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Broadcast receiver to handle sending reminder notifications.
 */
public class ReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent repeating_intent = new Intent(context, ConnectedActivity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        assert user != null;
        if (sharedPreferences.getString(user.getEmail() + "notification", "true").equals("true")) {

            CollectionReference collectionReference = db.collection("users");
            collectionReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    StringBuilder workmates = new StringBuilder();
                    List<User> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User workmate = document.toObject(User.class);
                        list.add(workmate);
                    }
                    User currentUser = new User();
                    for (int i = 0; i < list.size(); i++) {
                        if (Objects.equals(user.getEmail(), list.get(i).getEmail())) {
                            currentUser = list.get(i);
                            list.remove(i);
                            break;
                        }
                    }
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getLunchChoiceId().equals(currentUser.getLunchChoiceId())&& list.get(i).isToday())
                            workmates.append(" ").append(list.get(i).getDisplayName()).append(",");
                    }
                    if (workmates.toString().endsWith(",")) {

                        workmates = new StringBuilder(workmates.substring(0, workmates.length() - 1));
                    }
                    String address = loadAddress(context, currentUser.getEmail());

                    PendingIntent pendingIntent = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        pendingIntent = PendingIntent.getActivity(context, 0, repeating_intent, PendingIntent.FLAG_IMMUTABLE);
                    }

                    String msg;
                    if(!currentUser.isToday() || currentUser.getLunchChoiceId().isEmpty()) msg = context.getString(R.string.no_lunch_choice_notification_msg);
                    else if(workmates.toString().isEmpty()) msg = currentUser.getLunchChoiceName() + context.getString(R.string.no_workmates_msg);
                    else msg = currentUser.getLunchChoiceName() + ", " + address + "\n" + context.getString(R.string.workmates_attending) + workmates;

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyRestaurant")
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.drawable.baseline_notifications_active_24)
                            .setContentTitle(context.getString(R.string.restaurant_choice))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(msg))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true);

                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                    //checks permission
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        notificationManagerCompat.notify(200, builder.build());
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            });
        }else{
            Log.d("broadcast", "notification turned off");
        }
    }
    //Load the address from SharedPreferences
    private String loadAddress(Context context, String email){
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString(email, context.getString(R.string.no_address_found));
    }
}
