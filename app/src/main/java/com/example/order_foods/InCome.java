package com.example.order_foods;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InCome extends AppCompatActivity {

        EditText edtDate_Income;
        Button btnDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_come);

        edtDate_Income  = (EditText) findViewById(R.id.edtDate_Income);
        btnDisplay = (Button) findViewById(R.id.btnDisplay);

        btnDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InCome.this, ""+edtDate_Income.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}