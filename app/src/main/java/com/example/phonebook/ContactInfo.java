package com.example.phonebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

public class ContactInfo extends AppCompatActivity {
    private int id;
    private byte[] imageBytes;
    SQLite sqlite;
    private ImageView personImage,smsPerson,callPerson,mailPerson;
    private TextView personName,personMobile,personEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        init();
        Intent intent=getIntent();
        this.id=intent.getIntExtra("contactId",1);
        sqlite=MainActivity.sqlite;
        Cursor cursor=sqlite.getData("SELECT name,mobile,email,image FROM USER WHERE id='"+this.id+"'");
        if(cursor!=null && cursor.moveToFirst()){
            personName.setText(cursor.getString(0));
            personMobile.setText(cursor.getString(1));
            personEmail.setText(cursor.getString(2));
            imageBytes = cursor.getBlob(3);
            Bitmap bitmap=BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            personImage.setImageBitmap(bitmap);
        }

        smsPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo = personMobile.getText().toString();
                if(!TextUtils.isEmpty(phoneNo)) {
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNo));
                    smsIntent.putExtra("sms_body", "");
                    startActivity(smsIntent);
                }
            }
        });

        callPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo = personMobile.getText().toString();
                if(!TextUtils.isEmpty(phoneNo)) {
                    String dial = "tel:" + phoneNo;
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                }else {
                    Toast.makeText(ContactInfo.this, "Cannot make call", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mailPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL,new String[] {personEmail.getText().toString()});
                Intent mailer = Intent.createChooser(intent, null);
                startActivity(mailer);
            }
        });
    }

    private void init() {
        personImage=findViewById(R.id.contact_info_personImage);
        personName=findViewById(R.id.contact_info_personName);
        smsPerson=findViewById(R.id.contact_info_messagePerson);
        callPerson=findViewById(R.id.contact_info_callPerson);
        mailPerson=findViewById(R.id.contact_info_mailPerson);
        personMobile=findViewById(R.id.contact_info_personMobile);
        personEmail=findViewById(R.id.contact_info_personEmail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_info_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_info_contact_edit) {
            Intent editContactIntent = new Intent(ContactInfo.this, EditContact.class);
            editContactIntent.putExtra("contactId", this.id);
            startActivity(editContactIntent);
            this.finish();
        }
        return true;
    }
}
