package com.declarevariable.imagegallery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by m on 10/30/2015.
 * utility calss
 */
public class Common {
    public static class Keys{
        public static String SHARED_PREFS = "app_shared_pref";
        public static String PREF_GALLERY = "gallery_array";
        public static int PICK_GALLERY_IMAGE_REQUEST = 1;
        public static int CAMERA_REQUEST = 2;
    }
    public static class SharedPref{
        public static void update(Context mActivity, String gallery_array){
            SharedPreferences sharedPref = mActivity.getSharedPreferences(
                    Common.Keys.SHARED_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Common.Keys.PREF_GALLERY, gallery_array);
            editor.commit();
        }
        public static ArrayList<String> get(Context mActivity) {
            ArrayList<String> gallery = new ArrayList<String>();
            SharedPreferences prefs = mActivity.getSharedPreferences(Common.Keys.SHARED_PREFS, Context.MODE_PRIVATE);
            String array = prefs.getString(Common.Keys.PREF_GALLERY, null);
            if (array != null) {
                //make a arrayList and return results
                gallery = Common.Arr.convertToArray(array);
            }
            //section added to remove a glitch
            if (gallery.size()==1) {
                String value = gallery.get(0);
                Log.e(value, value);
                Bitmap bmpTest = BitmapConversion.stringToImage(value);
                if ( bmpTest == null )  {
                    gallery = new ArrayList<String>();
               }
            }
            //section added to remove a glitch
            return gallery;
        }
    }

    public static class BitmapConversion{
        public static android.graphics.Bitmap stringToImage(String imageString) {
            byte[] decodedByte = Base64.decode(imageString, 0);
            return BitmapFactory.decodeByteArray(decodedByte, 0,
                    decodedByte.length);
        }
        public static String imageToString(android.graphics.Bitmap image) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String encodedImageString = Base64.encodeToString(b, Base64.DEFAULT);
            return encodedImageString;
        }
    }

    public static class Arr{
        public static ArrayList<String> convertToArray(String string) {
            ArrayList<String> list = new ArrayList<String>(Arrays.asList(string.split(",")));
            return list;
        }
        public static Integer[] imagesArr = {
                     android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery,
                     android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery,
                     android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery,
                     android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery,
                     android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery,
                     android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery,
                     android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery,
                     android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery,
                     android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery,
                     android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery,
                     android.R.drawable.ic_menu_gallery
            };
    }

    public static class CustomDialog{
        public static final void listDialog(Context context, String[] list_items, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(list_items, listener);
        if (listener != null) {
        }
        builder.create().show();
        }
    }
}
