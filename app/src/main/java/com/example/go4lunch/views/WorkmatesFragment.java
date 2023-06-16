package com.example.go4lunch.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.adapter.MyWorkmatesAdapter;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.model.User;
import com.example.go4lunch.viewmodel.MainActivityViewModel;

import java.util.List;

public class WorkmatesFragment extends Fragment {
    private RecyclerView workmatesRecyclerView;
    private List<User> workmatesList;
    private MyWorkmatesAdapter mMyWorkmatesAdapter;
    private MainActivityViewModel mMainActivityViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workmates, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        workmatesRecyclerView = view.findViewById(R.id.workmates_rv);
        mMainActivityViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(getContext())).get(MainActivityViewModel.class);
        workmatesRecyclerView.setHasFixedSize(true);
        workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        workmatesList = mMainActivityViewModel.getWorkmatesMutableLiveData().getValue();
        mMyWorkmatesAdapter = new MyWorkmatesAdapter(getContext(), workmatesList);
        workmatesRecyclerView.setAdapter(mMyWorkmatesAdapter);
        mMyWorkmatesAdapter.notifyDataSetChanged();

    }
}