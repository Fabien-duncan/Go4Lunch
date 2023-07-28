package com.example.go4lunch.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.go4lunch.MainActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.viewmodel.ConnectedActivityViewModel;
import com.example.go4lunch.viewmodel.MainActivityViewModel;
import com.google.android.material.textfield.TextInputEditText;


public class CreateAccountFragment extends DialogFragment {
    private CreateAccountListener listener;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_account,container,false);


        Button create = view.findViewById(R.id.create_account_btn);
        EditText displayName_et = view.findViewById(R.id.create_account_display_name);
        EditText email_et = view.findViewById(R.id.create_account_email);
        EditText password_et = view.findViewById(R.id.create_account_password);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String displayName = displayName_et.getText().toString();
                String email = email_et.getText().toString();
                String password = password_et.getText().toString();
                listener.userInformation(displayName,email,password);
                dismiss();
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        // Set the width of the dialog proportional to 90% of the screen width
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.90), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);


        super.onResume();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (CreateAccountListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException((context.toString() + "must implementDialogueListener"));
        }
    }

    public interface CreateAccountListener{
        void userInformation(String displayName, String email, String password);
    }

}