package com.declarevariable.imagegallery;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by m on 10/30/2015.
 * Grid View Class
 */
public class GridAdapter extends BaseAdapter {
    Integer[] images;
    Context mContext;
    ArrayList<String> galleryList;

    GridAdapter( Integer[] _images, Context _context/*, ArrayList<String> _galleryList*/){
        this.images = _images;
        this.mContext = _context;
        galleryList = new ArrayList<String>();
        galleryList = Common.SharedPref.get(_context);
        //this.galleryList = _galleryList;
    }

    public void updateList(ArrayList<String> _list){
        galleryList = _list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return images.length;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return images[arg0];
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater li;
        View gridItem;
        if (convertView == null) {
            gridItem = new View(mContext);
            li = ((Activity) mContext).getLayoutInflater();
            gridItem = li.inflate(R.layout.grid_cell, parent, false);
        } else {
            gridItem = (View) convertView;
        }
        ImageView iv_itemImage = (ImageView) gridItem.findViewById(R.id.iv_image);
        if (galleryList.size() > 0) {//only if it contains some data
            if (position < galleryList.size() ){//0<3
                String item = galleryList.get(position);
                Bitmap itemBitmap = Common.BitmapConversion.stringToImage(item);
                iv_itemImage.setImageBitmap(itemBitmap);
            }else if (position == galleryList.size()) {
                iv_itemImage.setImageResource(android.R.drawable.ic_menu_add);
            }else{
                iv_itemImage.setImageResource(images[position]);
            }
        }else {
            if (position == 0) {
                iv_itemImage.setImageResource(android.R.drawable.ic_menu_add);
            }else{
                iv_itemImage.setImageResource(images[position]);
            }
        }
        //int iColor =  Color.parseColor("#eeba30");
        //iv_itemImage.setColorFilter(iColor);
        return gridItem;
    }
}
