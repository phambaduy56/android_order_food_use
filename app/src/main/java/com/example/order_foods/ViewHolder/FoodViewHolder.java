package com.example.order_foods.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.order_foods.Interface.ItemClickListencer;
import com.example.order_foods.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView food_name, food_price;
    public ImageView food_image, fav_image, cart_image;

    private ItemClickListencer itemClickListencer;

    public void setItemClickListencer(ItemClickListencer itemClickListencer) {
        this.itemClickListencer = itemClickListencer;
    }

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

        food_name = (TextView)itemView.findViewById(R.id.food_name);
        food_image = (ImageView) itemView.findViewById(R.id.food_image);
        fav_image = (ImageView) itemView.findViewById(R.id.fav);
        food_price = (TextView)itemView.findViewById(R.id.food_price);
        cart_image = (ImageView)itemView.findViewById(R.id.cart_food);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListencer.onClick(v, getAdapterPosition(), false);
    }
}
