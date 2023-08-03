package com.example.go4lunch.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.adapter.MyWorkmatesAdapter;
import com.example.go4lunch.adapter.WorkmatesRecyclerViewInterface;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.viewmodel.MainActivityViewModel;
import com.example.go4lunch.viewmodel.WorkmatesViewModel;

import java.util.List;

public class WorkmatesFragment extends Fragment implements WorkmatesRecyclerViewInterface {
    private RecyclerView workmatesRecyclerView;
    private List<User> workmatesList;
    private MyWorkmatesAdapter mMyWorkmatesAdapter;
    private WorkmatesViewModel mWorkmatesViewModel;
    private AuthenticationRepository mAuthenticationRepository;
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

        mAuthenticationRepository = new AuthenticationRepository(getContext());
        mWorkmatesViewModel = new WorkmatesViewModel(mAuthenticationRepository);
        mWorkmatesViewModel.setCurrentWorkmates();
        //mWorkmatesViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(getContext())).get(MainActivityViewModel.class);
        workmatesRecyclerView.setHasFixedSize(true);
        workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //workmatesList = mMainActivityViewModel.getWorkmatesMutableLiveData().getValue();
        workmatesList = mWorkmatesViewModel.getAllWorkmates().getValue();
        //System.out.println("workmate 1: " + workmatesList.get(0).getDisplayName());
        mMyWorkmatesAdapter = new MyWorkmatesAdapter(getContext(), workmatesList, this);
        workmatesRecyclerView.setAdapter(mMyWorkmatesAdapter);
        //mMyWorkmatesAdapter.notifyDataSetChanged();
        mWorkmatesViewModel.getAllWorkmates().observe((LifecycleOwner) getContext(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                workmatesList = users;
                mMyWorkmatesAdapter.setWorkmatesList(users);
            }
        });

    }

    @Override
    public void onItemClicked(int position) {
        Log.d("workmateClicked", "you have clicked on " + workmatesList.get(position).getDisplayName());

    }
}