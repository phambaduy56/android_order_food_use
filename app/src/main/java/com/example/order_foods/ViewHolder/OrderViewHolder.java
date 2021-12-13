package com.example.order_foods.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.order_foods.Interface.ItemClickListencer;
import com.example.order_foods.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress;
    public ImageView btn_delete;

    private ItemClickListencer itemClickListencer;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        txtOrderId = (TextView) itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.order_status);
        txtOrderPhone = (TextView) itemView.findViewById(R.id.order_phone);
        txtOrderAddress = (TextView) itemView.findViewById(R.id.order_address);
        btn_delete = (ImageView) itemView.findViewById(R.id.btn_delete_order);
        itemView.setOnClickListener(this);

    }

    public void setItemClickListencer(ItemClickListencer itemClickListencer) {
        this.itemClickListencer = itemClickListencer;
    }

    @Override
    public void onClick(View v) {
        itemClickListencer.onClick(v, getAdapterPosition(),false);
    }
}
