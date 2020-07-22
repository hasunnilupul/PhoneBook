package com.example.phonebook;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class ContactListAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Person> contactlist;

    public ContactListAdapter(Context context, int resourceLayout, ArrayList<Person> contactList) {
        this.context = context;
        this.layout = resourceLayout;
        this.contactlist = contactList;
    }

    @Override
    public int getCount() {
        return contactlist.size();
    }

    @Override
    public Object getItem(int position) {
        return contactlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView name, mobile;
        ImageView call, info;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View root = convertView;
        ViewHolder holder = new ViewHolder();

        if (root == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            root = inflater.inflate(layout, null);

            holder.name = root.findViewById(R.id.personName);
            holder.mobile = root.findViewById(R.id.personMobile);
            holder.call = root.findViewById(R.id.contact_item_call);
            holder.info = root.findViewById(R.id.contact_item_info);

            root.setTag(holder);
        } else {
            holder = (ViewHolder) root.getTag();
        }
        final Person person = contactlist.get(position);
        holder.name.setText(person.getName());
        holder.mobile.setText(person.getMobile());
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNo = person.getMobile();
                if(!TextUtils.isEmpty(phoneNo)) {
                    String dial = "tel:" + phoneNo;
                    context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                }else {
                    Toast.makeText(context, "Cannot make call", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactInfoIntent=new Intent(context,ContactInfo.class);
                contactInfoIntent.putExtra("contactId",person.getId());
                context.startActivity(contactInfoIntent);
            }
        });
        return root;
    }
}
