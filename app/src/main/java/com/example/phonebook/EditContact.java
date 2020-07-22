package com.example.phonebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.example.phonebook.AddContact.imageViewToByte;

public class EditContact extends AppCompatActivity {
    final int REQUEST_CODE_GALLERY = 999;
    private int id;
    private SQLite sqlite;
    private byte[] imageBytes;
    private ImageView personImage;
    private TextView editPhoto,deleteContact,popupCancelButton,popupDeleteButton,discardChangesButton,keepEditingButton;
    private EditText personName,personMobile,personEmail;
    private Dialog dialog;
    private boolean dataChanged=false;
    private String name,mobile,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        init();
        dialog=new Dialog(this);
        Intent intent=getIntent();
        this.id=intent.getIntExtra("contactId",1);
        sqlite=MainActivity.sqlite;
        Cursor cursor=sqlite.getData("SELECT name,mobile,email,image FROM USER WHERE id='"+this.id+"'");
        if(cursor!=null && cursor.moveToFirst()){
            imageBytes = cursor.getBlob(3);
            Bitmap bitmap= BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            personImage.setImageBitmap(bitmap);
            personName.setText(cursor.getString(0));
            name=personName.getText().toString();
            personMobile.setText(cursor.getString(1));
            mobile=personMobile.getText().toString();
            personEmail.setText(cursor.getString(2));
            email=personEmail.getText().toString();
        }

        personName.requestFocus();
        personName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { dataChanged=true; }

            @Override
            public void afterTextChanged(Editable s) {
                if(!personName.getText().toString().equals(name)){
                    dataChanged=true;
                }else{
                    dataChanged=false;
                }
            }
        });
        personMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { dataChanged=true; }

            @Override
            public void afterTextChanged(Editable s) {
                if(!personMobile.getText().toString().equals(mobile)){
                    dataChanged=true;
                }else{
                    dataChanged=false;
                }
            }
        });
        personEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { dataChanged=true; }

            @Override
            public void afterTextChanged(Editable s) {
                if(!personEmail.getText().toString().equals(email)){
                    dataChanged=true;
                }else{
                    dataChanged=false;
                }
            }
        });
        editPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        EditContact.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });
        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConfirmDialog();
            }
        });
    }

    private void openConfirmDialog() {
        dialog.setContentView(R.layout.layout_delete_popup);
        dialog.setCancelable(false);
        popupDeleteButton=dialog.findViewById(R.id.popup_delete_button);
        popupCancelButton=dialog.findViewById(R.id.popup_cancel_button);

        popupDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlite.deleteData(id);
                finish();
            }
        });
        popupCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_contact_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_edit_contact_save) {
            updateContact();
        }
        return true;
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
                if(imageBytes!=imageViewToByte(personImage)){
                    dataChanged=true;
                }else{
                    dataChanged=false;
                }
                personImage.setImageBitmap(null);
                personImage.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateContact() {
        if(dataChanged){
            if(!personName.getText().toString().equals("")){
                if(!personMobile.getText().toString().equals("")){
                    if (!personEmail.getText().toString().equals("")){
                        sqlite.updateData(
                                personName.getText().toString().trim(),
                                personMobile.getText().toString().trim(),
                                personEmail.getText().toString().trim(),
                                imageViewToByte(personImage),
                                id);
                    }else {
                        sqlite.updateData(
                                personName.getText().toString().trim(),
                                personMobile.getText().toString().trim(),
                                "none",
                                imageViewToByte(personImage),
                                id);
                    }
                    backToInfo();
                }
            }
        }else {
            backToInfo();
        }
    }

    private void backToInfo() {
        Intent contactInfoIntent=new Intent(EditContact.this,ContactInfo.class);
        contactInfoIntent.putExtra("contactId",this.id);
        startActivity(contactInfoIntent);
        this.finish();
    }

    private void init() {
        personImage=findViewById(R.id.edit_contact_personImage);
        personName=findViewById(R.id.edit_contact_personName);
        personMobile=findViewById(R.id.edit_contact_personMobile);
        personEmail=findViewById(R.id.edit_contact_personEmail);
        editPhoto=findViewById(R.id.edit_contact_editPhoto);
        deleteContact=findViewById(R.id.edt_contact_deleteContact);
    }

    @Override
    public void onBackPressed() {
        if(!dataChanged){
            backToInfo();
        }else{
            openDiscardChangesDialog();
        }
    }

    private void openDiscardChangesDialog() {
        dialog.setContentView(R.layout.layout_discard_changes);
        dialog.setCancelable(false);
        discardChangesButton=dialog.findViewById(R.id.popup_discard_changes_button);
        keepEditingButton=dialog.findViewById(R.id.popup_keep_edit_button);

        discardChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactInfoIntent=new Intent(EditContact.this,ContactInfo.class);
                contactInfoIntent.putExtra("contactId",id);
                startActivity(contactInfoIntent);
                finish();
            }
        });
        keepEditingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
    }
}