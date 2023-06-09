package com.example.go4lunch.injection;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.viewmodel.MainActivityViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final AuthenticationRepository mAuthenticationRepository;

    private final Executor executor;

    private static ViewModelFactory factory;

    public static ViewModelFactory getInstance(Context context) {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory(context);
                }
            }
        }
        return factory;
    }

    private ViewModelFactory(Context context) {
        this.mAuthenticationRepository = new AuthenticationRepository(context);
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    @NotNull
    public <T extends ViewModel>  T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(mAuthenticationRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
