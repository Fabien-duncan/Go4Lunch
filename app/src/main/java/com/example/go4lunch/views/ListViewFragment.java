package com.example.go4lunch.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.adapter.RestaurantRecyclerViewInterface;
import com.example.go4lunch.adapter.RestaurantsAdapter;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.viewmodel.ConnectedActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListViewFragment extends Fragment implements RestaurantRecyclerViewInterface {
    private RestaurantsAdapter mRestaurantsAdapter;
    private List<Restaurant> restaurantsList;
    private ConnectedActivityViewModel mConnectedActivityViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_view, container, false);

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView restaurantsRecyclerView = view.findViewById(R.id.restaurant_list_rv);

        mConnectedActivityViewModel = ((ConnectedActivity) requireActivity()).getConnectedActivityViewModel();

        restaurantsRecyclerView.setHasFixedSize(true);
        restaurantsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        restaurantsList = new ArrayList<>();

        mRestaurantsAdapter = new RestaurantsAdapter(getContext(), restaurantsList, this);
        restaurantsRecyclerView.setAdapter(mRestaurantsAdapter);

        mConnectedActivityViewModel.setCurrentWorkmates();

        TextView noData_tv = view.findViewById(R.id.restaurant_list_no_data);

        mConnectedActivityViewModel.getRestaurantsMutableLiveData().observe(requireActivity(), restaurants -> {
            restaurantsList = restaurants;
            if(restaurants.size()>0)noData_tv.setVisibility(View.INVISIBLE);
            else noData_tv.setVisibility(View.VISIBLE);
            mRestaurantsAdapter.setRestaurantList(restaurants);

        });
        mConnectedActivityViewModel.getAllWorkmates().observe(requireActivity(), users -> {
            if(users.size()>0){

                mConnectedActivityViewModel.updateAttending(users);
            }
        });
    }


    @Override
    public void onItemClick(int position) {
        RestaurantDetailDialogue restaurantDetailDialogue = RestaurantDetailDialogue.newInstance();
        restaurantDetailDialogue.setCurrentRestaurant(restaurantsList.get(position));
        restaurantDetailDialogue.show(requireActivity().getSupportFragmentManager(),getString(R.string.restaurant_details));

    }
}