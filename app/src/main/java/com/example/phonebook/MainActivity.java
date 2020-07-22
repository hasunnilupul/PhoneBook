package com.example.phonebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout parentLayout;
    GridView gridView;
    ArrayList<Person> list;
    ContactListAdapter adapter = null;
    public static SQLite sqlite;
    private boolean refreshNeeded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parentLayout=findViewById(R.id.main_activity_parentLayout);
        gridView = findViewById(R.id.gridView);
        list = new ArrayList<>();
        adapter = new ContactListAdapter(this, R.layout.layout_contact_item, list);
        gridView.setAdapter(null);

        // SQLite Connection
        sqlite =new SQLite(this, "Contacts.sqlite", null, 1);
        sqlite.queryData("CREATE TABLE IF NOT EXISTS USER (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, mobile VARCHAR,email VARCHAR, image BLOB)");
        loadContacts();
    }

    private void loadContacts() {
        gridView.setAdapter(adapter);
        Cursor cursor = sqlite.getData("SELECT * FROM USER ORDER BY name");
        list.clear();
        if(cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String mobile = cursor.getString(2);
                String email = cursor.getString(3);
                byte[] image = cursor.getBlob(4);

                list.add(new Person(id, name, mobile, email, image));
            }
        }else{
            Snackbar.make(parentLayout,"No Contact Cards !",Snackbar.LENGTH_LONG)
                    .setAction("New", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(MainActivity.this, AddContact.class));
                        }
                    })
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_main_new) {
            startActivity(new Intent(MainActivity.this, AddContact.class));
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        refreshNeeded=true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(refreshNeeded){
            loadContacts();
        }
    }
}
