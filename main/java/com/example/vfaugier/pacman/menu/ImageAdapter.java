package com.example.vfaugier.pacman.menu;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by vfaugier on 19/05/17.
 */

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private int[] imageFilesIds;

    public ImageAdapter(Context context, int[] imageFilesIds) {
        this.context = context;
        this.imageFilesIds = imageFilesIds;
    }

    public int getCount() {
        return imageFilesIds.length;
    }

    public Object getItem(int position) {
        return imageFilesIds[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setPadding(0, 0, 0, 0);
            imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), imageFilesIds[position]));
        } else {
            imageView = (ImageView) convertView;
        }
        return imageView;
    }
}