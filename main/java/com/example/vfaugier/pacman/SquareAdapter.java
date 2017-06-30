package com.example.vfaugier.pacman;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.vfaugier.pacman.square.Block;
import com.example.vfaugier.pacman.square.Square;

import java.util.ArrayList;


/**
 * Created by vfaugier on 19/05/17.
 */

public class SquareAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Square> squares;
    private int imageSize;

    public SquareAdapter(Context context, ArrayList<Square> squares, int imageSize) {
        this.context = context;
        this.squares = squares;
        this.imageSize = imageSize;
    }

    public void moveToPosition(int basePosition, int newPosition, Block block) {
        squares.get(basePosition).removeBlockFromList(block);
        squares.get(newPosition).addBlockToList(block);
    }

    public int getCount() {
        return squares.size();
    }

    public Object getItem(int position) {
        return squares.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(imageSize, imageSize));
            imageView.setPadding(0, 0, 0, 0);
            imageView.setImageBitmap(squares.get(position).getActualSprite());
        } else {
            imageView = (ImageView) convertView;
        }
        return imageView;
    }
}