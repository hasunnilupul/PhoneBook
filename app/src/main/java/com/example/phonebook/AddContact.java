package com.example.phonebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AddContact extends AppCompatActivity {
    ImageView addimg,putimg;
    EditText name,mobile,email;
    public static SQLite sqLite;
    final int REQUEST_CODE_GALLERY = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        sqLite=MainActivity.sqlite;
        name =  findViewById(R.id.name);
        mobile =  findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        addimg =  findViewById(R.id.imgadd);
        putimg  = findViewById(R.id.showimg);

        name.requestFocus();
        addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        AddContact.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_contact_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add_contact_save) {
            saveContact();
        }
        return true;
    }

    private void saveContact() {
        try{
            if(!name.getText().toString().equals("")){
                if(!mobile.getText().toString().equals("")){
                    if(!email.getText().toString().equals("")){
                        sqLite.insertData(
                                name.getText().toString().trim(),
                                mobile.getText().toString().trim(),
                                email.getText().toString().trim(),
                                imageViewToByte(putimg)
                        );
                    }else{
                        sqLite.insertData(
                                name.getText().toString().trim(),
                                mobile.getText().toString().trim(),
                                "none",
                                imageViewToByte(putimg)
                        );
                    }
                    Toast.makeText(getApplicationContext(), "Contact Saved Successfully!", Toast.LENGTH_SHORT).show();
                    name.setText("");
                    mobile.setText("");
                    email.setText("");
                    putimg.setImageResource(R.drawable.user_480px);
                    name.requestFocus();
                }else{
                    Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Enter a Person Name", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access storage !", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                putimg.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
