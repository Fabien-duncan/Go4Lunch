package com.example.go4lunch.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.go4lunch.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SettingsFragment extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        Button save_btn = view.findViewById(R.id.settings_save_btn);
        CheckBox notification_checkBox = view.findViewById(R.id.settings_notification_checkbox);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        assert user != null;
        notification_checkBox.setChecked(sharedPreferences.getString(user.getEmail() + "notification", "true").equals("true"));
        save_btn.setOnClickListener(view1 -> {
            SharedPreferences sharedPreferences1 = requireActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences1.edit();

            editor.putString(user.getEmail()+"notification", String.valueOf(notification_checkBox.isChecked()));
            editor.apply();
            dismiss();
        });
        return view;
    }
    @Override
    public void onResume() {
        // Set the width of the dialog proportional to 90% of the screen width
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.90), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);


        super.onResume();
    }
}
