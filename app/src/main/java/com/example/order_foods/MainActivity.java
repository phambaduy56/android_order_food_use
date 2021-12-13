package com.example.order_foods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.order_foods.Common.Common;
import com.example.order_foods.Model.User;
import com.example.order_foods.SignInAndSignUp.SignUp_Page;
import com.example.order_foods.SignInAndSignUp.SingIn_Page;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;


public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnSignUp;

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (Button) findViewById(R.id.btnSignin);
        btnSignUp = (Button) findViewById(R.id.btnSignup);

        //fire base
         database = FirebaseDatabase.getInstance();
         myRef = database.getReference("User");

         Paper.init(this);


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SingIn_Page.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUp_Page.class));
            }
        });

        String user = Paper.book().read(Common.USER_KEY);
        String pass = Paper.book().read(Common.PWD_KEY);

        if(user != null && pass != null) {
            if(!user.isEmpty() && !pass.isEmpty()) {
                login(user, pass);
            }
        }

    }

    private void login(String phone, String pass) {
        if(Common.isConnectedToInternet(getBaseContext())) {
            ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please waiting...");
            mDialog.show();

            //remember user and password

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //check user
                    if(snapshot.child(phone).exists()){
                        mDialog.dismiss();
                        //Get user infor
                        User user = snapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if(user.getPass().equals(pass))
                        {
                            Toast.makeText(MainActivity.this, "Sign in successfully !", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this,Home.class);
                            Common.currentUer = user;
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Sign in failed !!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User not exists in database !", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            Toast.makeText(MainActivity.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
            return;
        }
    }



}