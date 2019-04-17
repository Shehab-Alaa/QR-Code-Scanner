package com.example.dell.qrcodescanner;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class WhatAppMessage extends AppCompatActivity {

    private EditText mobileNumber;
    private Button sendMessage;
    private String phoneNumber;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what_app_message);

        mobileNumber = findViewById(R.id.mobileNumber);
        sendMessage = findViewById(R.id.sendMessage);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = "+20";
                phoneNumber += mobileNumber.getText().toString();
                if (phoneNumber.equals(""))
                {
                    Toast.makeText(getApplicationContext() , "incorrect mobile number" , Toast.LENGTH_SHORT).show();
                }
                if (phoneNumber.length() != 14)
                {
                    Toast.makeText(WhatAppMessage.this, "please enter correct mobile number", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ArrayListHolder holder = (ArrayListHolder) getIntent().getExtras().getSerializable("items");
                    ArrayList<String> items = holder.getItems();

                    message = "";
                    for (String item : items)
                    {
                      message += item + "\n";
                    }

                    openWhatsApp();
                }
            }
        });
    }

    private void openWhatsApp(){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+ phoneNumber +"&text="+ message));
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
