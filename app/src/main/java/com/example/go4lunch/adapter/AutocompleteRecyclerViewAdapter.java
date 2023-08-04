package com.example.go4lunch.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.util.FormatString;

import java.util.List;

public class AutocompleteRecyclerViewAdapter extends RecyclerView.Adapter<AutocompleteRecyclerViewAdapter.MyViewHolder>{
    private final RestaurantRecyclerViewInterface mRestaurantRecyclerViewInterface;
    Context mContext;
    List<Restaurant> mRestaurantList;

    public AutocompleteRecyclerViewAdapter(Context context, List<Restaurant> restaurantList, RestaurantRecyclerViewInterface restaurantRecyclerViewInterface) {
        mContext = context;
        mRestaurantList = restaurantList;
        this.mRestaurantRecyclerViewInterface = restaurantRecyclerViewInterface;
    }

    @NonNull
    @Override
    public AutocompleteRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.place_autocomplete_item,parent,false);

        return new AutocompleteRecyclerViewAdapter.MyViewHolder(v, mRestaurantRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull AutocompleteRecyclerViewAdapter.MyViewHolder holder, int position) {
        Restaurant restaurant = mRestaurantList.get(position);

        holder.name.setText(FormatString.capitalizeEveryWord(restaurant.getName()));
        holder.address.setText(restaurant.getAddress());

    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setRestaurantList(List<Restaurant> restaurantList){
        this.mRestaurantList = restaurantList;
        notifyDataSetChanged();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name, address;
        public MyViewHolder(@NonNull View itemView, RestaurantRecyclerViewInterface restaurantRecyclerViewInterface) {
            super(itemView);

            name = itemView.findViewById(R.id.autocomplete_item_name);
            address = itemView.findViewById(R.id.autocomplete_item_address);

            itemView.setOnClickListener(view -> {
                if(restaurantRecyclerViewInterface!=null){
                    int pos = getAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION){
                        restaurantRecyclerViewInterface.onItemClick(pos);
                    }
                }
            });
        }
    }
}
