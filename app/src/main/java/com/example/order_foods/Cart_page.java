package com.example.order_foods;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.order_foods.Common.Common;
import com.example.order_foods.Common.Config;
import com.example.order_foods.Database.Database;
import com.example.order_foods.Model.Order;
import com.example.order_foods.Model.Request;
import com.example.order_foods.ViewHolder.CartAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Cart_page extends AppCompatActivity {

    private static final int PAYPAL_REQUEST_CODE = 9999;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    Button btnPlace;
    public TextView txtTotalPrice;

    List<Order> cart= new ArrayList<>();

    CartAdapter adapter;

    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    String address,comment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_page);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        //Fire Base
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Init
        recyclerView = (RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        btnPlace = (Button) findViewById(R.id.btnPlaceOrder);
        txtTotalPrice = (TextView) findViewById(R.id.total);


        LoadListFood();

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart.size() > 0) {
                    ShowAlertDialog();
                } else {
                    Toast.makeText(Cart_page.this, "Your cart is empty !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void ShowAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Cart_page.this);
        dialog.setTitle("One more step!");
        dialog.setMessage("Enter your address: ");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment,null);
        EditText edtAddress = (EditText) order_address_comment.findViewById(R.id.edtAddress);
        EditText edtComment = (EditText) order_address_comment.findViewById(R.id.edtComment);

        dialog.setView(order_address_comment);

        dialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

        dialog.setPositiveButton("CASH ON DELYVERY ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Request request = new Request(
                        Common.currentUer.getPhone(),
                        Common.currentUer.getName(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        "0",
                        edtComment.getText().toString(),
                        "CASH ON DELYVERY",
                        Common.getDate(),
                        cart
                );

                //submit fire base
                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);
                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart_page.this, "Thank you, Order Place", Toast.LENGTH_SHORT).show();
                finish();


            }
        });

        dialog.setNegativeButton("PAYPAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                address = edtAddress.getText().toString();
                comment = edtComment.getText().toString();
                String format = txtTotalPrice.getText().toString()
                        .replace("$", "")
                        .replace(",", "");

                PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(format), "USD",
                        "Food App Order",
                        PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                startActivityForResult(intent, PAYPAL_REQUEST_CODE);
            }
        });

        dialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PAYPAL_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation != null) {
                    try {
                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);

                        Request request = new Request(
                                Common.currentUer.getPhone(),
                                Common.currentUer.getName(),
                                address,
                                txtTotalPrice.getText().toString(),
                                "0",
                                comment,
                                jsonObject.getJSONObject("response").getString("state"),
                                Common.getDate(),
                                cart
                        );

                        //submit fire base
                        requests.child(String.valueOf(System.currentTimeMillis()))
                                .setValue(request);
                        new Database(getBaseContext()).cleanCart();
                        Toast.makeText(Cart_page.this, "Thank you, Order Place", Toast.LENGTH_SHORT).show();
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (requestCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Payment canceled", Toast.LENGTH_SHORT).show();
            else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID)
                Toast.makeText(this, "Invalid payment", Toast.LENGTH_SHORT).show();
        }
    }

    private void LoadListFood() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        int total = 0;
        for(Order order:cart){
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        }
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        String to = String.valueOf(total);
        txtTotalPrice.setText(to);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals(Common.DELETE)) {
            deleteCart(item.getOrder());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCart(int position) {
        cart.remove(position);
        new Database(this).cleanCart();
        for(Order item:cart) {
            new Database(this).addToCart(item);
        }
        LoadListFood();
    }
}