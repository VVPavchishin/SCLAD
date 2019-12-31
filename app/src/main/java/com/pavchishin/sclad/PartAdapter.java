package com.pavchishin.sclad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PartAdapter extends ArrayAdapter {

    private LayoutInflater inflater;
    private int layout;
    private List<Part> parts;

    public PartAdapter(Context context, int resource, List<Part> parts) {
        super(context, resource, parts);
        this.parts = parts;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        @SuppressLint("ViewHolder")
        View view = inflater.inflate(this.layout, parent, false);

        TextView artView = view.findViewById(R.id.txt_art);
        TextView nameView = view.findViewById(R.id.txt_name);
        TextView quantView = view.findViewById(R.id.txt_quantity);
        TextView priceView = view.findViewById(R.id.txt_price);

        Part part = parts.get(position);

        artView.setText(part.getArtiqulPart());
        nameView.setText(part.getNamePart());
        quantView.setText(String.valueOf(part.getQuantityPart()));
        priceView.setText(String.valueOf(part.getPricePart()));

        return view;
    }

}
