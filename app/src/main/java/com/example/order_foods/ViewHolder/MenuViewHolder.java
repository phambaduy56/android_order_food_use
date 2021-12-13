package com.example.order_foods.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.order_foods.Interface.ItemClickListencer;
import com.example.order_foods.R;


public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuName;
    public ImageView imageView;

    private ItemClickListencer itemClickListencer;

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);

        txtMenuName = (TextView)itemView.findViewById(R.id.menu_name);
        imageView = (ImageView) itemView.findViewById(R.id.menu_image);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListencer(ItemClickListencer itemClickListencer) {
        this.itemClickListencer = itemClickListencer;
    }

    @Override
    public void onClick(View v) {
        itemClickListencer.onClick(v, getAdapterPosition(), false);
    }
}
