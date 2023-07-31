package com.example.go4lunch.views;

import android.content.Context;
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
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.go4lunch.R;

public class SignInFragment extends DialogFragment {
    private SignInListener listener;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in,container,false);


        Button signIn_btn = view.findViewById(R.id.sign_in_btn);
        EditText email_et = view.findViewById(R.id.sign_in_email);
        EditText password_et = view.findViewById(R.id.sign_in_password);
        signIn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_et.getText().toString();
                String password = password_et.getText().toString();
                listener.userSingInInformation(email,password);
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
            listener = (SignInFragment.SignInListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException((context.toString() + "must implementDialogueListener"));
        }
    }

    public interface SignInListener{
        void userSingInInformation(String email, String password);
    }
}
