package com.example.go4lunch.views;

import android.os.Bundle;
import android.util.Log;
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
        //initRestaurants();

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

    private void initRestaurants() {
        restaurantsList = new ArrayList<>();

        restaurantsList.add(new Restaurant(
                "01",
                "Zinc",
                "16 ch du four",
                45,26,
                0,
                "https://media.istockphoto.com/id/1446478827/fr/photo/un-chef-cuisine-dans-la-cuisine-de-son-restaurant.jpg?s=1024x1024&w=is&k=20&c=_KRkTJnju8zm8pTSs-aOq9J4mdtzlPc31AucwKR54CY=",
                "french",
                "23:00",
                4.5,
                120,
                "+33 6 58 32 57 01"
                ));
        restaurantsList.add(new Restaurant(
                "01",
                "Les deux Roch",
                "14 impasse, Les bas plans",
                45,20,
                2,
                "https://media.istockphoto.com/id/1446375027/fr/photo/r%C3%A9union-daffaires-dans-le-restaurant.jpg?s=1024x1024&w=is&k=20&c=fypbQnbN5F2zEI81FWLSHSA3EH5cpQXyHZDiSaWEQBY=",
                "french",
                "23:00",
                3.2,
                152,
                "+33 6 58 32 57 01"
        ));
        restaurantsList.add(new Restaurant(
                "03", "La Taverne", "144 ch de Bargemon", 46,25,
                1,
                "https://media.istockphoto.com/id/1268553744/fr/photo/couples-asiatiques-de-sup%C3%A9rieur-commandant-la-nourriture-dans-un-restaurant-pendant.jpg?s=1024x1024&w=is&k=20&c=3OiKrP-4HweCQ404BjtzibMM9ji-ILQzFgkxWftJrus=",
                "french",
                "23:00",
                0.8,
                230,
                "+33 6 58 32 57 01"
        ));
        restaurantsList.add(new Restaurant(
                "04", "Mambo Pizza", "56 route de Fayence", 43,23,
                3,
                "https://media.istockphoto.com/id/1227451737/fr/photo/patrons-japonais-appr%C3%A9ciant-la-bi%C3%A8re-et-le-sak%C3%A9-%C3%A0-tokyo-izakaya.jpg?s=1024x1024&w=is&k=20&c=6KaTwlECzrUgTzLUEcDLWpGCa1-Yisz8Znc7T0ehEcE=",
                "french",
                "23:00",
                4,
                80,
                "+33 6 58 32 57 01"
        ));

    }

    @Override
    public void onItemClick(int position) {
        Log.d("List Restaurant click", "position: " + restaurantsList.get(position).getName());
        RestaurantDetailDialogue restaurantDetailDialogue = RestaurantDetailDialogue.newInstance();
        restaurantDetailDialogue.setCurrentRestaurant(restaurantsList.get(position));
        restaurantDetailDialogue.show(requireActivity().getSupportFragmentManager(),getString(R.string.restaurant_details));

    }
}