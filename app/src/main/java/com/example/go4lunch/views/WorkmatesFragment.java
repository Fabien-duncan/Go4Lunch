package com.example.go4lunch.views;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.adapter.MyWorkmatesAdapter;
import com.example.go4lunch.adapter.WorkmatesRecyclerViewInterface;
import com.example.go4lunch.di.Injection;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.viewmodel.WorkmatesViewModel;

import java.util.List;

public class WorkmatesFragment extends Fragment implements WorkmatesRecyclerViewInterface {
    private List<User> workmatesList;
    private MyWorkmatesAdapter mMyWorkmatesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workmates, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView workmatesRecyclerView = view.findViewById(R.id.workmates_rv);

        AuthenticationRepository authenticationRepository = Injection.createAuthenticationRepository(this.getContext());
        WorkmatesViewModel workmatesViewModel = new WorkmatesViewModel(authenticationRepository);
        workmatesViewModel.setCurrentWorkmates();
        //mWorkmatesViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(getContext())).get(MainActivityViewModel.class);
        workmatesRecyclerView.setHasFixedSize(true);
        workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //workmatesList = mMainActivityViewModel.getWorkmatesMutableLiveData().getValue();
        workmatesList = workmatesViewModel.getAllWorkmates().getValue();
        //System.out.println("workmate 1: " + workmatesList.get(0).getDisplayName());
        mMyWorkmatesAdapter = new MyWorkmatesAdapter(getContext(), workmatesList, this);
        workmatesRecyclerView.setAdapter(mMyWorkmatesAdapter);
        //mMyWorkmatesAdapter.notifyDataSetChanged();
        workmatesViewModel.getAllWorkmates().observe((LifecycleOwner) requireContext(), users -> {
            workmatesList = users;
            mMyWorkmatesAdapter.setWorkmatesList(users);
        });

    }

    @Override
    public void onItemClicked(int position) {
        Log.d("workmateClicked", "you have clicked on " + workmatesList.get(position).getDisplayName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!workmatesList.get(position).isToday() || workmatesList.get(position).getLunchChoiceId().isEmpty()){
                Toast.makeText(getActivity(), "Workmate has not decided on a lunch choice yet!", Toast.LENGTH_LONG).show();
            }else{
                Restaurant tempRestaurant = new Restaurant();
                tempRestaurant.setId(workmatesList.get(position).getLunchChoiceId());
                RestaurantDetailDialogue restaurantDetailDialogue = RestaurantDetailDialogue.newInstance();
                restaurantDetailDialogue.setCurrentRestaurant(tempRestaurant);
                restaurantDetailDialogue.show(this.requireActivity().getSupportFragmentManager(), getString(R.string.restaurant_details));
            }
        }
    }
}