package com.example.order_foods.SignInAndSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.order_foods.Common.Common;
import com.example.order_foods.Home;
import com.example.order_foods.Model.User;
import com.example.order_foods.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class SingIn_Page extends AppCompatActivity {
    EditText edtPhone, edtPassword;
    Button btnSignIn;
    CheckBox ckbRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in__page);

        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignin_2);
        ckbRemember = (CheckBox) findViewById(R.id.ckbRemember);

        //Init paper
        Paper.init(this);

        //fire base
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("User");

        //event

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtPhone.getText().toString().equals("") || edtPassword.getText().toString().equals(""))
                {
                    Toast.makeText(SingIn_Page.this, "Must not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    if(Common.isConnectedToInternet(getBaseContext())) {
                        ProgressDialog mDialog = new ProgressDialog(SingIn_Page.this);
                        mDialog.setMessage("Please waiting...");
                        mDialog.show();

                        //remember user and password
                        if(ckbRemember.isChecked()) {
                            Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                            Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                        }

                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //check user
                                if(snapshot.child(edtPhone.getText().toString()).exists()){
                                    mDialog.dismiss();
                                    //Get user infor
                                    User user = snapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                    user.setPhone(edtPhone.getText().toString());
                                    if(user.getPass().equals(edtPassword.getText().toString()))
                                    {
                                        Toast.makeText(SingIn_Page.this, "Sign in successfully !", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SingIn_Page.this,Home.class);
                                        Common.currentUer = user;
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(SingIn_Page.this, "Sign in failed !!!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    mDialog.dismiss();
                                    Toast.makeText(SingIn_Page.this, "User not exists in database !", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        Toast.makeText(SingIn_Page.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        ///

    }
}