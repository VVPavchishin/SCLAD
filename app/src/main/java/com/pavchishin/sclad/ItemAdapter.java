package com.pavchishin.sclad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class ItemAdapter extends ArrayAdapter {

    private LayoutInflater inflater;
    private int layout;
    private List<Item> items;

    public ItemAdapter(@NonNull Context context, int resource, List<Item> items) {
        super(context, resource, items);
        this.items = items;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        @SuppressLint("ViewHolder")
        View view = inflater.inflate(this.layout, parent, false);

        ImageView folderView = view.findViewById(R.id.folder_image);
        TextView nameView = view.findViewById(R.id.file_name);
        TextView dateView = view.findViewById(R.id.file_date);

        Item item = items.get(position);

        folderView.setImageResource(item.getFolderRecourse());
        nameView.setText(item.getName());
        dateView.setText(item.getDate());

        return view;
    }
}
