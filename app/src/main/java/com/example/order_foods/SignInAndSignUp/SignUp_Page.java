package com.example.order_foods.SignInAndSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.order_foods.Common.Common;
import com.example.order_foods.Model.User;
import com.example.order_foods.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp_Page extends AppCompatActivity {

    EditText edtPhone, edtName, edtPassword;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__page);

        edtPhone = (EditText) findViewById(R.id.edtPhone_SignUp);
        edtName = (EditText) findViewById(R.id.edtName_SignUp);
        edtPassword = (EditText) findViewById(R.id.edtPassword_SignUp);
        btnSignUp = (Button) findViewById(R.id.btnSignUp_2);

        //fire base
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtPhone.getText().toString().equals("") ||
                        edtPassword.getText().toString().equals("") ||
                        edtName.getText().toString().equals(""))
                {
                    Toast.makeText(SignUp_Page.this, "Must not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    if(Common.isConnectedToInternet(getBaseContext())) {
                        ProgressDialog mDialog = new ProgressDialog(SignUp_Page.this);
                        mDialog.setMessage("Please waiting...");
                        mDialog.show();

                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child(edtPhone.getText().toString()).exists()){
                                    mDialog.dismiss();
                                    Toast.makeText(SignUp_Page.this, "Tài khoản đã tồn tại !", Toast.LENGTH_SHORT).show();
                                } else {
                                    mDialog.dismiss();
                                    String name = edtName.getText().toString();
                                    String phone = edtPhone.getText().toString();
                                    String pass = edtPassword.getText().toString();
                                    User user = new User(name,pass);
                                    myRef.child(edtPhone.getText().toString()).setValue(user);
                                    Toast.makeText(SignUp_Page.this, "Đăng ký tài khoản thành công !", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        Toast.makeText(SignUp_Page.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });


    }
}